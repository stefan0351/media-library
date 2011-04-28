package com.kiwisoft.media;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Bean;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.format.FormatStringComparator;

/**
 * todo allow assigning linkgroups to objects through gui
 * @author Stefan Stiller
 */
public class LinkManager extends Bean
{
	private static LinkManager instance;
	public static final String ROOT_GROUPS="rootGroups";

	public static LinkManager getInstance()
	{
		if (instance==null) instance=new LinkManager();
		return instance;
	}

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
		fireElementAdded(ROOT_GROUPS, group);
		return group;
	}

	public void dropRootGroup(LinkGroup group)
	{
		group.delete();
		fireElementRemoved(ROOT_GROUPS, group);
	}

	public void removeRootGroup(LinkGroup group)
	{
		fireElementRemoved(ROOT_GROUPS, group);
	}

	public void addRootGroup(LinkGroup group)
	{
		fireElementAdded(ROOT_GROUPS, group);
	}

	public void exportLinks(File file) throws IOException
	{
		XMLWriter html=new XMLWriter(new FileOutputStream(file), "UTF-8");
		html.start();
		html.startElement("html");
		html.startElement("h3");
		html.setText("Links");
		html.closeElement("h3");
		exportLinks(html, getRootGroups(), Collections.<Link>emptySet());
		html.closeElement("html");
		html.close();
	}

	private void exportLinks(XMLWriter html, Set<LinkGroup> groups, Set<Link> links) throws IOException
	{
		html.startElement("ul");
		List<LinkGroup> sortedGroups=new ArrayList<LinkGroup>(groups);
		Collections.sort(sortedGroups, new FormatStringComparator());
		for (LinkGroup group : sortedGroups)
		{
			html.startElement("li");
			html.startElement("b");
			html.setText(group.getName());
			html.closeElement("b");
			exportLinks(html, group.getSubGroups(), group.getLinks());
			html.closeElement("li");
		}
		List<Link> sortedLinks=new ArrayList<Link>(links);
		Collections.sort(sortedLinks, new FormatStringComparator());
		for (Link link : sortedLinks)
		{
			html.startElement("li");
			html.startElement("a");
			html.setAttribute("href", link.getUrl());
			html.setText(link.getName());
			html.closeElement("a");
			html.closeElement("li");
		}
		html.closeElement("ul");
	}

	public LinkGroup getRootGroup(String name)
	{
		return DBLoader.getInstance().load(LinkGroup.class, null, "name=? and parentgroup_id is null", name);
	}

	public LinkGroup getGroup(Long groupId)
	{
		return DBLoader.getInstance().load(LinkGroup.class, groupId);
	}
}
