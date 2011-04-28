package com.kiwisoft.media.photos;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.collection.*;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

public class PhotosView extends ViewPanel
{
	private PhotoGallery photoGallery;
	private ThumbnailPanel thumbnailPanel;
	private List<ContextAction> toolBarActions;

	public PhotosView(PhotoGallery photoGallery)
	{
		this.photoGallery=photoGallery;
	}

	@Override
	public String getTitle()
	{
		return "Photos - "+photoGallery.getName();
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		thumbnailPanel=new ThumbnailPanel();

		JPanel panel=new JPanel(new BorderLayout());
		panel.add(createToolBar(frame), BorderLayout.NORTH);
		panel.add(new JScrollPane(thumbnailPanel), BorderLayout.CENTER);

		getModelListenerList().installPropertyChangeListener(photoGallery, new PhotosCollectionListener());
		thumbnailPanel.addListSelectionListener(new SelectionListener());
		getModelListenerList().addDisposable(photoGallery.getPhotos().addChainListener(new PhotosChainListener()));

		return panel;
	}

	@Override
	protected void initializeData()
	{
		for (Photo photo : photoGallery.getPhotos()) addThumbnail(photo);
	}

	protected JToolBar createToolBar(ApplicationFrame frame)
	{
		toolBarActions=new ArrayList<ContextAction>(2);
		toolBarActions.add(new PhotoDetailsAction());
		toolBarActions.add(new AddPhotoAction(frame, photoGallery));
		toolBarActions.add(new DeletePhotoAction(frame));
		toolBarActions.add(new ShowPhotoAction(frame));
		ComplexAction rotateAction=new ComplexAction("Rotate", Icons.getIcon("rotate"));
		rotateAction.addAction(new RotatePhotoAction(frame, 90));
		rotateAction.addAction(new RotatePhotoAction(frame, 180));
		rotateAction.addAction(new RotatePhotoAction(frame, -90));
		toolBarActions.add(rotateAction);
		toolBarActions.add(new MovePhotoUpAction(thumbnailPanel, photoGallery.getPhotos()));
		toolBarActions.add(new MovePhotoDownAction(thumbnailPanel, photoGallery.getPhotos()));
		toolBarActions.add(new MovePhotoAction(frame, photoGallery));
		return GuiUtils.createToolBar(toolBarActions);
	}

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), PhotosView.class);
		bookmark.setParameter("photoGallery.id", String.valueOf(photoGallery.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long id=Long.valueOf(bookmark.getParameter("photoGallery.id"));
		PhotoGallery photoGallery=PhotoManager.getInstance().getGallery(id);
		frame.setCurrentView(new PhotosView(photoGallery));
	}

	private class PhotosCollectionListener extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (PhotoGallery.PHOTOS.equals(event.getPropertyName()))
			{
				if (event.getType()==CollectionPropertyChangeEvent.ADDED)
				{
					addThumbnail((Photo)event.getElement());
					updateUI();
				}
				else if (event.getType()==CollectionPropertyChangeEvent.REMOVED)
				{
					removeThumbnail((Photo)event.getElement());
					updateUI();
				}
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
		@Override
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
				for (ContextAction action : toolBarActions) action.update(photos);
			}
		}
	}

	private class PhotosChainListener implements ChainListener<Photo>
	{
		@Override
		public void chainChanged(ChainEvent<Photo> event)
		{
			thumbnailPanel.sort(Chain.getComparator());
		}
	}

}
