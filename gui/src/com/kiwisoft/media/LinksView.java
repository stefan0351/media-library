package com.kiwisoft.media;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.DefaultSortableTableModel;

public class LinksView extends ViewPanel
{
	private Show show;

	private TableController<Link> tableController;
	private CollectionChangeObserver collectionObserver;

	public LinksView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		return show.getTitle()+" - Links";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Link> tableModel=new DefaultSortableTableModel<Link>("name", "language", "url");
		createTableData(tableModel);

		tableController=new TableController<Link>(tableModel, new MediaTableConfiguration("table.links"))
		{
			@Override
			public List<ContextAction<? super Link>> getToolBarActions()
			{
				List<ContextAction<? super Link>> actions=new ArrayList<ContextAction<? super Link>>();
				actions.add(new LinkDetailsAction());
				actions.add(new NewLinkAction(show));
				actions.add(new DeleteLinkAction(frame, show));
				actions.add(new OpenLinkAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super Link>> getContextActions()
			{
				List<ContextAction<? super Link>> actions=new ArrayList<ContextAction<? super Link>>();
				actions.add(new LinkDetailsAction());
				actions.add(null);
				actions.add(new NewLinkAction(show));
				actions.add(new DeleteLinkAction(frame, show));
				actions.add(null);
				actions.add(new OpenLinkAction(frame));
				return actions;
			}

			@Override
			public ContextAction<Link> getDoubleClickAction()
			{
				return new LinkDetailsAction();
			}
		};
		JComponent component=tableController.createComponent();
		tableController.getTable().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK), "new link");
		tableController.getTable().getActionMap().put("new link", new NewLinkAction(show));
		return component;
	}

	private void createTableData(SortableTableModel<Link> tableModel)
	{
		collectionObserver=new CollectionChangeObserver();
		for (Link link : show.getLinks()) tableModel.addRow(new Row(link));
		tableModel.sort();
		show.addCollectionListener(collectionObserver);
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
			if (Show.LINKS.equals(event.getPropertyName()))
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
		Bookmark bookmark=new Bookmark(getName(), LinksView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long id=new Long(bookmark.getParameter("show"));
		Show show=ShowManager.getInstance().getShow(id);
		frame.setCurrentView(new LinksView(show), true);
	}
}