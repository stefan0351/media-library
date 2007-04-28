/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.video;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class VideosView extends ViewPanel
{
	private VideoListener videoListener;
	private MediumType type;
	private TableController<Video> tableController;

	public VideosView(MediumType type)
	{
		this.type=type;
	}

	public String getName()
	{
		return type.getPluralName();
	}

	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		VideosTableModel tableModel=new VideosTableModel(type);
		tableController=new TableController<Video>(tableModel, new MediaTableConfiguration("table.videos"))
		{
			public List<ContextAction<? super Video>> getToolBarActions()
			{
				List<ContextAction<? super Video>> actions=new ArrayList<ContextAction<? super Video>>();
				actions.add(new VideoDetailsAction());
				actions.add(new NewVideoAction(type));
				actions.add(new DeleteVideoAction(frame));
				actions.add(new VideoRecordingsAction(frame));
				return actions;
			}

			public List<ContextAction<? super Video>> getContextActions()
			{
				List<ContextAction<? super Video>> actions=new ArrayList<ContextAction<? super Video>>();
				actions.add(new VideoDetailsAction());
				actions.add(null);
				actions.add(new NewVideoAction(type));
				actions.add(new DeleteVideoAction(frame));
				actions.add(null);
				actions.add(new SetVideoObsoleteAction(frame));
				actions.add(new SetVideoActiveAction(frame));
				actions.add(null);
				actions.add(new VideoRecordingsAction(frame));
				return actions;
			}

			public ContextAction<Video> getDoubleClickAction()
			{
				return new VideoDetailsAction();
			}
		};

		videoListener=new VideoListener();
		VideoManager.getInstance().addCollectionChangeListener(videoListener);

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
		VideoManager.getInstance().removeCollectionListener(videoListener);
		tableController.dispose();
		super.dispose();
	}

	private class VideoListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (VideoManager.VIDEOS.equals(event.getPropertyName()))
			{
				SortableTableModel<Video> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Video newVideo=(Video) event.getElement();
						if (newVideo.getType()==type)
						{
							VideosTableModel.Row row=new VideosTableModel.Row(newVideo);
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
						Video video=(Video) event.getElement();
						int index=tableModel.indexOf(video);
						if (video.getType()==type)
						{
							if (index<0) tableModel.addRow(new VideosTableModel.Row(video));
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
		Bookmark bookmark=new Bookmark(getName(), VideosView.class);
		bookmark.setParameter("mediumType", String.valueOf(type.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long typeId=new Long(bookmark.getParameter("mediumType"));
		MediumType type=MediumType.get(typeId);
		frame.setCurrentView(new VideosView(type), true);
	}

}
