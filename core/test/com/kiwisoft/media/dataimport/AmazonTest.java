package com.kiwisoft.media.dataimport;

import com.kiwisoft.cfg.SimpleConfiguration;
import junit.framework.TestCase;

import java.io.File;
import java.util.Locale;

/**
 * @author Stefan Stiller
 */
public class AmazonTest extends TestCase
{
    public AmazonTest(String string)
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

	public void test_SabineBlazy_DieSchwarzeBucht() throws Exception
	{
		AmazonDeLoader loader=new AmazonDeLoader("978-3-522-18165-5");
		BookData bookData=loader.load();
		System.out.println(bookData);
		assertNotNull(bookData);
		assertEquals("Paula Pepper ermittelt: Die schwarze Bucht", bookData.getTitle());
		assertEquals("[Sabine Blazy]", bookData.getAuthors().toString());
		assertEquals("[]", bookData.getTranslators().toString());
		assertEquals(252, bookData.getPageCount());
		assertEquals("Gebundene Ausgabe", bookData.getBinding());
		assertEquals("Thienemann Verlag", bookData.getPublisher());
		assertEquals(2009, bookData.getPublishedYear());
		assertEquals("3522181654", bookData.getIsbn10());
		assertEquals("978-3522181655", bookData.getIsbn13());
		assertNotNull(bookData.getLanguage());
		assertEquals("de", bookData.getLanguage().getSymbol());
		assertNotNull(bookData.getImageFile());
		assertNotNull(bookData.getSummary());
		assertTrue(bookData.getSummary().startsWith("Drohend erheben sich die Klippen"));
		assertTrue(bookData.getSummary().endsWith("einer Vorliebe für Gänsehaut!"));
		Thread.sleep(1000);
	}

    public void test_TerryPrachett_SchoeneScheine() throws Exception
    {
        AmazonDeLoader loader=new AmazonDeLoader("3442546311");
        BookData bookData=loader.load();
        System.out.println(bookData);
        assertNotNull(bookData);
        assertEquals("Schöne Scheine: Ein Scheibenwelt-Roman", bookData.getTitle());
        assertEquals("[Terry Pratchett]", bookData.getAuthors().toString());
        assertEquals("[Bernhard Kempen]", bookData.getTranslators().toString());
        assertEquals(416, bookData.getPageCount());
        assertEquals("Gebundene Ausgabe", bookData.getBinding());
        assertEquals("Manhattan HC", bookData.getPublisher());
        assertEquals(2007, bookData.getPublishedYear());
        assertEquals("3442546311", bookData.getIsbn10());
        assertEquals("978-3442546312", bookData.getIsbn13());
		assertNotNull(bookData.getLanguage());
		assertEquals("de", bookData.getLanguage().getSymbol());
        assertNotNull(bookData.getImageFile());
        assertNotNull(bookData.getSummary());
        assertTrue(bookData.getSummary().startsWith("Nachdem Ankh-Morpork in"));
        assertTrue(bookData.getSummary().endsWith(" dafür findet. [i]-- Steffi Pritzens[/i]"));
        Thread.sleep(1000);
    }

    public void test_WolfgangHohlbein_SaintNick() throws Exception
    {
        AmazonDeLoader loader=new AmazonDeLoader("3453134575");
        BookData bookData=loader.load();
        System.out.println(bookData);
        assertNotNull(bookData);
        assertEquals("Saint Nick: Roman: Der Tag, an dem der Weihnachtsmann durchdrehte", bookData.getTitle());
        assertEquals("[Wolfgang Hohlbein]", bookData.getAuthors().toString());
        assertEquals(318, bookData.getPageCount());
        assertEquals("Taschenbuch", bookData.getBinding());
        assertEquals("Heyne Verlag", bookData.getPublisher());
        assertEquals(1997, bookData.getPublishedYear());
        assertEquals("3453134575", bookData.getIsbn10());
        assertEquals("978-3453134577", bookData.getIsbn13());
        assertNotNull(bookData.getImageFile());
        Thread.sleep(1000);
    }

    public void testTheRainbowCedar() throws Exception
    {
        AmazonDeLoader loader=new AmazonDeLoader("978-1-59493-124-6");
        BookData bookData=loader.load();
        System.out.println(bookData);
        assertNotNull(bookData);
//        assertEquals("Saint Nick: Der Tag, an dem der Weihnachtsmann durchdrehte", bookData.getTitle());
//        assertEquals("[Wolfgang Hohlbein]", bookData.getAuthors().toString());
//        assertEquals(318, bookData.getPageCount());
//        assertEquals("Taschenbuch", bookData.getBinding());
//        assertEquals("Heyne", bookData.getPublisher());
//        assertEquals(1997, bookData.getPublishedYear());
//        assertEquals("3453134575", bookData.getIsbn10());
//        assertEquals("9783453134577", bookData.getIsbn13());
//        assertNotNull(bookData.getImageFile());
        Thread.sleep(1000);
    }
//
    public void testBetweenWorlds() throws Exception
    {
        AmazonDeLoader loader=new AmazonDeLoader("0-689-85792-6");
        BookData bookData=loader.load();
        System.out.println(bookData);
        assertNotNull(bookData);
//        assertEquals("Saint Nick: Der Tag, an dem der Weihnachtsmann durchdrehte", bookData.getTitle());
//        assertEquals("[Wolfgang Hohlbein]", bookData.getAuthors().toString());
//        assertEquals(318, bookData.getPageCount());
//        assertEquals("Taschenbuch", bookData.getBinding());
//        assertEquals("Heyne", bookData.getPublisher());
//        assertEquals(1997, bookData.getPublishedYear());
//        assertEquals("3453134575", bookData.getIsbn10());
//        assertEquals("9783453134577", bookData.getIsbn13());
//        assertNotNull(bookData.getImageFile());
        Thread.sleep(1000);
    }
}
