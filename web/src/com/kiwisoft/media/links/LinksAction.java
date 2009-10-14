package com.kiwisoft.media.links;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.LinkManager;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.files.*;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.format.FormatStringComparator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public class LinksAction extends BaseAction
{
	private Long groupId;
	private Long showId;

	private String groupName;
	private LinkGroup group;
	private Show show;
	private List<Link> links;
	private List<LinkGroup> relatedGroups;
	private List<LinkGroup> childGroups;

	@Override
	public String getPageTitle()
	{
		return "Links";
	}

	@Override
	public String execute() throws Exception
	{
		if (groupId!=null) group=LinkManager.getInstance().getGroup(groupId);
		groupName=group!=null ? group.getName() : "Links";
		if (showId!=null) show=ShowManager.getInstance().getShow(showId);
		links=new ArrayList<Link>();
		relatedGroups=new ArrayList<LinkGroup>();
		childGroups=new ArrayList<LinkGroup>();
		if (group!=null)
		{
			links.addAll(group.getLinks());
			childGroups.addAll(group.getSubGroups());
			relatedGroups.addAll(group.getRelatedGroups());
		}
		else
		{
			childGroups.addAll(LinkManager.getInstance().getRootGroups());
		}
		Collections.sort(links, new FormatStringComparator());
		Collections.sort(childGroups, new FormatStringComparator());
		Collections.sort(relatedGroups, new FormatStringComparator("hierarchy"));

		return super.execute();
	}

	public Long getGroupId()
	{
		return groupId;
	}

	public void setGroupId(Long groupId)
	{
		this.groupId=groupId;
	}

	public Long getShowId()
	{
		return showId;
	}

	public void setShowId(Long showId)
	{
		this.showId=showId;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public LinkGroup getGroup()
	{
		return group;
	}

	public Show getShow()
	{
		return show;
	}

	public List<Link> getLinks()
	{
		return links;
	}

	public List<LinkGroup> getRelatedGroups()
	{
		return relatedGroups;
	}

	public List<LinkGroup> getChildGroups()
	{
		return childGroups;
	}

	public boolean hasImages()
	{
		return show!=null && MediaFileManager.getInstance().getNumberOfMediaFiles(show, MediaType.IMAGE)>0;
	}

	public boolean hasVideos()
	{
		return show!=null && MediaFileManager.getInstance().getNumberOfMediaFiles(show, MediaType.VIDEO)>0;
	}
}