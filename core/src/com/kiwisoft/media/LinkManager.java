package com.kiwisoft.media;

import java.util.Set;

import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeSource;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Disposable;

/**
 * @author Stefan Stiller
 */
public class LinkManager implements CollectionChangeSource
{
	private static LinkManager instance;
	public static final String ROOT_GROUPS="rootGroups";

	public static LinkManager getInstance()
	{
		if (instance==null) instance=new LinkManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	private LinkManager()
	{
	}

	public Set<LinkGroup> getRootGroups()
	{
		return DBLoader.getInstance().loadSet(LinkGroup.class, null, "parentgroup_id is null");
	}

	public LinkGroup createRootGroup(String name)
	{
		LinkGroup group=new LinkGroup(name);
		collectionChangeSupport.fireElementAdded(ROOT_GROUPS, group);
		return group;
	}

	public void dropRootGroup(LinkGroup group)
	{
		group.delete();
		collectionChangeSupport.fireElementRemoved(ROOT_GROUPS, group);
	}

	public Disposable addCollectionListener(CollectionChangeListener listener)
	{
		return collectionChangeSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	public void removeRootGroup(LinkGroup group)
	{
		collectionChangeSupport.fireElementRemoved(ROOT_GROUPS, group);
	}

	public void addRootGroup(LinkGroup group)
	{
		collectionChangeSupport.fireElementAdded(ROOT_GROUPS, group);
	}
}
