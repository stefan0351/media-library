/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.show;

import com.kiwisoft.media.WebInfo;
import com.kiwisoft.utils.db.DBDummy;

public class EpisodeInfo extends WebInfo
{
	public static final String EPISODE="episode";

	public EpisodeInfo(Episode episode)
	{
		setEpisode(episode);
	}

	public EpisodeInfo(DBDummy dummy)
	{
		super(dummy);
	}

	public Episode getEpisode()
	{
		return (Episode)getReference(EPISODE);
	}

	public void setEpisode(Episode episode)
	{
		setReference(EPISODE, episode);
	}

	public boolean isDefault()
	{
		return getEpisode().getDefaultInfo()==this;
	}
}
