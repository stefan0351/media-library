package com.kiwisoft.media.dataimport;

import com.kiwisoft.progress.ProgressSupport;

import java.util.List;

/**
 * @author Stefan Stiller
 * @since 03.04.2010
 */
public interface EpisodeLoader
{
	String getName();

	boolean hasGermanData();

	List<EpisodeData> loadList(ProgressSupport progressSupport) throws Exception;

	void loadDetails(ProgressSupport progressSupport, EpisodeData episodeData) throws Exception;

}
