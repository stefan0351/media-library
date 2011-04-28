package com.kiwisoft.media.fanfic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.DefaultSortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;

/**
 * @author Stefan Stiller
 */
public class AuthorsView extends ViewPanel implements Disposable
{
	private TableController<Author> tableController;

	public AuthorsView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Fan Fiction - Authors";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Author> tableModel=new DefaultSortableTableModel<Author>("name");
		for (Author author : FanFicManager.getInstance().getAuthors()) tableModel.addRow(new Row(author));
		tableModel.sort();
		getModelListenerList().installPropertyChangeListener(FanFicManager.getInstance(), new UpdateListener());

		tableController=new TableController<Author>(tableModel, new DefaultTableConfiguration("authors.list", AuthorsView.class, "authors"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new AuthorDetailsAction());
				actions.add(new NewAuthorAction());
				actions.add(new DeleteAuthorAction(frame));
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new AuthorDetailsAction());
				actions.add(null);
				actions.add(new NewAuthorAction());
				actions.add(new DeleteAuthorAction(frame));
				actions.add(null);
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new FanFicsAction(frame);
			}
		};
		return tableController.getComponent();
	}

	@Override
	public void installComponentListeners()
	{
		super.installComponentListeners();
		tableController.installListeners();
	}

	@Override
	public void removeComponentListeners()
	{
		super.removeComponentListeners();
		tableController.removeListeners();
	}

	@Override
	public void dispose()
	{
		tableController.dispose();
		super.dispose();
	}

	private class UpdateListener extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (FanFicManager.AUTHORS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionPropertyChangeEvent.ADDED:
					{
						Author newAuthor=(Author)event.getElement();
						int index=tableController.getModel().addRow(new Row(newAuthor));
						tableController.getTable().getSelectionModel().setSelectionInterval(index, index);
					}
					break;
					case CollectionPropertyChangeEvent.REMOVED:
						int index=tableController.getModel().indexOf(event.getElement());
						if (index>=0) tableController.getModel().removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class Row extends SortableTableRow<Author> implements PropertyChangeListener
	{
		public Row(Author author)
		{
			super(author);
		}

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		@Override
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

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		return new Bookmark(getTitle(), AuthorsView.class);
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		assert bookmark!=null;
		frame.setCurrentView(new AuthorsView());
	}
}
