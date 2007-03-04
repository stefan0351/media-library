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

import com.kiwisoft.media.dataImport.GermanEpisodeImport;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.dataImport.EpisodeImportDialog;
import com.kiwisoft.media.dataImport.ImportEpisode;
import com.kiwisoft.media.show.EpisodeDetailsView;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

public class ImportEpisodesAction extends AbstractAction
{
	private JFrame parent;

	public ImportEpisodesAction(JFrame frame)
	{
		super("Importiere Episoden");
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Configurator configurator=Configurator.getInstance();
		String source=configurator.getString("path.import.episodes", "");
		EpisodeImportDialog dialog=new EpisodeImportDialog(parent, source);
		dialog.setVisible(true);
		if (dialog.getValue())
		{
			source=dialog.getSource();
			configurator.setString("path.import.episodes", source);
			configurator.saveUserValues();
			GermanEpisodeImport episodeImport=new GermanEpisodeImport(source)
			{
				protected Episode createEpisode(Show show, ImportEpisode info)
				{
					return EpisodeDetailsView.createDialog(null, show, info);
				}
			};
			ProgressDialog progressDialog=new ProgressDialog(parent, episodeImport);
			progressDialog.show();
		}
	}
}
