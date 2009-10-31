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
		configuration.loadUserValues("media"+File.separator+"dev-profile.xml");
	}

	public void test_DoctorWho() throws Exception
	{
		Show show=ShowManager.getInstance().getShowByName("Doctor Who");
		assertNotNull(show);

		final Map<String, EpisodeData> episodesMap=new HashMap<String, EpisodeData>();
		SerienJunkiesDeLoader loader=new SerienJunkiesDeLoader(show, "http://www.serienjunkies.de/drwho/episoden.html", 1, 4, false)
		{
			@Override
			protected Episode createEpisode(Show show, EpisodeData info)
			{
				return null;
			}

			@Override
			public void saveEpisode(final Episode episode, final EpisodeData data)
			{
				episodesMap.put(data.getKey(), data);
			}
		};
		loader.run(new ConsoleProgressListener());

		assertEquals(56, episodesMap.size());

		EpisodeData episode=episodesMap.get("1.1");
		assertEquals("Rose", episode.getTitle());
		assertEquals("Rose", episode.getGermanTitle());
		assertNotNull(episode.getLink(EpisodeData.DETAILS_LINK));
		assertTrue(episode.getGermanSummary().startsWith("Rose Tyler arbeitet"));
		assertTrue(episode.getGermanSummary().endsWith("Quelle: ProSieben"));

		episode=episodesMap.get("1.2");
		assertEquals("The End of the World", episode.getTitle());
		assertEquals("Das Ende der Welt", episode.getGermanTitle());
		assertNotNull(episode.getLink(EpisodeData.DETAILS_LINK));
		assertTrue(episode.getGermanSummary().startsWith("Um Rose zu beeindrucken"));
		assertTrue(episode.getGermanSummary().endsWith("Quelle: ProSieben"));

		episode=episodesMap.get("2.7");
		assertEquals("The Idiot's Lantern", episode.getTitle());
		assertEquals("Die Glotze", episode.getGermanTitle());
		assertNotNull(episode.getLink(EpisodeData.DETAILS_LINK));
		assertTrue(episode.getGermanSummary().startsWith("London, im Jahr 1952:"));
		assertTrue(episode.getGermanSummary().endsWith("Quelle: ProSieben"));

		episode=episodesMap.get("4.1");
		assertNotNull(episode.getLink(EpisodeData.DETAILS_LINK));
		assertTrue(episode.getGermanSummary().startsWith("Nachdem sie im Weihnachtsspecial"));
		assertTrue(episode.getGermanSummary().endsWith("der Doktor hat Verdacht geschöpft..."));
	}
}
