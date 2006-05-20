/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.ui.fanfic;

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

import com.kiwisoft.media.fanfic.Author;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.ui.MediaManagerFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.Disposable;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/08/28 21:15:40 $
 */
public class AuthorsView extends ViewPanel implements Disposable
{
	private DynamicTable table;
	private AuthorsTableModel tableModel;
	private DoubleClickListener doubleClickListener;
	private UpdateListener updateListener;
	private JScrollPane scrollPane;

	public AuthorsView()
	{
	}

	public String getName()
	{
		return "Fan Fiction - Autoren";
	}

	public JComponent createContentPanel()
	{
		tableModel=new AuthorsTableModel();
		for (Author author : FanFicManager.getInstance().getAuthors()) tableModel.addRow(new Row(author));
		tableModel.sort();
		updateListener=new UpdateListener();
		FanFicManager.getInstance().addCollectionChangeListener(updateListener);

		table=new DynamicTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(200, 200));
		table.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.fanfic.authors"));

		scrollPane=new JScrollPane(table);
		return scrollPane;
	}

	public void installComponentListener()
	{
		super.installComponentListener();
		doubleClickListener=new DoubleClickListener();
		table.addMouseListener(doubleClickListener);
		scrollPane.addMouseListener(doubleClickListener);
	}

	public void removeComponentListeners()
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
					wizard.setCurrentView(new FanFicsView((Author)row.getUserObject()), true);
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=table.getSelectedRows();
				Author author=null;
				if (rows.length==1) author=(Author)tableModel.getObject(rows[0]);
				Set<Author> authors=new HashSet<Author>();
				for (int i=0; i<rows.length; i++) authors.add((Author)tableModel.getObject(rows[i]));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new FanFicsAction(AuthorsView.this, authors));
				popupMenu.add(new PropertiesAction(author));
				popupMenu.addSeparator();
				popupMenu.add(new NewAction());
				popupMenu.add(new DeleteAction(authors));
				popupMenu.show(table, e.getX(), e.getY());
				e.consume();
			}
		}
	}

	private class UpdateListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFicManager.AUTHORS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						{
							Author newAuthor=(Author)event.getElement();
							int index=tableModel.addRow(new Row(newAuthor));
							table.getSelectionModel().setSelectionInterval(index, index);
						}
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class AuthorsTableModel extends SortableTableModel<Row>
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

	private static class Row extends SortableTableRow<Author> implements PropertyChangeListener
	{
		public Row(Author author)
		{
			super(author);
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

	private class NewAction extends AbstractAction
	{
		public NewAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			AuthorDetailsView.create(null);
		}
	}

	private class PropertiesAction extends AbstractAction
	{
		private Author author;

		public PropertiesAction(Author author)
		{
			super("Eigenschaften");
			this.author=author;
		}

		public void actionPerformed(ActionEvent e)
		{
			AuthorDetailsView.create(author);
		}
	}

	private class DeleteAction extends AbstractAction
	{
		private Author author;

		public DeleteAction(Set authors)
		{
			super("Löschen");
			if (authors.size()==1) author=(Author)authors.iterator().next();
			setEnabled(author!=null);
		}

		public void actionPerformed(ActionEvent event)
		{
			if (author.isUsed())
			{
				JOptionPane.showMessageDialog(AuthorsView.this,
						"Der Autor '"+author.getName()+"' kann nicht gelöscht werden.",
						"Meldung",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(AuthorsView.this,
					"Den Autor '"+author.getName()+"' wirklick löschen?",
					"Löschen?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					FanFicManager.getInstance().dropAuthor(author);
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
							JOptionPane.showMessageDialog(AuthorsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(AuthorsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		return new Bookmark(getName(), AuthorsView.class);
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		assert bookmark!=null;
		frame.setCurrentView(new AuthorsView(), true);
	}
}
