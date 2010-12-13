package com.kiwisoft.media.dataimport;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.File;

import junit.framework.TestCase;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.progress.ConsoleProgressListener;
import com.kiwisoft.progress.ProgressSupport;

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
		ImportUtils.USE_CACHE=true;
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		configuration.loadDefaultsFromFile(new File("conf", "config-dev.xml"));
	}

	public void test_DoctorWho() throws Exception
	{
		SerienJunkiesDeLoader loader=new SerienJunkiesDeLoader("http://www.serienjunkies.de/drwho/episoden.html");

		ProgressSupport progressSupport=new ProgressSupport(null, new ConsoleProgressListener());
		List<EpisodeData> episodeList=loader.loadList(progressSupport);
		progressSupport.info("Found "+episodeList.size()+" episodes");
		Map<String, EpisodeData> episodesMap=new HashMap<String, EpisodeData>();
		for (EpisodeData episodeData : episodeList)
		{
			loader.loadDetails(progressSupport, episodeData);
			episodesMap.put(episodeData.getKey(), episodeData);
			progressSupport.info("Loaded details for "+episodeData.getTitle());
			Thread.sleep(500);
		}

		assertEquals(70, episodesMap.size());

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
