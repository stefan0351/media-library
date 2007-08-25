/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.medium;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MediaView extends ViewPanel
{
	private MediumListener mediumListener;
	private MediumType type;
	private TableController<Medium> tableController;

	public MediaView(MediumType type)
	{
		this.type=type;
	}

	public String getTitle()
	{
		return type.getPluralName();
	}

	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		MediaTableModel tableModel=new MediaTableModel(type);
		tableController=new TableController<Medium>(tableModel, new DefaultTableConfiguration(MediaTableModel.class))
		{
			public List<ContextAction<? super Medium>> getToolBarActions()
			{
				List<ContextAction<? super Medium>> actions=new ArrayList<ContextAction<? super Medium>>();
				actions.add(new MediumDetailsAction());
				actions.add(new NewMediumAction(type));
				actions.add(new DeleteMediumAction(frame));
				actions.add(new TracksAction(frame));
				return actions;
			}

			public List<ContextAction<? super Medium>> getContextActions()
			{
				List<ContextAction<? super Medium>> actions=new ArrayList<ContextAction<? super Medium>>();
				actions.add(new MediumDetailsAction());
				actions.add(null);
				actions.add(new NewMediumAction(type));
				actions.add(new DeleteMediumAction(frame));
				actions.add(null);
				actions.add(new SetMediumObsoleteAction(frame));
				actions.add(new SetMediumActiveAction(frame));
				actions.add(null);
				actions.add(new TracksAction(frame));
				return actions;
			}

			public ContextAction<Medium> getDoubleClickAction()
			{
				return new MediumDetailsAction();
			}
		};

		mediumListener=new MediumListener();
		MediumManager.getInstance().addCollectionChangeListener(mediumListener);

		return tableController.createComponent();
	}

	protected void installComponentListeners()
	{
		tableController.installListeners();
	}

	protected void removeComponentListeners()
	{
		tableController.removeListeners();
	}

	public void dispose()
	{
		MediumManager.getInstance().removeCollectionListener(mediumListener);
		tableController.dispose();
		super.dispose();
	}

	private class MediumListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (MediumManager.MEDIA.equals(event.getPropertyName()))
			{
				SortableTableModel<Medium> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Medium newMedium=(Medium) event.getElement();
						if (newMedium.getType()==type)
						{
							MediaTableModel.Row row=new MediaTableModel.Row(newMedium);
							tableModel.addRow(row);
						}
						break;
					case CollectionChangeEvent.REMOVED:
					{
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
					}
					break;
					case CollectionChangeEvent.CHANGED:
					{
						Medium medium=(Medium) event.getElement();
						int index=tableModel.indexOf(medium);
						if (medium.getType()==type)
						{
							if (index<0) tableModel.addRow(new MediaTableModel.Row(medium));
						}
						else
						{
							if (index>=0) tableModel.removeRowAt(index);
						}
					}
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
		Bookmark bookmark=new Bookmark(getTitle(), MediaView.class);
		bookmark.setParameter("mediumType", String.valueOf(type.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long typeId=new Long(bookmark.getParameter("mediumType"));
		MediumType type=MediumType.get(typeId);
		frame.setCurrentView(new MediaView(type), true);
	}

}
