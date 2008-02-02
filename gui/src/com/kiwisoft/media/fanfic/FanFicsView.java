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

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBObject;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;

public class FanFicsView extends ViewPanel
{
	private FanFicGroup group;

	private FanFicListener fanFicListener;
	private TableController<FanFic> tableController;

	public FanFicsView(FanFicGroup group)
	{
		this.group=group;
	}

	public String getTitle()
	{
		return "Fan Fiction - "+group.getFanFicGroupName();
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<FanFic> tmFanFics=new DefaultSortableTableModel<FanFic>("id", "title", "author", "fandom", "pairing");

		tableController=new TableController<FanFic>(tmFanFics, new DefaultTableConfiguration(FanFicsView.class, "fanfics"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new FanFicDetailsAction());
				actions.add(new NewFanFicAction(group));
				actions.add(new DeleteFanFicAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new FanFicDetailsAction());
				actions.add(null);
				actions.add(new NewFanFicAction(group));
				actions.add(new DeleteFanFicAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new FanFicDetailsAction();
			}
		};
		return tableController.createComponent();
	}

	public void initializeData()
	{
		SortableTableModel<FanFic> tableModel=tableController.getModel();
		fanFicListener=new FanFicListener();
		if (group!=null)
		{
			for (FanFic fanFic : group.getFanFics())
			{
				tableModel.addRow(new FanFicTableRow(fanFic));
			}
			FanFicManager.getInstance().addCollectionChangeListener(fanFicListener);
		}
		tableModel.sort();
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
		FanFicManager.getInstance().removeCollectionListener(fanFicListener);
		tableController.dispose();
		super.dispose();
	}

	private class FanFicListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFicManager.FANFICS.equals(event.getPropertyName()))
			{
				SortableTableModel<FanFic> tmFanFics=tableController.getModel();
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

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), FanFicsView.class);
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
