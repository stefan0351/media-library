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
import java.util.Set;
import javax.swing.*;

import com.kiwisoft.media.*;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class ShowCastView extends ViewPanel
{
	// Dates Panel
	private SortableTable tblMainCast;
	private CastTableModel tmMainCast;
	private JScrollPane scrlMainCast;
	private SortableTable tblRecurringCast;
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

	public JComponent createContentPanel(ApplicationFrame frame)
	{
		tmMainCast=new CastTableModel();
		tmRecurringCast=new CastTableModel();
		createTableData();

		tblMainCast=new SortableTable(tmMainCast);
		tblMainCast.initializeColumns(new MediaTableConfiguration("table.show.cast.main"));
		scrlMainCast=new JScrollPane(tblMainCast);

		tblRecurringCast=new SortableTable(tmRecurringCast);
		tblRecurringCast.initializeColumns(new MediaTableConfiguration("table.show.cast.recurring"));
		scrlRecurringCast=new JScrollPane(tblRecurringCast);

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Hauptdarsteller:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
																			  new Insets(0, 0, 0, 0), 0, 0));
		row++;
		pnlContent.add(scrlMainCast, new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH,
															new Insets(5, 0, 0, 0), 0, 0));
		row++;
		pnlContent
			.add(new JLabel("Wiederkehrende Darsteller:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
																				  new Insets(10, 0, 0, 0), 0, 0));
		row++;
		pnlContent.add(scrlRecurringCast, new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH,
																 new Insets(5, 0, 0, 0), 0, 0));

		return pnlContent;
	}

	private void createTableData()
	{
		for (CastMember castMember : show.getMainCast()) tmMainCast.addRow(new CastTableRow(castMember));
		tmMainCast.sort();
		for (CastMember castMember : show.getRecurringCast()) tmRecurringCast.addRow(new CastTableRow(castMember));
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
						CastMember newCast=(CastMember)event.getElement();
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
						CastMember newCast=(CastMember)event.getElement();
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
						if (row!=null) CastDetailsView.create((CastMember)row.getUserObject());
					}
					e.consume();
				}
				if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
				{
					int[] rows=tblMainCast.getSelectedRows();
					Set<CastMember> casts=new HashSet<CastMember>();
					for (int row : rows) casts.add(tmMainCast.getObject(row));
					JPopupMenu popupMenu=new JPopupMenu();
					popupMenu.add(new NewAction(CastMember.MAIN_CAST));
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
						if (row!=null) CastDetailsView.create((CastMember)row.getUserObject());
					}
					e.consume();
				}
				if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
				{
					int[] rows=tblRecurringCast.getSelectedRows();
					Set<CastMember> casts=new HashSet<CastMember>();
					for (int row : rows) casts.add(tmRecurringCast.getObject(row));
					JPopupMenu popupMenu=new JPopupMenu();
					popupMenu.add(new NewAction(CastMember.RECURRING_CAST));
					popupMenu.add(new DeleteAction(casts));
					popupMenu.show(tblRecurringCast, e.getX(), e.getY());
					e.consume();
				}
			}
		}
	}

	private static class CastTableModel extends SortableTableModel<CastMember>
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

	private static class CastTableRow extends SortableTableRow<CastMember> implements PropertyChangeListener
	{
		public CastTableRow(CastMember cast)
		{
			super(cast);
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
			CastMember cast=getUserObject();
			switch (column)
			{
				case 0:
					return cast.getCharacterName();
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
		private Collection<CastMember> casts;

		public DeleteAction(Collection<CastMember> casts)
		{
			super("Löschen");
			this.casts=casts;
			setEnabled(!casts.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			for (CastMember cast : casts)
			{
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
					for (CastMember cast : casts) show.dropCast(cast);
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