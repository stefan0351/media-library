package com.kiwisoft.media.show;

import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.xp.XPLoader;
import com.kiwisoft.xp.XPBean;
import com.kiwisoft.utils.FileUtils;

import java.io.File;

import org.apache.struts2.ServletActionContext;

/**
 * @author Stefan Stiller
 * @since 08.10.2009
 */
public class ShowInfoAction extends ShowAction
{
	private Long infoId;
	private ShowInfo info;
	private String html;
	private XPBean xmlBean;
	private String template;

	@Override
	public String execute() throws Exception
	{
		if (infoId!=null)
		{
			info=ShowManager.getInstance().getShowInfo(infoId);
			if (info!=null)
			{
				setShow(info.getShow());
				File file=new File(MediaConfiguration.getRootPath(), info.getPath());
				if (file.getName().endsWith(".xp"))
				{
					xmlBean=XPLoader.loadXMLFile(ServletActionContext.getRequest(), file);
					template=(String)xmlBean.getValue("template");
					if (template==null) template=xmlBean.getName();
					Object variant=xmlBean.getValue("variant");
					if (variant!=null) template=template+"-"+variant;
				}
				else if (file.getName().endsWith(".html"))
				{
					html=FileUtils.loadFile(file);
					template="html";
				}
				else
				{
					return "redirect";
				}
			}
		}
		return super.execute();
	}

	public ShowInfo getInfo()
	{
		return info;
	}

	public Long getInfoId()
	{
		return infoId;
	}

	public void setInfoId(Long infoId)
	{
		this.infoId=infoId;
	}

	public String getTemplate()
	{
		return template;
	}

	public String getHtml()
	{
		return html;
	}

	public XPBean getXmlBean()
	{
		return xmlBean;
	}
}
