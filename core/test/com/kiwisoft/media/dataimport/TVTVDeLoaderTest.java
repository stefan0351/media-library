package com.kiwisoft.media.dataimport;

import java.util.Locale;
import java.util.Collections;
import java.io.File;

import junit.framework.TestCase;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.progress.ConsoleProgressListener;

/**
 * @author Stefan Stiller
 */
public class TVTVDeLoaderTest extends TestCase
{
	public TVTVDeLoaderTest(String string)
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

	public void test_Frasier() throws Exception
	{
		Show show=DBLoader.getInstance().load(Show.class, null, "title=?", "Frasier");
		TVTVDeLoader loader=new TVTVDeLoader(Collections.<Object>singletonList(show));
		assertTrue(loader.run(new ConsoleProgressListener()));
	}

	public void test_MileyCyrus() throws Exception
	{
		Person person=DBLoader.getInstance().load(Person.class, null, "name=?", "Miley Cyrus");
		TVTVDeLoader loader=new TVTVDeLoader(Collections.<Object>singletonList(person));
		assertTrue(loader.run(new ConsoleProgressListener()));
	}

	public void test_AlysonStoner() throws Exception
	{
		Person person=DBLoader.getInstance().load(Person.class, null, "name=?", "Alyson Stoner");
		TVTVDeLoader loader=new TVTVDeLoader(Collections.<Object>singletonList(person));
		assertTrue(loader.run(new ConsoleProgressListener()));
	}
}
