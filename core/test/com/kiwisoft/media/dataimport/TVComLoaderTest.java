package com.kiwisoft.media.dataimport;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.progress.ConsoleProgressListener;
import com.kiwisoft.utils.StringUtils;
import org.htmlparser.util.ParserException;

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
		configuration.loadUserValues("media"+File.separator+"dev-profile.xml");
	}

	public void test_PushingDaisies() throws Exception
	{
		Show show=ShowManager.getInstance().getShowByName("Pushing Daisies");
		assertNotNull(show);

		final Map<String, EpisodeData> episodesMap=new HashMap<String, EpisodeData>();
		EpisodeDataLoader loader=new TVComLoader(show, "http://www.tv.com/pushing-daisies/show/68663/episode_listings.html", 1, 2, false)
		{
			@Override
			protected void saveEpisode(EpisodeData data) throws IOException, InterruptedException, ParserException
			{
				if (data.getFirstAirdate()==null || data.getFirstAirdate().before(new Date()))
				{
					if (!StringUtils.isEmpty(data.getLink(EpisodeData.DETAILS_LINK)))
					{
						loadDetails(data);
					}
					Thread.sleep(300); // To avoid DOS on the server
				}
				episodesMap.put(data.getKey(), data);
			}

			@Override
			protected Episode createEpisode(Show show, EpisodeData info)
			{
				return null;
			}

			@Override
			public void saveEpisode(final Episode episode, final EpisodeData data)
			{
			}
		};
		loader.run(new ConsoleProgressListener());

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
		assertEquals("14383", episode.getWrittenBy().get(0).getPerson().getKey());
		assertEquals("Bryan Fuller", episode.getWrittenBy().get(0).getPerson().getName());
		assertEquals(1, episode.getDirectedBy().size());
		assertEquals("51311", episode.getDirectedBy().get(0).getPerson().getKey());
		assertEquals("Barry Sonnenfeld", episode.getDirectedBy().get(0).getPerson().getName());

		episode=episodesMap.get("1.2");
		assertTrue(episode.getEnglishSummary().startsWith("Ned sees his power"));
		assertTrue(episode.getEnglishSummary().endsWith("order to get the reward."));

		episode=episodesMap.get("1.6");
		assertEquals("582600", episode.getWrittenBy().get(0).getPerson().getKey());
		assertEquals("Dara Resnik Creasey", episode.getWrittenBy().get(0).getPerson().getName());
		assertEquals("582599", episode.getWrittenBy().get(1).getPerson().getKey());
		assertEquals("Chad Gomez Creasey", episode.getWrittenBy().get(1).getPerson().getName());

	}
}
