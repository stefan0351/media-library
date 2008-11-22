package com.kiwisoft.media.show;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class EpisodeFormat extends DefaultObjectFormat
{
	private boolean full;

	public EpisodeFormat(boolean full)
	{
		this.full=full;
	}

	@Override
	public String format(Object value)
	{
		if (value instanceof Episode)
		{
			Episode episode=(Episode)value;
			String episodeTitle=episode.getTitleWithKey(episode.getShow().getLanguage());
			if (!full) return episodeTitle;
			else return episode.getShow().getTitle()+" - "+episodeTitle;
		}
		return super.format(value);
	}
}
