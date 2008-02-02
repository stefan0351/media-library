package com.kiwisoft.media.links;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;

public class OldLinksView extends ViewPanel
{
	private Show show;

	private TableController<Link> tableController;
	private CollectionChangeObserver collectionObserver;

	public OldLinksView(Show show)
	{
		this.show=show;
	}

	public String getTitle()
	{
		return show.getTitle()+" - Links";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Link> tableModel=new DefaultSortableTableModel<Link>("name", "language", "url");
		createTableData(tableModel);

		tableController=new TableController<Link>(tableModel, new DefaultTableConfiguration(OldLinksView.class, "links"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new LinkDetailsAction());
				actions.add(new NewLinkAction());
				actions.add(new DeleteLinkAction(frame));
				actions.add(new OpenLinkAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new LinkDetailsAction());
				actions.add(null);
				actions.add(new NewLinkAction());
				actions.add(new DeleteLinkAction(frame));
				actions.add(null);
				actions.add(new OpenLinkAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new LinkDetailsAction();
			}
		};
		return tableController.createComponent();
	}

	private void createTableData(SortableTableModel<Link> tableModel)
	{
		collectionObserver=new CollectionChangeObserver();
		LinkGroup linkGroup=show.getLinkGroup();
		for (Link link : linkGroup.getLinks()) tableModel.addRow(new Row(link));
		tableModel.sort();
		linkGroup.addCollectionListener(collectionObserver);
	}

	protected void installComponentListeners()
	{
		tableController.installListeners();
		super.installComponentListeners();
	}

	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		super.removeComponentListeners();
	}

	public void dispose()
	{
		show.removeCollectionListener(collectionObserver);
		tableController.dispose();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (LinkGroup.LINKS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Link newLink=(Link)event.getElement();
						Row row=new Row(newLink);
						int newIndex=tableController.getModel().addRow(row);
						tableController.getModel().sort();
						SortableTable table=tableController.getTable();
						table.getSelectionModel().setSelectionInterval(newIndex, newIndex);
						table.scrollRectToVisible(table.getCellRect(newIndex, 0, false));
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableController.getModel().indexOf(event.getElement());
						if (index>=0) tableController.getModel().removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class Row extends SortableTableRow<Link> implements PropertyChangeListener
	{
		public Row(Link link)
		{
			super(link);
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
			if ("name".equals(property))
				return getUserObject().getName();
			else if ("url".equals(property))
				return getUserObject().getUrl();
			else if ("language".equals(property))
				return getUserObject().getLanguage();
			else
				return "";
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), OldLinksView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long id=new Long(bookmark.getParameter("show"));
		Show show=ShowManager.getInstance().getShow(id);
		frame.setCurrentView(new OldLinksView(show), true);
	}
}