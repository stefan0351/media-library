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

public class PairingsView extends ViewPanel implements Disposable
{
	private SortableTable table;
	private PairingsTableModel tableModel;
	private DoubleClickListener doubleClickListener;
	private UpdateListener updateListener;
	private JScrollPane scrollPane;

	public PairingsView()
	{
	}

	public String getName()
	{
		return "Fan Fiction - Paare";
	}

	public JComponent createContentPanel(ApplicationFrame frame)
	{
		tableModel=new PairingsTableModel();
		for (Pairing pairing : FanFicManager.getInstance().getPairings())
		{
			tableModel.addRow(new Row(pairing));
		}
		tableModel.sort();
		updateListener=new UpdateListener();
		FanFicManager.getInstance().addCollectionChangeListener(updateListener);

		table=new SortableTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(200, 200));
		table.initializeColumns(new MediaTableConfiguration("table.fanfic.pairings"));

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
					wizard.setCurrentView(new FanFicsView((Pairing)row.getUserObject()), true);
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=table.getSelectedRows();
				Pairing pairing=null;
				if (rows.length==1) pairing=tableModel.getObject(rows[0]);
				Set<Pairing> pairings=new HashSet<Pairing>();
				for (int row : rows) pairings.add(tableModel.getObject(row));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new FanFicsAction(PairingsView.this, pairings));
				popupMenu.add(new PropertiesAction(pairing));
				popupMenu.addSeparator();
				popupMenu.add(new NewAction());
				popupMenu.add(new DeleteAction(pairings));
				popupMenu.show(table, e.getX(), e.getY());
				e.consume();
			}
		}
	}

	private class UpdateListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFicManager.PAIRINGS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Pairing newPairing=(Pairing)event.getElement();
						tableModel.addRow(new Row(newPairing));
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class PairingsTableModel extends SortableTableModel<Pairing>
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

	private static class Row extends SortableTableRow<Pairing> implements PropertyChangeListener
	{
		public Row(Pairing pairing)
		{
			super(pairing);
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
				case 0:
					return getUserObject().getName();
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
			PairingDetailsView.create(null);
		}
	}

	private static class PropertiesAction extends AbstractAction
	{
		private Pairing pairing;

		public PropertiesAction(Pairing pairing)
		{
			super("Eigenschaften");
			this.pairing=pairing;
		}

		public void actionPerformed(ActionEvent e)
		{
			PairingDetailsView.create(pairing);
		}
	}

	public class DeleteAction extends AbstractAction
	{
		private Pairing pairing;

		public DeleteAction(Set authors)
		{
			super("Löschen");
			if (authors.size()==1) pairing=(Pairing)authors.iterator().next();
			setEnabled(pairing!=null);
		}

		public void actionPerformed(ActionEvent event)
		{
			if (pairing.isUsed())
			{
				JOptionPane.showMessageDialog(PairingsView.this,
											  "Die Paarung '"+pairing.getName()+"' kann nicht gelöscht werden.",
											  "Meldung",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(PairingsView.this,
													 "Die Paarung '"+pairing.getName()+"' wirklick löschen?",
													 "Löschen?",
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					FanFicManager.getInstance().dropPairing(pairing);
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
							JOptionPane.showMessageDialog(PairingsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(PairingsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		return new Bookmark(getName(), PairingsView.class);
	}

	@SuppressWarnings({"UNUSED_SYMBOL"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new PairingsView(), true);
	}
}
