/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.fanfic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Disposable;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/08/28 21:15:40 $
 */
public class AuthorsView extends ViewPanel implements Disposable
{
	private UpdateListener updateListener;
	private TableController<Author> tableController;

	public AuthorsView()
	{
	}

	public String getName()
	{
		return "Fan Fiction - Authors";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		AuthorsTableModel tableModel=new AuthorsTableModel();
		for (Author author : FanFicManager.getInstance().getAuthors()) tableModel.addRow(new Row(author));
		tableModel.sort();
		updateListener=new UpdateListener();
		FanFicManager.getInstance().addCollectionChangeListener(updateListener);

		tableController=new TableController<Author>(tableModel, new MediaTableConfiguration("table.fanfic.authors"))
		{
			@Override
			public List<ContextAction<? super Author>> getToolBarActions()
			{
				List<ContextAction<? super Author>> actions=new ArrayList<ContextAction<? super Author>>();
				actions.add(new AuthorDetailsAction());
				actions.add(new NewAuthorAction());
				actions.add(new DeleteAuthorAction(frame));
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super Author>> getContextActions()
			{
				List<ContextAction<? super Author>> actions=new ArrayList<ContextAction<? super Author>>();
				actions.add(new AuthorDetailsAction());
				actions.add(null);
				actions.add(new NewAuthorAction());
				actions.add(new DeleteAuthorAction(frame));
				actions.add(null);
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public ContextAction<Author> getDoubleClickAction()
			{
				return new FanFicsAction<Author>(frame);
			}
		};
		return tableController.createComponent();
	}

	public void installComponentListeners()
	{
		super.installComponentListeners();
		tableController.installListeners();
	}

	public void removeComponentListeners()
	{
		super.removeComponentListeners();
		tableController.removeListeners();
	}

	public void dispose()
	{
		FanFicManager.getInstance().removeCollectionListener(updateListener);
		tableController.dispose();
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
						int index=tableController.getModel().addRow(new Row(newAuthor));
						tableController.getTable().getSelectionModel().setSelectionInterval(index, index);
					}
					break;
					case CollectionChangeEvent.REMOVED:
						int index=tableController.getModel().indexOf(event.getElement());
						if (index>=0) tableController.getModel().removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class AuthorsTableModel extends SortableTableModel<Author>
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
