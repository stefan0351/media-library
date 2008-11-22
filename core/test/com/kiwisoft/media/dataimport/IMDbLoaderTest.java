package com.kiwisoft.media.dataimport;

import java.util.Locale;
import java.io.File;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.CountryManager;
import junit.framework.TestCase;

/**
 * @author Stefan Stiller
 */
public class IMDbLoaderTest extends TestCase
{
	public IMDbLoaderTest(String string)
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

	public void test_DizzyLieberDizzy() throws Exception
	{
		IMDbComLoader loader=new IMDbComLoader("http://www.imdb.com/title/tt0112877/", "tt0112877");
		MovieData movieData=loader.load();
		System.out.println(movieData);
		assertEquals("Dizzy, lieber Dizzy", movieData.getTitle());
		assertEquals(new Integer(1997), movieData.getYear());
		assertEquals(new Integer(88), movieData.getRuntime());
		assertEquals(1, movieData.getLanguages().size());
		assertTrue(movieData.getLanguages().contains(LanguageManager.getInstance().getLanguageBySymbol("de")));
		assertEquals(1, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(CountryManager.getInstance().getCountryBySymbol("DE")));
		assertEquals("tt0112877", movieData.getImdbKey());
		assertNotNull(movieData.getCreditsLink());
		assertNotNull(movieData.getReleaseInfoLink());
	}

	public void test_ISeeYouCom() throws Exception
	{
		IMDbComLoader loader=new IMDbComLoader("http://www.imdb.com/title/tt0489085/", "tt0489085");
		MovieData movieData=loader.load();
		System.out.println(movieData);
		assertEquals("I-See-You.Com", movieData.getTitle());
		assertEquals(new Integer(2006), movieData.getYear());
		assertEquals(new Integer(92), movieData.getRuntime());
		assertEquals(1, movieData.getLanguages().size());
		assertTrue(movieData.getLanguages().contains(LanguageManager.getInstance().getLanguageBySymbol("en")));
		assertEquals(1, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(CountryManager.getInstance().getCountryBySymbol("US")));
		assertEquals("tt0489085", movieData.getImdbKey());
		assertNotNull(movieData.getCreditsLink());
		assertNotNull(movieData.getReleaseInfoLink());
	}

	public void test_EyeOfTheDolphin() throws Exception
	{
		IMDbComLoader loader=new IMDbComLoader("http://www.imdb.com/title/tt0465407/", "tt0465407");
		MovieData movieData=loader.load();
		System.out.println(movieData);
		assertEquals("Eye of the Dolphin", movieData.getTitle());
		assertEquals(new Integer(2006), movieData.getYear());
		assertEquals(1, movieData.getLanguages().size());
		assertTrue(movieData.getLanguages().contains(LanguageManager.getInstance().getLanguageBySymbol("en")));
		assertEquals(1, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(CountryManager.getInstance().getCountryBySymbol("US")));
		assertEquals("tt0465407", movieData.getImdbKey());
		assertNotNull(movieData.getPlotSummaryLink());
		assertNotNull(movieData.getPlotSynopsisLink());
		assertNotNull(movieData.getCreditsLink());
		assertNotNull(movieData.getReleaseInfoLink());
		assertNotNull(movieData.getSummary());
		assertTrue(movieData.getSummary().startsWith("Fourteen year old Alyssa"));
		assertTrue(movieData.getSummary().endsWith("friend who hold the key."));
	}

	public void test_LuxoJr() throws Exception
	{
		IMDbComLoader loader=new IMDbComLoader("http://www.imdb.com/title/tt0091455/", "tt0091455");
		MovieData movieData=loader.load();
		System.out.println(movieData);
		assertEquals("Luxo Jr.", movieData.getTitle());
		assertEquals("Kleine Lampe, Die", movieData.getGermanTitle());
		assertEquals(new Integer(1986), movieData.getYear());
		assertEquals(new Integer(2), movieData.getRuntime());
		assertEquals(1, movieData.getLanguages().size());
		assertTrue(movieData.getLanguages().contains(LanguageManager.getInstance().getLanguageBySymbol("none")));
		assertEquals(1, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(CountryManager.getInstance().getCountryBySymbol("US")));
		assertEquals("tt0091455", movieData.getImdbKey());
		assertNotNull(movieData.getPlotSummaryLink());
		assertNotNull(movieData.getCreditsLink());
		assertNotNull(movieData.getReleaseInfoLink());
		assertNotNull(movieData.getSummary());
		assertTrue(movieData.getSummary().startsWith("This was Pixar's first attempt"));
		assertTrue(movieData.getSummary().endsWith("[i] Written by LIIT [/i]"));
	}
}
