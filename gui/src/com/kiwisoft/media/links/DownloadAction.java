package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.LinkManager;
import com.kiwisoft.media.download.DownloadFrame;
import com.kiwisoft.media.download.DownloadProject;
import com.kiwisoft.utils.WebUtils;

/**
 * @author Stefan Stiller
 */
public class DownloadAction extends ContextAction
{
	private ApplicationFrame frame;

	public DownloadAction(ApplicationFrame frame)
	{
		super("Download", Icons.getIcon("download"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		new DownloadFrame(new DownloadProject()).setVisible(true);
	}
}
