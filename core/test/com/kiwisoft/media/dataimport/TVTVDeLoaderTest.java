package com.kiwisoft.media.dataimport;

import java.util.Locale;
import java.util.Collections;
import java.io.File;

import junit.framework.TestCase;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.media.dataImport.TVTVDeLoader;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.persistence.DBLoader;

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

	public void test_PushingDaisies() throws Exception
	{
		File output=new File(System.getProperty("user.home"), ".kiwisoft"+File.separator+"temp");
		output.mkdirs();
		Show show=DBLoader.getInstance().load(Show.class, null, "title=?", "Pushing Daisies");
		TVTVDeLoader loader=new TVTVDeLoader(Collections.singletonList(show));
		assertTrue(loader.run(null));
	}

	public void test_KirstenDunst() throws Exception
	{
		File output=new File(System.getProperty("user.home"), ".kiwisoft"+File.separator+"temp");
		output.mkdirs();
		Person person=DBLoader.getInstance().load(Person.class, null, "name=?", "Kirsten Dunst");
		TVTVDeLoader loader=new TVTVDeLoader(Collections.singletonList(person));
		assertTrue(loader.run(null));
	}
}