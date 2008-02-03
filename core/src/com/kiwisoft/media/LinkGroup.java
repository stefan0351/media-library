package com.kiwisoft.media;

import java.util.HashSet;
import java.util.Set;

import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.IDObject;

public class LinkGroup extends IDObject implements Linkable
{
	public static final String PARENT_GROUP="parentGroup";
	public static final String NAME="name";
	public static final String LINKS="links";
	public static final String SUB_GROUPS="subGroups";
	public static final String RELATED_GROUPS="relatedGroups";

	private String name;
	private Set<Link> links;
	private Set<LinkGroup> subGroups;

	public LinkGroup()
	{
	}

	public LinkGroup(String name)
	{
		this.name=name;
	}

	public LinkGroup(DBDummy dummy)
	{
		super(dummy);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, name);
	}

	public LinkGroup getParentGroup()
	{
		return (LinkGroup)getReference(PARENT_GROUP);
	}

	public void setParentGroup(LinkGroup newParentGroup)
	{
		setReference(PARENT_GROUP, newParentGroup);
	}

	public Link createLink()
	{
		Link link=new Link(this);
		if (links!=null) links.add(link);
		fireElementAdded(LINKS, link);
		return link;
	}

	public void addLink(Link link)
	{
		if (links!=null) links.add(link);
		fireElementAdded(LINKS, link);
	}

	public void dropLink(Link link)
	{
		if (links!=null) links.remove(link);
		link.delete();
		fireElementRemoved(LINKS, link);
	}

	public void removeLink(Link link)
	{
		if (links!=null) links.remove(link);
		fireElementRemoved(LINKS, link);
	}

	public Set<Link> getLinks()
	{
		if (links==null) links=DBLoader.getInstance().loadSet(Link.class, null, "linkgroup_id=?", getId());
		return links;
	}

	public int getLinkCount()
	{
		if (links!=null) return links.size();
		else return DBLoader.getInstance().count(Link.class, null, "linkgroup_id=?", getId());
	}

	public LinkGroup createSubGroup(String name)
	{
		LinkGroup group=new LinkGroup(name);
		group.setParentGroup(this);
		if (subGroups!=null) subGroups.add(group);
		fireElementAdded(SUB_GROUPS, group);
		return group;
	}

	public void dropSubGroup(LinkGroup group)
	{
		if (subGroups!=null) subGroups.remove(group);
		group.delete();
		fireElementRemoved(SUB_GROUPS, group);
	}

	public void removeSubGroup(LinkGroup group)
	{
		if (subGroups!=null) subGroups.remove(group);
		fireElementRemoved(SUB_GROUPS, group);
	}

	public void addSubGroup(LinkGroup group)
	{
		if (subGroups!=null) subGroups.add(group);
		fireElementAdded(SUB_GROUPS, group);
	}

	public Set<LinkGroup> getSubGroups()
	{
		if (subGroups==null) subGroups=DBLoader.getInstance().loadSet(LinkGroup.class, null, "parentgroup_id=?", getId());
		return subGroups;
	}

	public LinkGroup getLinkGroup(boolean create)
	{
		return this;
	}

	@Override
	public void afterReload()
	{
		subGroups=null;
		links=null;
		super.afterReload();
	}

	@Override
	public void delete()
	{
		for (LinkGroup group : new HashSet<LinkGroup>(getSubGroups())) dropSubGroup(group);
		for (Link link : new HashSet<Link>(getLinks())) dropLink(link);
		super.delete();
	}

	public Set<LinkGroup> getRelatedGroups()
	{
		return getAssociations(RELATED_GROUPS);
	}

	public boolean isRelatedGroup(LinkGroup linkGroup)
	{
		return containsAssociation(RELATED_GROUPS, linkGroup);
	}

	public void addRelatedGroup(LinkGroup linkGroup)
	{
		createAssociation(RELATED_GROUPS, linkGroup);
		linkGroup.createAssociation(RELATED_GROUPS, this);
	}

	public void removeRelatedGroup(LinkGroup linkGroup)
	{
		dropAssociation(RELATED_GROUPS, linkGroup);
		linkGroup.dropAssociation(RELATED_GROUPS, this);
	}
}
