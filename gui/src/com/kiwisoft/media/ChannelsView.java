/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.kiwisoft.media.Channel;
import com.kiwisoft.media.ChannelManager;
import com.kiwisoft.media.ChannelDetailsView;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.utils.gui.ApplicationFrame;

public class ChannelsView extends ViewPanel
{
	private DynamicTable tblChannels;
	private ChannelsTableModel tmChannels;
	private DoubleClickListener doubleClickListener;
	private ChannelListener channelListener;

	public ChannelsView()
	{
	}

	public String getName()
	{
		return "Sender";
	}

	public JComponent createContentPanel()
	{
		tmChannels=new ChannelsTableModel();
		Iterator it=ChannelManager.getInstance().getChannels().iterator();
		while (it.hasNext())
		{
			Channel video=(Channel)it.next();
			tmChannels.addRow(new ChannelTableRow(video));
		}
		tmChannels.sort();
		channelListener=new ChannelListener();
		ChannelManager.getInstance().addCollectionChangeListener(channelListener);

		tblChannels=new DynamicTable(tmChannels);
		tblChannels.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblChannels.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.channels"));

		return new JScrollPane(tblChannels);
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblChannels.addMouseListener(doubleClickListener);
	}

	protected void removeComponentListeners()
	{
		tblChannels.removeMouseListener(doubleClickListener);
	}

	public void dispose()
	{
		ChannelManager.getInstance().removeCollectionListener(channelListener);
		tmChannels.clear();
		super.dispose();
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblChannels.rowAtPoint(e.getPoint());
				SortableTableRow row=tmChannels.getRow(rowIndex);
				if (row!=null) ChannelDetailsView.create((Channel)row.getUserObject());
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblChannels.getSelectedRows();
				Set channels=new HashSet();
				for (int i=0; i<rows.length; i++) channels.add(tmChannels.getObject(rows[i]));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new NewChannelAction());
				popupMenu.add(new DeleteChannelAction(channels));
				popupMenu.show(tblChannels, e.getX(), e.getY());
				e.consume();
			}
		}
	}

	private class ChannelListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (ChannelManager.CHANNELS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Channel newChannel=(Channel)event.getElement();
						ChannelTableRow row=new ChannelTableRow(newChannel);
						tmChannels.addRow(row);
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmChannels.indexOf(event.getElement());
						if (index>=0) tmChannels.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class ChannelsTableModel extends SortableTableModel
	{
		private static final String[] COLUMNS={"name", "receiving"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class ChannelTableRow extends SortableTableRow implements PropertyChangeListener
	{
		public ChannelTableRow(Channel channel)
		{
			super(channel);
		}

		public void installListener()
		{
			((Channel)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((Channel)getUserObject()).removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			switch (column)
			{
				case 0:
					return ((Channel)getUserObject()).getName();
				case 1:
					return new Boolean(((Channel)getUserObject()).isReceivable());
			}
			return "";
		}
	}

	private class NewChannelAction extends AbstractAction
	{
		public NewChannelAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			ChannelDetailsView.create(null);
		}
	}

	public class DeleteChannelAction extends AbstractAction
	{
		private Channel channel;

		public DeleteChannelAction(Set channels)
		{
			super("Löschen");
			if (channels.size()==1) channel=(Channel)channels.iterator().next();
			setEnabled(channel!=null);
		}

		public void actionPerformed(ActionEvent event)
		{
			if (channel.isUsed())
			{
				JOptionPane.showMessageDialog(ChannelsView.this,
				        "Der Sender '"+channel.getName()+"' kann nicht gelöscht werden.",
				        "Meldung",
				        JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(ChannelsView.this,
			        "Den Sender '"+channel.getName()+"' wirklick löschen?",
			        "Löschen?",
			        JOptionPane.YES_NO_OPTION,
			        JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					ChannelManager.getInstance().dropChannel(channel);
					transaction.close();
				}
				catch (Exception e)
				{
					if (transaction!=null)
					{
						try
						{
							transaction.rollback();
						}
						catch (SQLException e1)
						{
							e1.printStackTrace();
							JOptionPane.showMessageDialog(ChannelsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(ChannelsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		return new Bookmark(getName(), ChannelsView.class);
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new ChannelsView(), true);
	}
}
