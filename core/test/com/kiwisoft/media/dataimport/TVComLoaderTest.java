package com.kiwisoft.media.dataimport;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.progress.ConsoleProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import junit.framework.TestCase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Stefan Stiller
 */
public class TVComLoaderTest extends TestCase
{
	public TVComLoaderTest(String string)
	{
		super(string);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ImportUtils.USE_CACHE=true;
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		configuration.loadDefaultsFromFile(configFile);
		configuration.loadUserValues("media"+File.separator+"dev-profile.xml");
	}

	public void test_PushingDaisies() throws Exception
	{
		EpisodeDataLoader2 loader=new TVComLoader2("http://www.tv.com/pushing-daisies/show/68663/episode_listings.html");

		ProgressSupport progressSupport=new ProgressSupport(null, new ConsoleProgressListener());
		List<EpisodeData> episodeList=loader.loadList(progressSupport);
		Map<String, EpisodeData> episodesMap=new HashMap<String, EpisodeData>();
		for (EpisodeData episodeData : episodeList)
		{
			loader.loadDetails(progressSupport, episodeData);
			episodesMap.put(episodeData.getKey(), episodeData);
			Thread.sleep(500);
		}

		assertEquals(22, episodesMap.size());

		EpisodeData episode=episodesMap.get("1.1");
		assertEquals("Pie-lette", episode.getTitle());
		assertEquals("3.10.2007", new SimpleDateFormat("d.M.yyyy").format(episode.getFirstAirdate()));
		assertEquals("276027", episode.getProductionCode());
		assertTrue(episode.getEnglishSummary().startsWith("Ned works"));
		assertTrue(episode.getEnglishSummary().endsWith("let her keep living."));
		assertEquals(7, episode.getMainCast().size());
		assertEquals(5, episode.getRecurringCast().size());
		assertEquals(11, episode.getGuestCast().size());
		assertEquals(1, episode.getWrittenBy().size());
		assertEquals("14383", episode.getWrittenBy().get(0).getKey());
		assertEquals("Bryan Fuller", episode.getWrittenBy().get(0).getName());
		assertEquals(1, episode.getDirectedBy().size());
		assertEquals("51311", episode.getDirectedBy().get(0).getKey());
		assertEquals("Barry Sonnenfeld", episode.getDirectedBy().get(0).getName());

		episode=episodesMap.get("1.2");
		assertTrue(episode.getEnglishSummary().startsWith("Ned sees his power"));
		assertTrue(episode.getEnglishSummary().endsWith("order to get the reward."));

		episode=episodesMap.get("1.6");
		assertEquals("582600", episode.getWrittenBy().get(0).getKey());
		assertEquals("Dara Resnik Creasey", episode.getWrittenBy().get(0).getName());
		assertEquals("582599", episode.getWrittenBy().get(1).getKey());
		assertEquals("Chad Gomez Creasey", episode.getWrittenBy().get(1).getName());

	}
}
