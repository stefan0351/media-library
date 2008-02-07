package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.LinkManager;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.WebUtils;

/**
 * @author Stefan Stiller
 */
public class ExportLinksAction extends ContextAction
{
	private ApplicationFrame frame;

	public ExportLinksAction(ApplicationFrame frame)
	{
		super("Export", Icons.getIcon("export"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION)
		{
			File file=fileChooser.getSelectedFile();
			try
			{
				LinkManager.getInstance().exportLinks(file);
				WebUtils.openURL(file.toURI().toURL());
			}
			catch (IOException e1)
			{
				GuiUtils.handleThrowable(frame, e1);
			}
		}
	}
}
