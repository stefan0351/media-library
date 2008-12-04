package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.show.Episode;

/**
 * @author Stefan Stiller
*/
class TvTvDeEpisodeData
{
	private int timeOffset;
	private Episode episode;
	private String title;
	private String germanTitle;
	private String originalTitle;

	TvTvDeEpisodeData(String title)
	{
		this.title=title;
	}

	TvTvDeEpisodeData(String germanTitle, String originalTitle)
	{
		this(germanTitle, originalTitle, 0);
	}

	TvTvDeEpisodeData(String germanTitle, String originalTitle, int timeOffset)
	{
		title=germanTitle+" ("+originalTitle+")";
		this.originalTitle=originalTitle;
		this.germanTitle=germanTitle;
		this.timeOffset=timeOffset;
	}

	public Episode getEpisode()
	{
		return episode;
	}

	public void setEpisode(Episode episode)
	{
		this.episode=episode;
	}

	public String getGermanTitle()
	{
		return germanTitle;
	}

	public void setGermanTitle(String germanTitle)
	{
		this.germanTitle=germanTitle;
	}

	public String getOriginalTitle()
	{
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle)
	{
		this.originalTitle=originalTitle;
	}

	public int getTimeOffset()
	{
		return timeOffset;
	}

	public void setTimeOffset(int timeOffset)
	{
		this.timeOffset=timeOffset;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title=title;
	}
}
