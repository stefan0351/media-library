package com.kiwisoft.media;

import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.utils.Configurator;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 10:52:07
 * To change this template use File | Settings | File Templates.
 */
public class MediaTableConfiguration extends TableConfiguration
{
	public MediaTableConfiguration(String key)
	{
		super(Configurator.getInstance(), MediaTableConfiguration.class, key);
	}
}
