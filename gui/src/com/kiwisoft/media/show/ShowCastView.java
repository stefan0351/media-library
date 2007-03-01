package com.kiwisoft.media.show;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.kiwisoft.media.Cast;
import com.kiwisoft.media.Person;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.ShowCharacter;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.CastDetailsView;
import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;

public class ShowCastView extends ViewPanel
{
	// Dates Panel
	private DynamicTable tblMainCast;
	private CastTableModel tmMainCast;
	private JScrollPane scrlMainCast;
	private DynamicTable tblRecurringCast;
	private CastTableModel tmRecurringCast;
	private JScrollPane scrlRecurringCast;
	private DoubleClickListener doubleClickListener;
	private Show show;
	private CollectionChangeObserver collectionObserver;

	public ShowCastView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		return show.getName()+" - Darsteller";
	}

	public JComponent createContentPanel()
	{
		tmMainCast=new CastTableModel();
		tmRecurringCast=new CastTableModel();
		createTableData();

		tblMainCast=new DynamicTable(tmMainCast);
		tblMainCast.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.show.cast.main"));
		scrlMainCast=new JScrollPane(tblMainCast);

		tblRecurringCast=new DynamicTable(tmRecurringCast);
		tblRecurringCast.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.show.cast.recurring"));
		scrlRecurringCast=new JScrollPane(tblRecurringCast);

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Hauptdarsteller:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		row++;
		pnlContent.add(scrlMainCast, new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 0, 0, 0), 0, 0));
		row++;
		pnlContent.add(new JLabel("Wiederkehrende Darsteller:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(10, 0, 0, 0), 0, 0));
		row++;
		pnlContent.add(scrlRecurringCast, new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(5, 0, 0, 0), 0, 0));

		return pnlContent;
	}

	private void createTableData()
	{
		Iterator it=show.getMainCast().iterator();
		while (it.hasNext())
		{
			Cast cast=(Cast)it.next();
			tmMainCast.addRow(new CastTableRow(cast));
		}
		tmMainCast.sort();
		it=show.getRecurringCast().iterator();
		while (it.hasNext())
		{
			Cast cast=(Cast)it.next();
			tmRecurringCast.addRow(new CastTableRow(cast));
		}
		tmRecurringCast.sort();

		collectionObserver=new CollectionChangeObserver();
		show.addCollectionChangeListener(collectionObserver);
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblMainCast.addMouseListener(doubleClickListener);
		scrlMainCast.addMouseListener(doubleClickListener);
		tblRecurringCast.addMouseListener(doubleClickListener);
		scrlRecurringCast.addMouseListener(doubleClickListener);
		super.installComponentListener();
	}

	protected void removeComponentListeners()
	{
		tblMainCast.removeMouseListener(doubleClickListener);
		scrlMainCast.removeMouseListener(doubleClickListener);
		tblRecurringCast.removeMouseListener(doubleClickListener);
		scrlRecurringCast.removeMouseListener(doubleClickListener);
		super.removeComponentListeners();
	}

	public void dispose()
	{
		show.removeCollectionListener(collectionObserver);
		tmMainCast.clear();
		tmRecurringCast.clear();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (Show.MAIN_CAST.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Cast newCast=(Cast)event.getElement();
						CastTableRow row=new CastTableRow(newCast);
						tmMainCast.addRow(row);
						tmMainCast.sort();
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmMainCast.indexOf(event.getElement());
						if (index>=0) tmMainCast.removeRowAt(index);
						break;
				}
			}
			else if (Show.RECURRING_CAST.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Cast newCast=(Cast)event.getElement();
						CastTableRow row=new CastTableRow(newCast);
						tmRecurringCast.addRow(row);
						tmRecurringCast.sort();
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmRecurringCast.indexOf(event.getElement());
						if (index>=0) tmRecurringCast.removeRowAt(index);
						break;
				}
			}
		}
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getSource()==tblMainCast || e.getSource()==scrlMainCast)
			{
				if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
				{
					int rowIndex=tblMainCast.rowAtPoint(e.getPoint());
					if (rowIndex>=0)
					{
						SortableTableRow row=tmMainCast.getRow(rowIndex);
						if (row!=null) CastDetailsView.create((Cast)row.getUserObject());
					}
					e.consume();
				}
				if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
				{
					int[] rows=tblMainCast.getSelectedRows();
					Set casts=new HashSet();
					for (int i=0; i<rows.length; i++) casts.add(tmMainCast.getObject(rows[i]));
					JPopupMenu popupMenu=new JPopupMenu();
					popupMenu.add(new NewAction(Cast.MAIN_CAST));
					popupMenu.add(new DeleteAction(casts));
					popupMenu.show(tblMainCast, e.getX(), e.getY());
					e.consume();
				}
			}
			else if (e.getSource()==tblRecurringCast || e.getSource()==scrlRecurringCast)
			{
				if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
				{
					int rowIndex=tblRecurringCast.rowAtPoint(e.getPoint());
					if (rowIndex>=0)
					{
						SortableTableRow row=tmRecurringCast.getRow(rowIndex);
						if (row!=null) CastDetailsView.create((Cast)row.getUserObject());
					}
					e.consume();
				}
				if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
				{
					int[] rows=tblRecurringCast.getSelectedRows();
					Set casts=new HashSet();
					for (int i=0; i<rows.length; i++) casts.add(tmRecurringCast.getObject(rows[i]));
					JPopupMenu popupMenu=new JPopupMenu();
					popupMenu.add(new NewAction(Cast.RECURRING_CAST));
					popupMenu.add(new DeleteAction(casts));
					popupMenu.show(tblRecurringCast, e.getX(), e.getY());
					e.consume();
				}
			}
		}
	}

	private static class CastTableModel extends SortableTableModel
	{
		private static final String[] COLUMNS={"character", "actor", "voice"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private class CastTableRow extends SortableTableRow implements PropertyChangeListener
	{
		public CastTableRow(Cast cast)
		{
			super(cast);
		}

		public void installListener()
		{
			((Cast)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((Cast)getUserObject()).removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			Cast cast=(Cast)getUserObject();
			switch (column)
			{
				case 0:
					ShowCharacter character=cast.getCharacter();
					if (character!=null) return character.getName();
					return null;
				case 1:
					Person actor=cast.getActor();
					if (actor!=null) return actor.getName();
					return null;
				case 2:
					return cast.getVoice();
			}
			return null;
		}
	}

	private class NewAction extends AbstractAction
	{
		private int castType;

		public NewAction(int castType)
		{
			super("Neu");
			this.castType=castType;
		}

		public void actionPerformed(ActionEvent e)
		{
			CastDetailsView.create(show, castType);
		}
	}

	private class DeleteAction extends AbstractAction
	{
		private Collection casts;

		public DeleteAction(Collection casts)
		{
			super("Löschen");
			this.casts=casts;
			setEnabled(!casts.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			Iterator it=casts.iterator();
			while (it.hasNext())
			{
				Cast cast=(Cast)it.next();
				if (cast.isUsed())
				{
					JOptionPane.showMessageDialog(ShowCastView.this, "Der Darsteller '"+cast+"' kann nicht gelöscht werden.", "Meldung",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			int option=JOptionPane.showConfirmDialog(ShowCastView.this, "Darsteller wirklick löschen?", "Löschen?", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					it=casts.iterator();
					while (it.hasNext())
					{
						Cast cast=(Cast)it.next();
						show.dropCast(cast);
					}
					transaction.close();
				}
				catch (Exception e1)
				{
					try
					{
						if (transaction!=null) transaction.rollback();
					}
					catch (SQLException e2)
					{
						e2.printStackTrace();
					}
					e1.printStackTrace();
					JOptionPane.showMessageDialog(ShowCastView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		Bookmark bookmark=new Bookmark(getName(), ShowCastView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Show show=ShowManager.getInstance().getShow(new Long(bookmark.getParameter("show")));
		frame.setCurrentView(new ShowCastView(show), true);
	}
}