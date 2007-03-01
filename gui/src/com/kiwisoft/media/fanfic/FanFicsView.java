/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.fanfic;

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
import javax.swing.*;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.DBObject;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class FanFicsView extends ViewPanel
{
	private FanFicGroup group;

	private SortableTable tblFanFics;
	private FanFicsTableModel tmFanFics;
	private DoubleClickListener doubleClickListener;
	private FanFicListener fanFicListener;
	private JScrollPane scrlFanFics;

	public FanFicsView(FanFicGroup group)
	{
		this.group=group;
	}

	public String getName()
	{
		return "Fan Fiction - "+group.getName();
	}

	public JComponent createContentPanel()
	{
		tmFanFics=new FanFicsTableModel();

		tblFanFics=new SortableTable(tmFanFics);
		tblFanFics.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblFanFics.initializeColumns(new MediaTableConfiguration("table.fanfics"));

		scrlFanFics=new JScrollPane(tblFanFics);
		return scrlFanFics;
	}

	public void initializeData()
	{
		fanFicListener=new FanFicListener();
		if (group!=null)
		{
			Iterator it=group.getFanFics().iterator();
			while (it.hasNext())
			{
				FanFic fanFic=(FanFic)it.next();
				tmFanFics.addRow(new FanFicTableRow(fanFic));
			}
			FanFicManager.getInstance().addCollectionChangeListener(fanFicListener);
		}
		tmFanFics.sort();
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblFanFics.addMouseListener(doubleClickListener);
		scrlFanFics.addMouseListener(doubleClickListener);

	}

	protected void removeComponentListeners()
	{
		scrlFanFics.removeMouseListener(doubleClickListener);
		tblFanFics.removeMouseListener(doubleClickListener);
	}

	public void dispose()
	{
		FanFicManager.getInstance().removeCollectionListener(fanFicListener);
		tmFanFics.clear();
		super.dispose();
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblFanFics.rowAtPoint(e.getPoint());
				SortableTableRow row=tmFanFics.getRow(rowIndex);
				if (row!=null) FanFicDetailsView.create((FanFic)row.getUserObject());
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblFanFics.getSelectedRows();
				Set fanFics=new HashSet();
				for (int i=0; i<rows.length; i++) fanFics.add(tmFanFics.getObject(rows[i]));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new NewAction());
				popupMenu.add(new DeleteAction(fanFics));
				popupMenu.show(tblFanFics, e.getX(), e.getY());
				e.consume();
			}
		}
	}

	private class FanFicListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFicManager.FANFICS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						FanFic newFanFic=(FanFic)event.getElement();
						if (group==null || group.contains(newFanFic))
							tmFanFics.addRow(new FanFicTableRow(newFanFic));
						break;
					case CollectionChangeEvent.REMOVED:
					{
						int index=tmFanFics.indexOf(event.getElement());
						if (index>=0) tmFanFics.removeRowAt(index);
					}
					break;
					case CollectionChangeEvent.CHANGED:
						if (group!=null)
						{
							FanFic fanFic=(FanFic)event.getElement();
							int index=tmFanFics.indexOf(fanFic);
							if (group.contains(fanFic))
							{
								if (index<0) tmFanFics.addRow(new FanFicTableRow(fanFic));
							}
							else
							{
								if (index>=0) tmFanFics.removeRowAt(index);
							}
						}
				}
			}
		}
	}

	private static class FanFicsTableModel extends SortableTableModel<FanFicTableRow>
	{
		private static final String[] COLUMNS={"id", "title", "author", "fandom", "pairing"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class FanFicTableRow extends SortableTableRow<FanFic> implements PropertyChangeListener
	{
		public FanFicTableRow(FanFic fanFic)
		{
			super(fanFic);
		}

		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			switch (column)
			{
				case 0: // id
					return getUserObject().getId();
				case 1: // title
					return getUserObject().getTitle();
				case 2: // author
					return StringUtils.formatAsEnumeration(getUserObject().getAuthors());
				case 3: // fandom
					return StringUtils.formatAsEnumeration(getUserObject().getFanDoms());
				case 4: // pairing
					return StringUtils.formatAsEnumeration(getUserObject().getPairings());
			}
			return null;
		}
	}

	private class NewAction extends AbstractAction
	{
		public NewAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			FanFicDetailsView.create(group);
		}
	}

	public class DeleteAction extends AbstractAction
	{
		private FanFic fanFic;

		public DeleteAction(Set fanFics)
		{
			super("Löschen");
			if (fanFics.size()==1) fanFic=(FanFic)fanFics.iterator().next();
			setEnabled(fanFic!=null);
		}

		public void actionPerformed(ActionEvent event)
		{
			if (fanFic.isUsed())
			{
				JOptionPane.showMessageDialog(FanFicsView.this,
											  "Das FanFic '"+fanFic.getTitle()+"' kann nicht gelöscht werden.",
											  "Meldung",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(FanFicsView.this,
													 "Den FanFic '"+fanFic.getTitle()+"' wirklick löschen?",
													 "Löschen?",
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					FanFicManager.getInstance().dropFanFic(fanFic);
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
							JOptionPane.showMessageDialog(FanFicsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(FanFicsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		Bookmark bookmark=new Bookmark(getName(), FanFicsView.class);
		bookmark.setParameter("class", group.getClass().getName());
		bookmark.setParameter("id", String.valueOf(group.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String className=bookmark.getParameter("class");
		Long id=new Long(bookmark.getParameter("id"));
		try
		{
			FanFicGroup group=(FanFicGroup)DBLoader.getInstance().load((Class<DBObject>)Class.forName(className), id);
			frame.setCurrentView(new FanFicsView(group), true);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
}
