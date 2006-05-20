/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 3:41:18 PM
 */
package com.kiwisoft.media.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.kiwisoft.media.dataImport.EpisodeFormatter;
import com.kiwisoft.media.ui.EpisodeFormatterDialog;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

public class FormatEpisodesAction extends AbstractAction
{
	private JFrame parent;

	public FormatEpisodesAction(JFrame frame)
	{
		super("Konvertiere Episoden");
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Configurator configurator=Configurator.getInstance();
		String source=configurator.getString("path.import.episodes.source", "");
		String target=configurator.getString("path.import.episodes.target", "");
		EpisodeFormatterDialog dialog=new EpisodeFormatterDialog(parent, source, target);
		dialog.show();
		if (dialog.getValue())
		{
			source=dialog.getSource();
			configurator.setString("path.import.episodes.source", source);
			target=dialog.getTarget();
			configurator.setString("path.import.episodes.target", target);
			configurator.saveUserValues();
			EpisodeFormatter episodeImport=new EpisodeFormatter(source, target);
			ProgressDialog progressDialog=new ProgressDialog(parent, episodeImport);
			progressDialog.show();
		}
	}
}
