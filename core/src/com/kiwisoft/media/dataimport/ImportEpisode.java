package com.kiwisoft.media.dataImport;

import java.util.Date;

/**
 * @author Stefan Stiller
 */
public interface ImportEpisode
{
	String getKey();

	String getGermanTitle();

	String getTitle();

	Date getFirstAirdate();

	String getProductionCode();
}
