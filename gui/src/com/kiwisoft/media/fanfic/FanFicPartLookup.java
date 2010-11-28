package com.kiwisoft.media.fanfic;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.lookup.FileLookup;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.cfg.Configuration;

public class FanFicPartLookup extends FileLookup
{
	public FanFicPartLookup()
	{
		super(JFileChooser.FILES_ONLY, false);
	}

	@Override
	public String getCurrentDirectory()
	{
		return MediaConfiguration.getRecentFanFicPath();
	}

	@Override
	public void setCurrentDirectory(String path)
	{
		MediaConfiguration.setRecentFanFicPath(path);
		Configuration.getInstance().saveUserValues();
	}
}
