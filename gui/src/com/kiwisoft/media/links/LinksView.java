package com.kiwisoft.media.links;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.media.*;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.tree.GenericTree;
import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.swing.tree.TreeController;
import com.kiwisoft.swing.tree.TreeUtils;

/**
 * @author Stefan Stiller
 * @todo import from html
 * @todo export to html
 * @todo check active
 */
public class LinksView extends ViewPanel
{
	private TreeController treeController;
	private Linkable linkable;

	public LinksView()
	{
		this(null);
	}

	public LinksView(Linkable linkable)
	{
		this.linkable=linkable;
		setTitle("Links");
	}

	protected JComponent createContentPanel(final ApplicationFrame applicationFrame)
	{
		treeController=new TreeController()
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new LinkDetailsAction());
				actions.add(new ComplexAction("Add", Icons.getIcon("add"), new NewLinkAction(), new NewLinkGroupAction()));
				actions.add(new DeleteLinkAction(applicationFrame));
				actions.add(new ExportLinksAction(applicationFrame));
				actions.add(new DownloadAction(applicationFrame));
				return actions;
			}

			@Override
			protected Object getNonSelectionObject()
			{
				return LinkManager.getInstance();
			}
		};
		JComponent component=treeController.createComponent();
		GenericTree tree=treeController.getTree();
		tree.setRootVisible(false);
		tree.setDragEnabled(true);
		tree.setExpandsSelectedPaths(true);
		tree.setTransferHandler(new MyTransferHandler());
		return component;
	}

	@Override
	protected void initializeData()
	{
		GenericTree tree=treeController.getTree();
		LinksRootNode rootNode=new LinksRootNode();
		tree.setRoot(rootNode);
		if (linkable!=null)
		{
			LinkGroup linkGroup=linkable.getLinkGroup(false);
			if (linkGroup!=null)
			{
				TreePath groupPath=getLinkGroupPath(linkGroup);
				TreePath treePath=TreeUtils.findByUserObjectPath(rootNode, groupPath);
				if (treePath!=null)
				{
					tree.expandPath(treePath);
					tree.setSelectionPath(treePath);
				}
			}
			else
			{
				rootNode.children(); // loads children
				LinkableNode linkableNode=new LinkableNode(linkable);
				rootNode.addChild(linkableNode);
				tree.setSelectionPath(TreeUtils.getPathToRoot(linkableNode));
			}
		}
		super.initializeData();
	}

	private TreePath getLinkGroupPath(LinkGroup group)
	{
		List<Object> path=new LinkedList<Object>();
		while (group!=null)
		{
			path.add(0, group);
			group=group.getParentGroup();
		}
		return new TreePath(path.toArray());
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
		super.removeComponentListeners();
		treeController.removeListeners();
	}

	private class MyTransferHandler extends TransferHandler
	{
		public int getSourceActions(JComponent c)
		{
			GenericTree tree=(GenericTree)c;
			if (tree.getSelectionCount()==1) return COPY+MOVE;
			else return NONE;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			List objects=TreeUtils.getSelectedObjects((GenericTree)c);
			if (objects.size()==1)
			{
				Object object=objects.get(0);
				if (object instanceof Link) return new MediaTransferable(Link.class, ((Link)object).getId());
				else if (object instanceof LinkGroup) return new MediaTransferable(LinkGroup.class, ((LinkGroup)object).getId());
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
			GenericTree tree=(GenericTree)support.getComponent();
			Point dropLocation=support.getDropLocation().getDropPoint();
			TreePath path=tree.getPathForLocation(dropLocation.x, dropLocation.y);
			Transferable transferable=support.getTransferable();
			try
			{
				Object draggedObject=transferable.getTransferData(MediaTransferable.DATA_FLAVOR);
				if (path!=null)
				{
					GenericTreeNode targetNode=(GenericTreeNode)path.getLastPathComponent();
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
			if (draggedObject instanceof LinkGroup)
			{
				final LinkGroup draggedGroup=(LinkGroup)draggedObject;
				if (target!=draggedObject && draggedGroup.getParentGroup()!=target && (target==null || target instanceof LinkGroup))
				{
					final LinkGroup targetGroup=(LinkGroup)target;
					return DBSession.execute(new Transactional()
					{
						public void run() throws Exception
						{
							LinkGroup oldParentGroup=draggedGroup.getParentGroup();
							if (oldParentGroup==null) LinkManager.getInstance().removeRootGroup(draggedGroup);
							else oldParentGroup.removeSubGroup(draggedGroup);
							if (targetGroup==null) LinkManager.getInstance().addRootGroup(draggedGroup);
							else targetGroup.addSubGroup(draggedGroup);
							draggedGroup.setParentGroup(targetGroup);
						}

						public void handleError(Throwable throwable, boolean rollback)
						{
							GuiUtils.handleThrowable(LinksView.this, throwable);
						}
					});
				}
			}
			else if (draggedObject instanceof Link)
			{
				final Link draggedLink=(Link)draggedObject;
				if (target instanceof LinkGroup && draggedLink.getGroup()!=target)
				{
					final LinkGroup targetGroup=(LinkGroup)target;
					return DBSession.execute(new Transactional()
					{
						public void run() throws Exception
						{
							LinkGroup oldParentGroup=draggedLink.getGroup();
							if (oldParentGroup!=null) oldParentGroup.removeLink(draggedLink);
							targetGroup.addLink(draggedLink);
							draggedLink.setGroup(targetGroup);
						}

						public void handleError(Throwable throwable, boolean rollback)
						{
							GuiUtils.handleThrowable(LinksView.this, throwable);
						}
					});
				}
			}
			return false;
		}

		private boolean copyObject(Object draggedObject, Object target)
		{
			if (draggedObject instanceof LinkGroup)
			{
				final LinkGroup draggedGroup=(LinkGroup)draggedObject;
				if (target!=draggedGroup && target instanceof LinkGroup)
				{
					final LinkGroup targetGroup=(LinkGroup)target;
					if (!targetGroup.isRelatedGroup(draggedGroup))
					{
						return DBSession.execute(new Transactional()
						{
							public void run() throws Exception
							{
								targetGroup.addRelatedGroup(draggedGroup);
							}

							public void handleError(Throwable throwable, boolean rollback)
							{
								GuiUtils.handleThrowable(LinksView.this, throwable);
							}
						});
					}
				}
			}
			return false;
		}
	}

}
