package com.kiwisoft.media.photos;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.media.MediaTransferable;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.swing.tree.GenericTree;
import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.swing.tree.TreeController;
import com.kiwisoft.swing.tree.TreeUtils;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleriesView extends ViewPanel
{
	private TableController<PhotoGallery> tableController;
	private TreeController treeController;

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
		treeController=new TreeController()
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
			public List<ContextAction> getContextActions(GenericTreeNode node)
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
			public ContextAction getDoubleClickAction(GenericTreeNode node)
			{
				if (node instanceof PhotoGalleryNode) return new PhotosAction(frame);
				return null;
			}
		};

		JComponent component=treeController.createComponent();

		GenericTree tree=treeController.getTree();
		tree.setRootVisible(false);
		tree.setDragEnabled(true);
		tree.setExpandsSelectedPaths(true);
		tree.setTransferHandler(new MyTransferHandler());

//		PhotoGalleriesTableModel tableModel=new PhotoGalleriesTableModel();
//		tableController=new TableController<PhotoGallery>(tableModel, new DefaultTableConfiguration(PhotoGalleriesTableModel.class))
//		{
//			@Override
//			public List<ContextAction> getToolBarActions()
//			{
//				List<ContextAction> actions=new ArrayList<ContextAction>();
//				actions.add(new PhotoGalleryDetailsAction());
//				actions.add(new NewPhotoGalleryAction());
//				actions.add(new DeletePhotoGalleryAction(frame));
//				actions.add(new PhotosAction(frame));
//				return actions;
//			}
//
//			@Override
//			public List<ContextAction> getContextActions()
//			{
//				List<ContextAction> actions=new ArrayList<ContextAction>();
//				actions.add(new PhotoGalleryDetailsAction());
//				actions.add(null);
//				actions.add(new NewPhotoGalleryAction());
//				actions.add(new DeletePhotoGalleryAction(frame));
//				actions.add(null);
//				actions.add(new PhotosAction(frame));
//				return actions;
//			}
//
//			@Override
//			public ContextAction getDoubleClickAction()
//			{
//				return new PhotosAction(frame);
//			}
//		};

//		getModelListenerList().addDisposable(PhotoManager.getInstance().addCollectionListener(new MyCollectionListener()));
		return component;
	}

	@Override
	protected void initializeData()
	{
		super.initializeData();
		GenericTree tree=treeController.getTree();
		tree.setRoot(new PhotosRootNode());
	}

	@Override
	protected void installComponentListeners()
	{
		treeController.installListeners();
		super.installComponentListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		treeController.removeListeners();
		super.removeComponentListeners();
	}

	@Override
	public void dispose()
	{
		treeController.dispose();
		super.dispose();
	}

//	private class MyCollectionListener implements CollectionChangeListener
//	{
//		@Override
//		public void collectionChanged(CollectionChangeEvent event)
//		{
//			if (PhotoManager.GALLERIES.equals(event.getPropertyName()))
//			{
//				SortableTableModel<PhotoGallery> tableModel=tableController.getModel();
//				switch (event.getType())
//				{
//					case CollectionChangeEvent.ADDED:
//						PhotoGallery gallery=(PhotoGallery)event.getElement();
//						tableModel.addRow(new PhotoGalleriesTableModel.Row(gallery));
//						break;
//					case CollectionChangeEvent.REMOVED:
//						int oldIndex=tableModel.indexOf(event.getElement());
//						if (oldIndex>=0) tableModel.removeRowAt(oldIndex);
//						break;
//				}
//			}
//		}
//	}

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

	private class MyTransferHandler extends TransferHandler
	{
		@Override
		public int getSourceActions(JComponent c)
		{
			GenericTree tree=(GenericTree) c;
			if (tree.getSelectionCount()==1) return COPY+MOVE;
			else return NONE;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			List objects=TreeUtils.getSelectedObjects((GenericTree) c);
			if (objects.size()==1)
			{
				Object object=objects.get(0);
				if (object instanceof PhotoGallery) return new MediaTransferable(PhotoGallery.class, ((PhotoGallery) object).getId());
			}
			return null;
		}

		@Override
		public boolean canImport(TransferSupport support)
		{
			return true;
		}

		@Override
		public boolean importData(TransferSupport support)
		{
			GenericTree tree=(GenericTree) support.getComponent();
			Point dropLocation=support.getDropLocation().getDropPoint();
			TreePath path=tree.getPathForLocation(dropLocation.x, dropLocation.y);
			Transferable transferable=support.getTransferable();
			try
			{
				Object draggedObject=transferable.getTransferData(MediaTransferable.DATA_FLAVOR);
				if (path!=null)
				{
					GenericTreeNode targetNode=(GenericTreeNode) path.getLastPathComponent();
					return dragObject(draggedObject, targetNode.getUserObject(), support.getUserDropAction());
				}
				else
				{
					return dragObject(draggedObject, null, support.getUserDropAction());
				}
			}
			catch (UnsupportedFlavorException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return false;
		}

		private boolean dragObject(Object draggedObject, Object target, int dropAction)
		{
			switch (dropAction)
			{
				case MOVE:
					return moveObject(draggedObject, target);
				case COPY:
					return copyObject(draggedObject, target);
			}
			return false;
		}

		private boolean moveObject(Object draggedObject, Object target)
		{
			if (draggedObject instanceof PhotoGallery)
			{
				final PhotoGallery draggedGallery=(PhotoGallery)draggedObject;
				if (target!=draggedObject && draggedGallery.getParent()!=target && (target==null || target instanceof PhotoGallery))
				{
					final PhotoGallery targetGallery=(PhotoGallery)target;
					return DBSession.execute(new Transactional()
					{
						@Override
						public void run() throws Exception
						{
							PhotoGallery oldParent=draggedGallery.getParent();
							if (oldParent==null) PhotoManager.getInstance().removeRootGallery(draggedGallery);
							else oldParent.removeChildGallery(draggedGallery);
							if (targetGallery==null) PhotoManager.getInstance().addRootGallery(draggedGallery);
							else targetGallery.addChildGallery(draggedGallery);
						}

						@Override
						public void handleError(Throwable throwable, boolean rollback)
						{
							GuiUtils.handleThrowable(PhotoGalleriesView.this, throwable);
						}
					});
				}
			}
			return false;
		}

		private boolean copyObject(Object draggedObject, Object target)
		{
//			if (draggedObject instanceof PhotoGallery)
//			{
//				if (target instanceof PhotoGallery && target!=draggedObject)
//				{
//					final PhotoGallery gallery=(PhotoGallery) draggedObject;
//					final PhotoGallery targetGallery=(PhotoGallery) target;
//					return DBSession.execute(new Transactional()
//					{
//						@Override
//						public void run() throws Exception
//						{
//							targetGallery.addChildGallery(gallery);
//						}
//
//						@Override
//						public void handleError(Throwable throwable, boolean rollback)
//						{
//							GuiUtils.handleThrowable(PhotoGalleriesView.this, throwable);
//						}
//					});
//				}
//			}
			return false;
		}
	}
}
