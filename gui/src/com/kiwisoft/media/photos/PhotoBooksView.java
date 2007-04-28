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

public class PhotoBooksView extends ViewPanel
{
	private TableController<PhotoBook> tableController;

	public PhotoBooksView()
	{
	}

	public String getName()
	{
		return "Photo Books";
	}

	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		PhotoBooksTableModel tableModel=new PhotoBooksTableModel();
		tableController=new TableController<PhotoBook>(tableModel, new DefaultTableConfiguration(PhotoBooksTableModel.class, "table.photoBooks"))
		{
			public List<ContextAction<? super PhotoBook>> getToolBarActions()
			{
				List<ContextAction<? super PhotoBook>> actions=new ArrayList<ContextAction<? super PhotoBook>>();
				actions.add(new PhotoBookDetailsAction());
				actions.add(new NewPhotoBookAction());
//				actions.add(new DeleteVideoAction(frame));
				actions.add(new PhotosAction(frame));
				return actions;
			}

			public List<ContextAction<? super PhotoBook>> getContextActions()
			{
				List<ContextAction<? super PhotoBook>> actions=new ArrayList<ContextAction<? super PhotoBook>>();
				actions.add(new PhotoBookDetailsAction());
				actions.add(null);
				actions.add(new NewPhotoBookAction());
//				actions.add(new DeleteVideoAction(frame));
//				actions.add(null);
//				actions.add(new SetVideoObsoleteAction(frame));
//				actions.add(new SetVideoActiveAction(frame));
//				actions.add(null);
				actions.add(new PhotosAction(frame));
				return actions;
			}

			public ContextAction<? super PhotoBook> getDoubleClickAction()
			{
				return new PhotosAction(frame);
			}
		};

		getModelListenerList().installCollectionListener(PhotoManager.getInstance(), new PhotoBooksListener());

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

	private class PhotoBooksListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (PhotoManager.BOOKS.equals(event.getPropertyName()))
			{
				SortableTableModel<PhotoBook> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						PhotoBook book=(PhotoBook) event.getElement();
						tableModel.addRow(new PhotoBooksTableModel.Row(book));
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
		return new Bookmark(getName(), PhotoBooksView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new PhotoBooksView(), true);
	}

}
