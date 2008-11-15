package com.kiwisoft.media.dataimport;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

import junit.framework.TestCase;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.dataImport.ImportEpisode;
import com.kiwisoft.media.dataImport.SerienJunkiesDeLoader;
import com.kiwisoft.media.dataImport.EpisodeData;
import com.kiwisoft.progress.ConsoleProgressListener;

/**
 * @author Stefan Stiller
 */
public class SerienJunkiesDeLoaderTest extends TestCase
{
	public SerienJunkiesDeLoaderTest(String string)
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

	public void test_DoctorWho() throws Exception
	{
		Show show=ShowManager.getInstance().getShowByName("Doctor Who");
		assertNotNull(show);

		final Map<String, EpisodeData> episodesMap=new HashMap<String, EpisodeData>();
		SerienJunkiesDeLoader loader=new SerienJunkiesDeLoader(show, "http://www.serienjunkies.de/drwho/episoden.html", 3, 3, false)
		{
			protected Episode createEpisode(Show show, ImportEpisode info)
			{
				return null;
			}

			@Override
			public void saveEpisode(final Episode episode, final EpisodeData data)
			{
				episodesMap.put(data.getKey(), data);
				System.out.println("episode = "+episode);
				System.out.println("data.key = "+data.getKey());
				System.out.println("data.title = "+data.getTitle());
				System.out.println("data.germanTitle = "+data.getGermanTitle());
				System.out.println("data.firstAirdate = "+data.getFirstAirdate());
				System.out.println("data.germanSummary = "+data.getGermanSummary());
			}
		};
		loader.run(new ConsoleProgressListener());

//		assertEquals(12, episodesMap.size());
//		for (int i=1;i<=9;i++) assertTrue(episodesMap.containsKey("1."+i));
//
//		TVComLoader.EpisodeData episode=episodesMap.get("1.1");
//		assertEquals("Pie-lette", episode.getEpisodeTitle());
//		assertEquals("3.10.2007", new SimpleDateFormat("d.M.yyyy").format(episode.getFirstAirdate()));
//		assertEquals("276027", episode.getProductionCode());
//		assertTrue(episode.getEnglishSummary().startsWith("Ned works"));
//		assertTrue(episode.getEnglishSummary().endsWith("let her keep living."));
//		assertEquals(1, episode.getWrittenBy().size());
//		assertEquals("14383", episode.getWrittenBy().get(0).getKey());
//		assertEquals("Bryan Fuller", episode.getWrittenBy().get(0).getName());
//		assertEquals(1, episode.getDirectedBy().size());
//		assertEquals("51311", episode.getDirectedBy().get(0).getKey());
//		assertEquals("Barry Sonnenfeld", episode.getDirectedBy().get(0).getName());
//		assertEquals(7, episode.getMainCast().size());
//		assertEquals(5, episode.getRecurringCast().size());
//		assertEquals(11, episode.getGuestCast().size());
//
//		episode=episodesMap.get("1.2");
//		assertTrue(episode.getEnglishSummary().startsWith("Ned sees his power"));
//		assertTrue(episode.getEnglishSummary().endsWith("order to get the reward."));
//
//		episode=episodesMap.get("1.6");
//		assertEquals("582600", episode.getWrittenBy().get(0).getKey());
//		assertEquals("Dara Resnik Creasey", episode.getWrittenBy().get(0).getName());
//		assertEquals("582599", episode.getWrittenBy().get(1).getKey());
//		assertEquals("Chad Gomez Creasey", episode.getWrittenBy().get(1).getName());
	}
}
