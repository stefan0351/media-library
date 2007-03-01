package com.kiwisoft.media.fanfic;

import javax.swing.JFileChooser;
import javax.swing.Icon;

import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.Configurator;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 15.04.2004
 * Time: 19:56:11
 * To change this template use File | Settings | File Templates.
 */
public class FanFicPartLookup extends FileLookup
{
	public FanFicPartLookup()
	{
		super(JFileChooser.FILES_ONLY, false);
	}

	public String getCurrentDirectory()
	{
		Configurator configurator=Configurator.getInstance();
		return configurator.getString("path.fanfics.current", configurator.getString("path.fanfics"));
	}

	public void setCurrentDirectory(String path)
	{
		Configurator.getInstance().setString("path.fanfics.current", path);
		Configurator.getInstance().saveUserValues();
	}

	public Icon getIcon()
	{
		return IconManager.getIcon("com/kiwisoft/utils/icons/tlookup.gif");
	}
}
