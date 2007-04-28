package com.kiwisoft.media.photos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.kiwisoft.media.utils.GuiUtils;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.actions.ComplexAction;

public class PhotosView extends ViewPanel
{
	private PhotoBook photoBook;
	private ThumbnailPanel thumbnailPanel;
	private List<ContextAction<? super Photo>> toolBarActions;

	public PhotosView(PhotoBook photoBook)
	{
		this.photoBook=photoBook;
	}

	public String getName()
	{
		return "Photos - "+photoBook.getName();
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		thumbnailPanel=new ThumbnailPanel();

		JPanel panel=new JPanel(new BorderLayout());
		panel.add(createToolBar(frame), BorderLayout.NORTH);
		panel.add(new JScrollPane(thumbnailPanel), BorderLayout.CENTER);

		getModelListenerList().installCollectionListener(photoBook, new PhotoBookCollectionListener());
		thumbnailPanel.addListSelectionListener(new SelectionListener());

		return panel;

	}

	@Override
	protected void initializeData()
	{
		for (Photo photo : photoBook.getPhotos()) addThumbnail(photo);
	}

	protected JToolBar createToolBar(ApplicationFrame frame)
	{
		toolBarActions=new ArrayList<ContextAction<? super Photo>>(2);
		toolBarActions.add(new AddPhotoAction(frame, photoBook));
		toolBarActions.add(new DeletePhotoAction(frame));
		ComplexAction<Photo> rotateAction=new ComplexAction<Photo>("Rotate", Icons.getIcon("rotate"));
		rotateAction.addAction(new RotatePhotoAction(frame, 90));
		rotateAction.addAction(new RotatePhotoAction(frame, 180));
		rotateAction.addAction(new RotatePhotoAction(frame, -90));
		toolBarActions.add(rotateAction);
		return GuiUtils.createToolBar(toolBarActions);
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getName(), PhotosView.class);
		bookmark.setParameter("photoBook.id", String.valueOf(photoBook.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long id=Long.valueOf(bookmark.getParameter("photoBook.id"));
		PhotoBook photoBook=PhotoManager.getInstance().getBook(id);
		frame.setCurrentView(new PhotosView(photoBook), true);
	}

	private class PhotoBookCollectionListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (event.getType()==CollectionChangeEvent.ADDED)
			{
				addThumbnail((Photo)event.getElement());
				updateUI();
			}
			else if (event.getType()==CollectionChangeEvent.REMOVED)
			{
				removeThumbnail((Photo)event.getElement());
				updateUI();
			}
		}
	}

	private void addThumbnail(Photo photo)
	{
		Thumbnail thumbnail=new Thumbnail(photo);
		thumbnailPanel.addThumbnail(thumbnail);
		getModelListenerList().addDisposable(thumbnail);
	}

	private void removeThumbnail(Photo photo)
	{
		for (Thumbnail thumbnail : thumbnailPanel.getThumbnails())
		{
			if (thumbnail.getPhoto()==photo)
			{
				thumbnailPanel.removeThumbnail(thumbnail);
				getModelListenerList().dispose(thumbnail);
			}
		}
	}

	private class SelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				List<Thumbnail> thumbnails=thumbnailPanel.getSelectedThumbnails();
				List<Photo> photos=new ArrayList<Photo>();
				for (Thumbnail thumbnail : thumbnails)
				{
					photos.add(thumbnail.getPhoto());
				}
				for (ContextAction<? super Photo> action : toolBarActions) action.update(photos);
			}
		}
	}
}
