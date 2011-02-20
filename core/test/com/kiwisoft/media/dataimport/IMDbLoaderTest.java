package com.kiwisoft.media.dataimport;

import java.util.Locale;
import java.io.File;

import com.kiwisoft.cfg.SimpleConfiguration;
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
		ImportUtils.USE_CACHE=true;
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		configuration.loadDefaultsFromFile(new File("conf", "config-dev.xml"));
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
		assertTrue(movieData.getLanguages().contains(new LanguageData("German")));
		assertEquals(1, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(new CountryData("Germany")));
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
		assertTrue(movieData.getLanguages().contains(new LanguageData("English")));
		assertEquals(1, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(new CountryData("USA")));
		assertEquals("tt0489085", movieData.getImdbKey());
		assertNotNull(movieData.getCreditsLink());
		assertNotNull(movieData.getReleaseInfoLink());
		assertTrue(movieData.getOutline().startsWith("A 17-year-old boy buys mini-cameras and displays"));
		assertTrue(movieData.getOutline().endsWith("until it all comes crashing down...."));
		assertEquals(movieData.getOutline(), movieData.getSummary());
	}

	public void test_EyeOfTheDolphin() throws Exception
	{
		IMDbComLoader loader=new IMDbComLoader("http://www.imdb.com/title/tt0465407/", "tt0465407");
		MovieData movieData=loader.load();
		System.out.println(movieData);
		assertEquals("Eye of the Dolphin", movieData.getTitle());
		assertEquals(new Integer(2006), movieData.getYear());
		assertEquals(1, movieData.getLanguages().size());
		assertTrue(movieData.getLanguages().contains(new LanguageData("English")));
		assertEquals(1, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(new CountryData("USA")));
		assertEquals("tt0465407", movieData.getImdbKey());
		assertNotNull(movieData.getPlotSummaryLink());
		assertNotNull(movieData.getPlotSynopsisLink());
		assertNotNull(movieData.getCreditsLink());
		assertNotNull(movieData.getReleaseInfoLink());
		assertNotNull(movieData.getSummary());
		assertTrue(movieData.getOutline().startsWith("Alyssa is a troubled 14-year old, "));
		assertTrue(movieData.getSummary().startsWith("Fourteen year old Alyssa"));
		assertTrue(movieData.getSummary().endsWith("friend who hold the key."));
	}

	public void test_LuxoJr() throws Exception
	{
		IMDbComLoader loader=new IMDbComLoader("http://www.imdb.com/title/tt0091455/", "tt0091455");
		MovieData movieData=loader.load();
		System.out.println(movieData);
		assertEquals("Luxo Jr.", movieData.getTitle());
		assertEquals("Die kleine Lampe", movieData.getGermanTitle());
		assertEquals(new Integer(1986), movieData.getYear());
		assertEquals(new Integer(2), movieData.getRuntime());
		assertEquals(1, movieData.getLanguages().size());
		assertTrue(movieData.getLanguages().contains(new LanguageData("None")));
		assertEquals(1, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(new CountryData("USA")));
		assertEquals("tt0091455", movieData.getImdbKey());
		assertNotNull(movieData.getPlotSummaryLink());
		assertNotNull(movieData.getCreditsLink());
		assertNotNull(movieData.getReleaseInfoLink());
		assertNotNull(movieData.getSummary());
		assertTrue(movieData.getOutline().startsWith("This was Pixar's first attempt"));
		assertTrue(movieData.getSummary().startsWith("This was Pixar's first attempt"));
		assertTrue(movieData.getSummary().endsWith("[i] Written by LIIT [/i]"));
	}

	public void test_WildChild() throws Exception
	{
		IMDbComLoader loader=new IMDbComLoader("http://www.imdb.com/title/tt1024255/", "tt1024255");
		MovieData movieData=loader.load();
		System.out.println(movieData);
		assertEquals("Wild Child", movieData.getTitle());
		assertEquals("Wild Child - Erstklassig zickig", movieData.getGermanTitle());
		assertEquals(new Integer(2008), movieData.getYear());
		assertEquals(new Integer(98), movieData.getRuntime());
		assertEquals(1, movieData.getLanguages().size());
		assertTrue(movieData.getLanguages().contains(new LanguageData("English")));
		assertEquals(3, movieData.getCountries().size());
		assertTrue(movieData.getCountries().contains(new CountryData("France")));
		assertTrue(movieData.getCountries().contains(new CountryData("USA")));
		assertTrue(movieData.getCountries().contains(new CountryData("UK")));
		assertEquals("tt1024255", movieData.getImdbKey());
		assertNotNull(movieData.getCreditsLink());
		assertNotNull(movieData.getReleaseInfoLink());
		assertNotNull(movieData.getSummary());
		assertTrue(movieData.getOutline().startsWith("Since Malibu brat Poppy Moore's ma left,"));
		assertTrue(movieData.getSummary().startsWith("Sixteen-year-old Poppy Moore"));
		assertTrue(movieData.getSummary().endsWith("Poppy will remain at Abbey Mount."));
	}
}
