package com.kiwisoft.media.dataimport;

import com.kiwisoft.cfg.SimpleConfiguration;
import junit.framework.TestCase;

import java.io.File;
import java.util.Locale;

/**
 * @author Stefan Stiller
 * @since 02.10.11
 */
public class FanFictionNetLoaderTest extends TestCase
{
	@Override
	 protected void setUp() throws Exception
	 {
		 super.setUp();
		 ImportUtils.USE_CACHE=true;
		 Locale.setDefault(Locale.UK);
		 SimpleConfiguration configuration=new SimpleConfiguration();
		 configuration.loadDefaultsFromFile(new File("conf", "config-dev.xml"));
	 }

	public void testLoadFanFic() throws Exception
	{
		FanFictionNetLoader loader=new FanFictionNetLoader("http://www.fanfiction.net/s/5907639/1/A_Jump_to_the_Left");
		FanFicData data=loader.getInfo();
		System.out.println("data = "+data);
		assertNotNull(data);
		assertEquals("A Jump to the Left", data.getTitle());
		assertEquals("Ry-Rain", data.getAuthor());
		assertEquals("http://www.fanfiction.net/u/646765", data.getAuthorUrl());
		assertEquals(1, data.getChapterCount());
		assertEquals(true, data.isComplete());
		assertTrue(data.getDomains().contains("Hannah Montana"));
		assertEquals("K+", data.getRating());
	}

	public void testLoadCrossoverFanFic() throws Exception
	{
		FanFictionNetLoader loader=new FanFictionNetLoader("http://www.fanfiction.net/s/7325520/5/Red");
		FanFicData data=loader.getInfo();
		assertNotNull(data);
		System.out.println("data = "+data);
	}
}
