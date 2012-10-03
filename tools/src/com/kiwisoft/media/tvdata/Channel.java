package com.kiwisoft.media.tvdata;

import javax.swing.*;
import java.util.TimeZone;

/**
 * @author Stefan Stiller
 * @since 06.03.11
 */
public class Channel
{
	/** The identifier for a channel that fits in no other category */
	public static final int CATEGORY_NONE = 0;
	/** The identifier for a channel that is in the category TV */
	public static final int CATEGORY_TV = 1;
	/** The identifier for a channel that is in the category radio */
	public static final int CATEGORY_RADIO = 1 << 1;
	/** The identifier for a channel that is in the category cinema */
	public static final int CATEGORY_CINEMA = 1 << 2;
	/** The identifier for a channel that is in the category events */
	public static final int CATEGORY_EVENTS = 1 << 3;
	/** The identifier for a channel that is in the category digital */
	public static final int CATEGORY_DIGITAL = 1 << 4;
	/** The identifier for a channel that is in the category music */
	public static final int CATEGORY_SPECIAL_MUSIC = 1 << 5;
	/** The identifier for a channel that is in the category sport */
	public static final int CATEGORY_SPECIAL_SPORT = 1 << 6;
	/** The identifier for a channel that is in the category news */
	public static final int CATEGORY_SPECIAL_NEWS = 1 << 7;
	/** The identifier for a channel that is in the category other */
	public static final int CATEGORY_SPECIAL_OTHER = 1 << 8;
	/** The identifier for a channel that is in the category pay TV */
	public static final int CATEGORY_PAY_TV = 1 << 9;
	/** The identifier for a channel that is in the category payed data TV */
	public static final int CATEGORY_PAYED_DATA_TV = 1 << 10;

	private String iconUrl;
	private Icon defaultIcon;
	private String name;

	public Channel(String name, String channelId, TimeZone timeZone, String country, String copyright, String webpage, ChannelGroup channelGroup, int categories)
	{
		this.name=name;
	}

	public void setIconUrl(String iconUrl)
	{
		this.iconUrl=iconUrl;
	}

	public void setDefaultIcon(Icon defaultIcon)
	{
		this.defaultIcon=defaultIcon;
	}

	public String getName()
	{
		return name;
	}
}
