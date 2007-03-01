/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 8, 2003
 * Time: 6:23:39 PM
 */
package com.kiwisoft.media.db;

import java.io.File;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBMappingTest;

public class MappingTest extends DBMappingTest
{
	public MappingTest(String s)
	{
		super(s);
	}

	protected void configure()
	{
		Configurator configurator=Configurator.getInstance();
		configurator.determineBaseDirectory(getClass());
		configurator.loadDefaultsFromFile(new File(Configurator.getInstance().getApplicationBase(), "config.xml"));
	}
}
