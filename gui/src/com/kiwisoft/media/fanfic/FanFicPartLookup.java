package com.kiwisoft.media.fanfic;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.cfg.Configuration;

public class FanFicPartLookup extends FileLookup
{
	public FanFicPartLookup()
	{
		super(JFileChooser.FILES_ONLY, false);
	}

	public String getCurrentDirectory()
	{
		return MediaConfiguration.getRecentFanFicPath();
	}

	public void setCurrentDirectory(String path)
	{
		MediaConfiguration.setRecentFanFicPath(path);
		Configuration.getInstance().saveUserValues();
	}

	public Icon getIcon()
	{
		return Icons.getIcon("lookup.table");
	}
}
