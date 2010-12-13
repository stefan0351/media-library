package com.kiwisoft.media.tools;

import com.kiwisoft.cfg.SimpleConfiguration;

import java.io.File;

/**
 * @author Stefan Stiller
 * @since 11.12.2010
 */
public class Converter
{
	public static void main(String[] args)
	{
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config-dev.xml");
		configuration.loadDefaultsFromFile(configFile);

		run();
	}

	private static void run()
	{
	}

	private Converter()
	{
	}
}
