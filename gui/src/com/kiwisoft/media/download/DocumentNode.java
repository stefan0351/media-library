package com.kiwisoft.media.download;

import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import com.kiwisoft.swing.tree.GenericTreeNode;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeEvent;

public class DocumentNode extends GenericTreeNode<WebDocument> implements PropertyChangeListener, CollectionChangeListener
{
	public DocumentNode(WebDocument document)
	{
		super(document);
		setNameProperties("url", "state");
	}

	@Override
	public int getSortPriority()
	{
		return 1;
	}

	@Override
	protected void installListeners()
	{
		getListeners().installPropertyChangeListener(getUserObject(), WebDocument.STATE, this);
		getListeners().addDisposable(getUserObject().addCollectionListener(this));
		super.installListeners();
	}

	public Vector<GenericTreeNode> loadChildren()
	{
		Vector<GenericTreeNode> nodes=super.loadChildren();
		for (WebDocument document : getUserObject().getContainedDocuments()) nodes.add(new DocumentNode(document));
		return nodes;
	}

	public boolean isLeaf()
	{
		return getUserObject().getLinkedDocuments().isEmpty();
	}

	public String getText()
	{
		if (getParent() instanceof FolderNode)
		{
			//noinspection unchecked
			WebFolder folder=((FolderNode)getParent()).getUserObject();
			return folder.getDocumentName(getUserObject());
		}
		return getUserObject().getURL().toString();
	}

//	public JComponent[] getPopupMenu()
//	{
//		return new JComponent[]{
//			new JMenuItem(new EditDocumentAction(getUserObject())),
//			new JSeparator(),
//			new JMenuItem(new StartDownloadAction(getUserObject()))
//		};
//	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		fireNodeChanged();
	}

	public void collectionChanged(CollectionChangeEvent event)
	{
		if (WebDocument.CONTAINED_DOCUMENTS.equals(event.getPropertyName()))
		{
			if (CollectionChangeEvent.ADDED==event.getType())
			{
				addChild(new DocumentNode((WebDocument)event.getElement()));
			}
		}
	}
}
