package com.kiwisoft.media.dataimport;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.kiwisoft.utils.xml.XMLAdapter;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.DefaultXMLObject;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.ChannelManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class AiringData extends XMLAdapter
{
	private static final SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("d.M.yy");

	private String channelName;
	private Date time;
	private Channel channel;

	public AiringData(XMLContext dummy, String aName)
	{
		super(dummy, aName);
	}

	public String getChannelName()
	{
		return channelName;
	}

	public Date getTime()
	{
		return time;
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof DefaultXMLObject)
		{
			DefaultXMLObject xmlObject=(DefaultXMLObject)element;
			if ("Sender".equalsIgnoreCase(xmlObject.getName()))
				channelName=xmlObject.getContent();
			else if ("Datum".equalsIgnoreCase(xmlObject.getName()))
			{
				try
				{
					time=ImportUtils.DATE_FORMAT.parse(xmlObject.getContent());
				}
				catch (ParseException pe1)
				{
					try
					{
						time=DATE_FORMAT.parse(xmlObject.getContent());
					}
					catch (ParseException pe2)
					{
						pe2.printStackTrace();
					}
				}
			}
		}
	}

	public String toString()
	{
		return channelName+" "+time;
	}

	public Channel getChannel()
	{
		if (channel==null) channel=ChannelManager.getInstance().getChannelByName(channelName);
		return channel;
}
}