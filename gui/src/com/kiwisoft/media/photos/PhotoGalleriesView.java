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
import java.util.Collections;

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

//		getModelListenerList().addDisposable(PhotoManager.getInstance().addCollectionListener(new MyCollectionListener()));
		return component;
	}

	@Override
	protected void initializeData()
	{
		super.initializeData();
		GenericTree tree=treeController.getTree();
		tree.setRoot(new PhotoGalleryNode(PhotoManager.getInstance().getRootGallery()));
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
			GenericTree tree=(GenericTree) c;
			TreePath selectionPath=tree.getLeadSelectionPath();
			if (selectionPath!=null)
			{
				GenericTreeNode node=(GenericTreeNode) selectionPath.getLastPathComponent();
				if (node.getUserObject() instanceof PhotoGallery)
				{
					MediaTransferable transferable=new MediaTransferable(PhotoGallery.class, ((PhotoGallery) node.getUserObject()).getId());
					GenericTreeNode parentNode=node.getParent();
					if (parentNode.getUserObject() instanceof PhotoGallery)
					transferable.setProperty("parent", ((PhotoGallery) parentNode.getUserObject()).getId());
					return transferable;
				}
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

		private boolean dragObject(Object draggedObject, Object target, final int dropAction)
		{
			if (dropAction==MOVE || dropAction==COPY)
			{
				if (draggedObject instanceof PhotoGallery)
				{
					final PhotoGallery draggedGallery=(PhotoGallery)draggedObject;
					if (target==null) target=PhotoManager.getInstance().getRootGallery();
					if (target instanceof PhotoGallery && target!=draggedObject && !draggedGallery.getParents().contains(target))
					{
						final PhotoGallery targetGallery=(PhotoGallery)target;
						return DBSession.execute(new Transactional()
						{
							@Override
							public void run() throws Exception
							{
								if (dropAction==MOVE) draggedGallery.setParents(Collections.<PhotoGallery>emptySet());
								targetGallery.addChildGallery(draggedGallery);
							}

							@Override
							public void handleError(Throwable throwable, boolean rollback)
							{
								GuiUtils.handleThrowable(PhotoGalleriesView.this, throwable);
							}
						});
					}
				}
			}
			return false;
		}
	}
}
