/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 8, 2003
 * Time: 6:23:39 PM
 */
package com.kiwisoft.media.db;

import java.io.File;

import com.kiwisoft.utils.db.DBMappingTest;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.cfg.SimpleConfiguration;

public class MappingTest extends DBMappingTest
{
	public MappingTest(String s)
	{
		super(s);
	}

	protected void configure()
	{
		new SimpleConfiguration().loadDefaultsFromFile(new File(FileUtils.getRootDirectory(getClass()), "config.xml"));
	}
}
