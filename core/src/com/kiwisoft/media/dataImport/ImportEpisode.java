package com.kiwisoft.media.dataImport;

import java.util.Date;

/**
 * @author Stefan Stiller
 */
public interface ImportEpisode
{
	String getEpisodeKey();

	String getGermanEpisodeTitle();

	String getEpisodeTitle();

	Date getFirstAirdate();

	String getProductionCode();
}
