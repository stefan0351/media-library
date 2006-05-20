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
import com.kiwisoft.media.dataImport.OldEpisodeFormatter;
import com.kiwisoft.media.ui.EpisodeFormatterDialog;
import com.kiwisoft.media.ui.OldEpisodeFormatterDialog;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Language;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

public class FormatOldEpisodesAction extends AbstractAction
{
	private JFrame parent;

	public FormatOldEpisodesAction(JFrame frame)
	{
		super("Konvertiere alte Episoden");
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Configurator configurator=Configurator.getInstance();
		String source=configurator.getString("path.import.episodes.source", "");
		String target=configurator.getString("path.import.episodes.target", "");
		OldEpisodeFormatterDialog dialog=new OldEpisodeFormatterDialog(parent, source, target);
		dialog.show();
		if (dialog.getValue())
		{
			source=dialog.getSource();
			configurator.setString("path.import.episodes.source", source);
			target=dialog.getTarget();
			configurator.setString("path.import.episodes.target", target);
			configurator.saveUserValues();
			Show show=dialog.getShow();
			Language language=dialog.getLanguage();
			OldEpisodeFormatter episodeImport=new OldEpisodeFormatter(show, language, source, target);
			ProgressDialog progressDialog=new ProgressDialog(parent, episodeImport);
			progressDialog.show();
		}
	}
}
