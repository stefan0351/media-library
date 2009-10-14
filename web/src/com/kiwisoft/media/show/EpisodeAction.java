package com.kiwisoft.media.show;

import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.Country;
import com.kiwisoft.media.CountryManager;

/**
 * @author Stefan Stiller
 * @since 07.10.2009
 */
public class EpisodeAction extends ShowAction
{
	private Long episodeId;
	private Season season;
	private Episode episode;

	@Override
	public String execute() throws Exception
	{
		if (episodeId!=null) episode=ShowManager.getInstance().getEpisode(episodeId);
		if (episode!=null)
		{
			season=episode.getSeason();
			setShow(episode.getShow());
		}
		return SUCCESS;
	}

	public Season getSeason()
	{
		return season;
	}

	public Episode getEpisode()
	{
		return episode;
	}

	public Long getEpisodeId()
	{
		return episodeId;
	}

	public void setEpisodeId(Long episodeId)
	{
		this.episodeId=episodeId;
	}

	@Override
	public MediaFile findLogo()
	{
		MediaFile logo=null;
		if (getSeason()!=null) logo=getSeason().getLogo();
		if (logo==null) logo=super.findLogo();
		return logo;
	}

	public Country getGermany()
	{
		return CountryManager.getInstance().getCountryBySymbol("DE");
	}
}