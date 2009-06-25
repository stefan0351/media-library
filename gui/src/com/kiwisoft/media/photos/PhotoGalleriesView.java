package com.kiwisoft.media.photos;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.TableController;

public class PhotoGalleriesView extends ViewPanel
{
	private TableController<PhotoGallery> tableController;

	public PhotoGalleriesView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Photo Galleries";
	}

	@Override
	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		PhotoGalleriesTableModel tableModel=new PhotoGalleriesTableModel();
		tableController=new TableController<PhotoGallery>(tableModel, new DefaultTableConfiguration(PhotoGalleriesTableModel.class))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new PhotoGalleryDetailsAction());
				actions.add(new NewPhotoGalleryAction());
				actions.add(new DeletePhotoGalleryAction(frame));
				actions.add(new PhotosAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new PhotoGalleryDetailsAction());
				actions.add(null);
				actions.add(new NewPhotoGalleryAction());
				actions.add(new DeletePhotoGalleryAction(frame));
				actions.add(null);
				actions.add(new PhotosAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new PhotosAction(frame);
			}
		};

		getModelListenerList().addDisposable(PhotoManager.getInstance().addCollectionListener(new MyCollectionListener()));

		return tableController.createComponent();
	}

	@Override
	protected void installComponentListeners()
	{
		tableController.installListeners();
		super.installComponentListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		super.removeComponentListeners();
	}

	@Override
	public void dispose()
	{
		tableController.dispose();
		super.dispose();
	}

	private class MyCollectionListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (PhotoManager.GALLERIES.equals(event.getPropertyName()))
			{
				SortableTableModel<PhotoGallery> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						PhotoGallery gallery=(PhotoGallery)event.getElement();
						tableModel.addRow(new PhotoGalleriesTableModel.Row(gallery));
						break;
					case CollectionChangeEvent.REMOVED:
						int oldIndex=tableModel.indexOf(event.getElement());
						if (oldIndex>=0) tableModel.removeRowAt(oldIndex);
						break;
				}
			}
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
		return new Bookmark(getTitle(), PhotoGalleriesView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new PhotoGalleriesView());
	}

}
