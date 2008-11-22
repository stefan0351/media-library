package com.kiwisoft.media.dataimport;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.progress.ConsoleProgressListener;

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
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		configuration.loadDefaultsFromFile(configFile);
	}

	public void test_PushingDaisies() throws Exception
	{
		Show show=ShowManager.getInstance().getShowByName("Pushing Daisies");
		assertNotNull(show);

		final Map<String, EpisodeData> episodesMap=new HashMap<String, EpisodeData>();
		EpisodeDataLoader loader=new TVComLoader(show, "http://www.tv.com/pushing-daisies/show/68663/episode_listings.html", 1, 2, false)
		{
			protected Episode createEpisode(Show show, EpisodeData info)
			{
				return null;
			}

			@Override
			public void saveEpisode(final Episode episode, final EpisodeData data)
			{
				episodesMap.put(data.getKey(), data);
				System.out.println("episode = "+episode);
				System.out.println("data.episodeKey = "+data.getKey());
				System.out.println("data.episodeTitle = "+data.getTitle());
				System.out.println("data.firstAirdate = "+data.getFirstAirdate());
				System.out.println("data.productionCode = "+data.getProductionCode());
				System.out.println("data.summary = "+data.getEnglishSummary());
				System.out.println("data.writtenBy = "+data.getWrittenBy());
				System.out.println("data.directedBy = "+data.getDirectedBy());
				System.out.println("data.mainCast = "+data.getMainCast());
				System.out.println("data.recurringCast = "+data.getRecurringCast());
				System.out.println("data.guestCast = "+data.getGuestCast());
			}
		};
		loader.run(new ConsoleProgressListener());

		assertEquals(14, episodesMap.size());
		for (int i=1;i<=9;i++) assertTrue(episodesMap.containsKey("1."+i));

		EpisodeData episode=episodesMap.get("1.1");
		assertEquals("Pie-lette", episode.getTitle());
		assertEquals("3.10.2007", new SimpleDateFormat("d.M.yyyy").format(episode.getFirstAirdate()));
		assertEquals("276027", episode.getProductionCode());
		assertTrue(episode.getEnglishSummary().startsWith("Ned works"));
		assertTrue(episode.getEnglishSummary().endsWith("let her keep living."));
		assertEquals(1, episode.getWrittenBy().size());
		assertEquals("14383", episode.getWrittenBy().get(0).getKey());
		assertEquals("Bryan Fuller", episode.getWrittenBy().get(0).getName());
		assertEquals(1, episode.getDirectedBy().size());
		assertEquals("51311", episode.getDirectedBy().get(0).getKey());
		assertEquals("Barry Sonnenfeld", episode.getDirectedBy().get(0).getName());
		assertEquals(7, episode.getMainCast().size());
		assertEquals(5, episode.getRecurringCast().size());
		assertEquals(11, episode.getGuestCast().size());

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
