package com.kiwisoft.media.fanfics;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.FanFicPart;
import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.web.RecentItemManager;
import com.kiwisoft.web.RecentIdObject;

import java.io.File;
import java.util.Collection;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class FanFicAction extends BaseAction
{
	private Long fanFicId;
	private Long partId;

	private FanFicPart part;
	private FanFic fanFic;
	private String html;
	private FanFicPart nextPart;
	private Movie movie;
	private Show show;
	private String imagePath;

	@Override
	public String getPageTitle()
	{
		return fanFic.getTitle();
	}

	@Override
	public String execute() throws Exception
	{
		if (partId!=null)
		{
			part=FanFicManager.getInstance().getFanFicPart(partId);
			if (part!=null) fanFic=part.getFanFic();
		}
		else if (fanFicId!=null) 
		{
			fanFic=FanFicManager.getInstance().getFanFic(fanFicId);
			if (fanFic!=null) part=fanFic.getParts().getFirst();
		}
		if (fanFic!=null)
		{
			RecentItemManager.getInstance().addItem(new RecentIdObject<FanFic>(FanFic.class, fanFic));
			Collection<FanDom> fanDoms=fanFic.getFanDoms();
			if (fanDoms.size()==1)
			{
				FanDom fanDom=fanDoms.iterator().next();
				movie=fanDom.getMovie();
				show=fanDom.getShow();
			}
			if (part!=null)
			{
				nextPart=fanFic.getParts().getNext(part);
			}
		}

		if (part!=null)
		{
			if ("image".equals(part.getType()))
			{
				imagePath="/files/"+part.getClass().getName()+"/"+part.getId()+"/content."+part.getExtension();
			}
			else if ("html".equals(part.getType()))
			{
				File contentFile=part.getContentFile();
				html=contentFile!=null ? FileUtils.loadFile(contentFile, part.getEncoding()) : "";
			}
		}
		return super.execute();
	}

	public Long getFanFicId()
	{
		return fanFicId;
	}

	public void setFanFicId(Long fanFicId)
	{
		this.fanFicId=fanFicId;
	}

	public Long getPartId()
	{
		return partId;
	}

	public void setPartId(Long partId)
	{
		this.partId=partId;
	}

	public FanFicPart getPart()
	{
		return part;
	}

	public FanFic getFanFic()
	{
		return fanFic;
	}

	public String getHtml()
	{
		return html;
	}

	public FanFicPart getNextPart()
	{
		return nextPart;
	}

	public Movie getMovie()
	{
		return movie;
	}

	public Show getShow()
	{
		return show;
	}

	public String getType()
	{
		return part!=null ? part.getType() : null;
	}

	public String getImagePath()
	{
		return imagePath;
	}
}
