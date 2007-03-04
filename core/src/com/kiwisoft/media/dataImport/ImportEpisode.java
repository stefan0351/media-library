package com.kiwisoft.media.dataImport;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 12:28:03
 * To change this template use File | Settings | File Templates.
 */
public interface ImportEpisode
{
	String getEpisodeKey();

	String getEpisodeTitle();

	String getOriginalEpisodeTitle();

	Date getFirstAirdate();

	String getProductionCode();
}
