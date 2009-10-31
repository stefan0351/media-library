package com.kiwisoft.media.fanfics;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.FanFicPart;
import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.xp.XPLoader;
import com.kiwisoft.xp.XPBean;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.web.RecentItemManager;
import com.kiwisoft.web.RecentIdObject;

import java.io.File;
import java.util.Collection;

import org.apache.struts2.ServletActionContext;

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
	private XPBean xmlBean;
	private String html;
	private FanFicPart nextPart;
	private Movie movie;
	private Show show;

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
		String source=null;
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
				source=part.getSource();
			}
		}

		if (source!=null)
		{
			File file=new File(MediaConfiguration.getRootPath(), "/fanfic/authors/"+source);
			if (source.endsWith(".xp"))
			{
				xmlBean=XPLoader.loadXMLFile(ServletActionContext.getRequest(), file);
			}
			else
			{
				html=FileUtils.loadFile(file);
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

	public XPBean getXmlBean()
	{
		return xmlBean;
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
}
