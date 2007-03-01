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
import java.util.Set;
import javax.swing.*;

import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Disposable;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/08/28 21:20:14 $
 */
public class FanDomsView extends ViewPanel implements Disposable
{
	private SortableTable table;
	private FanDomainsTableModel tableModel;
	private DoubleClickListener doubleClickListener;
	private UpdateListener updateListener;
	private JScrollPane scrollPane;

	public FanDomsView()
	{
	}

	public String getName()
	{
		return "Fan Fiction - Domänen";
	}

	public JComponent createContentPanel()
	{
		tableModel=new FanDomainsTableModel();
		for (FanDom domain : FanFicManager.getInstance().getDomains()) tableModel.addRow(new Row(domain));
		tableModel.sort();
		updateListener=new UpdateListener();
		FanFicManager.getInstance().addCollectionChangeListener(updateListener);

		table=new SortableTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(200, 200));
		table.initializeColumns(new MediaTableConfiguration("table.fanfic.domains"));

		scrollPane=new JScrollPane(table);
		return scrollPane;
	}

	protected void installComponentListener()
	{
		super.installComponentListener();
		doubleClickListener=new DoubleClickListener();
		table.addMouseListener(doubleClickListener);
		scrollPane.addMouseListener(doubleClickListener);
	}

	protected void removeComponentListeners()
	{
		super.removeComponentListeners();
		table.removeMouseListener(doubleClickListener);
		scrollPane.removeMouseListener(doubleClickListener);
	}

	public void dispose()
	{
		FanFicManager.getInstance().removeCollectionListener(updateListener);
		tableModel.clear();
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=table.rowAtPoint(e.getPoint());
				SortableTableRow row=tableModel.getRow(rowIndex);
				if (row!=null)
				{
					MediaManagerFrame wizard=(MediaManagerFrame)getTopLevelAncestor();
					wizard.setCurrentView(new FanFicsView((FanDom)row.getUserObject()), true);
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=table.getSelectedRows();
				FanDom domain=null;
				if (rows.length==1) domain=(FanDom)tableModel.getObject(rows[0]);
				Set<FanDom> domains=new HashSet<FanDom>();
				for (int i=0; i<rows.length; i++) domains.add((FanDom)tableModel.getObject(rows[i]));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new FanFicsAction(FanDomsView.this, domains));
				popupMenu.add(new PropertiesAction(domain));
				popupMenu.addSeparator();
				popupMenu.add(new NewAction());
				popupMenu.add(new DeleteAction(domains));
				popupMenu.show(table, e.getX(), e.getY());
				e.consume();
			}
		}
	}

	private class UpdateListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFicManager.DOMAINS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						FanDom newDomain=(FanDom)event.getElement();
						tableModel.addRow(new Row(newDomain));
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class FanDomainsTableModel extends SortableTableModel<Row>
	{
		private static final String[] COLUMNS={"name"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class Row extends SortableTableRow implements PropertyChangeListener
	{
		public Row(FanDom domain)
		{
			super(domain);
		}

		public void installListener()
		{
			((FanDom)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((FanDom)getUserObject()).removePropertyChangeListener(this);
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
					return ((FanDom)getUserObject()).getName();
			}
			return null;
		}
	}

	private static class NewAction extends AbstractAction
	{
		public NewAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			FanDomDetailsView.create(null);
		}
	}

	private static class PropertiesAction extends AbstractAction
	{
		private FanDom fanDom;

		public PropertiesAction(FanDom fanDom)
		{
			super("Eigenschaften");
			this.fanDom=fanDom;
			setEnabled(fanDom!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			FanDomDetailsView.create(fanDom);
		}
	}

	public class DeleteAction extends AbstractAction
	{
		private FanDom domain;

		public DeleteAction(Set<FanDom> domains)
		{
			super("Löschen");
			if (domains.size()==1) domain=domains.iterator().next();
			setEnabled(domain!=null);
		}

		public void actionPerformed(ActionEvent event)
		{
			if (domain.isUsed())
			{
				JOptionPane.showMessageDialog(FanDomsView.this,
											  "Die Fan Domain '"+domain.getName()+"' kann nicht gelöscht werden.",
											  "Meldung",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(FanDomsView.this,
													 "Die Fan Domain '"+domain.getName()+"' wirklick löschen?",
													 "Löschen?",
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					FanFicManager.getInstance().dropDomain(domain);
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
							JOptionPane.showMessageDialog(FanDomsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(FanDomsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		return new Bookmark(getName(), FanDomsView.class);
	}

	@SuppressWarnings({"UNUSED_SYMBOL"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new FanDomsView(), true);
	}
}
