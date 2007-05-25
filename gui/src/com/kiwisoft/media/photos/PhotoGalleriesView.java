package com.kiwisoft.media.photos;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JComponent;

import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.DefaultTableConfiguration;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.media.video.*;
import com.kiwisoft.media.utils.TableController;

public class PhotoGalleriesView extends ViewPanel
{
	private TableController<PhotoGallery> tableController;

	public PhotoGalleriesView()
	{
	}

	public String getName()
	{
		return "Photo Galleries";
	}

	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		PhotoGalleriesTableModel tableModel=new PhotoGalleriesTableModel();
		tableController=new TableController<PhotoGallery>(tableModel, new DefaultTableConfiguration(PhotoGalleriesTableModel.class, "table.photoGalleries"))
		{
			public List<ContextAction<? super PhotoGallery>> getToolBarActions()
			{
				List<ContextAction<? super PhotoGallery>> actions=new ArrayList<ContextAction<? super PhotoGallery>>();
				actions.add(new PhotoGalleryDetailsAction());
				actions.add(new NewPhotoGalleryAction());
				actions.add(new DeletePhotoGalleryAction(frame));
				actions.add(new PhotosAction(frame));
				return actions;
			}

			public List<ContextAction<? super PhotoGallery>> getContextActions()
			{
				List<ContextAction<? super PhotoGallery>> actions=new ArrayList<ContextAction<? super PhotoGallery>>();
				actions.add(new PhotoGalleryDetailsAction());
				actions.add(null);
				actions.add(new NewPhotoGalleryAction());
				actions.add(new DeletePhotoGalleryAction(frame));
				actions.add(null);
				actions.add(new PhotosAction(frame));
				return actions;
			}

			public ContextAction<? super PhotoGallery> getDoubleClickAction()
			{
				return new PhotosAction(frame);
			}
		};

		getModelListenerList().installCollectionListener(PhotoManager.getInstance(), new MyCollectionListener());

		return tableController.createComponent();
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
						PhotoGallery gallery=(PhotoGallery) event.getElement();
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

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		return new Bookmark(getName(), PhotoGalleriesView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new PhotoGalleriesView(), true);
	}

}