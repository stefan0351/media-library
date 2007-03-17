package com.kiwisoft.media;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.lookup.FileLookup;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 15.04.2004
 * Time: 19:56:11
 * To change this template use File | Settings | File Templates.
 */
public class WebFileLookup extends FileLookup
{
	private boolean table;

	public WebFileLookup(boolean table)
	{
		super(JFileChooser.FILES_ONLY, false);
		this.table=table;
	}

	public String getCurrentDirectory()
	{
		Configurator configurator=Configurator.getInstance();
		return configurator.getString("path.web.current", configurator.getString("path.root"));
	}

	public void setCurrentDirectory(String path)
	{
		Configurator.getInstance().setString("path.web.current", path);
		Configurator.getInstance().saveUserValues();
	}

	public Icon getIcon()
	{
		if (table) return Icons.getIcon("lookup.table");
		return Icons.getIcon("lookup.file");
	}
}
