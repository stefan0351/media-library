package com.kiwisoft.media.dataImport;

import com.kiwisoft.media.dataImport.EpisodeLoaderDialog;
import com.kiwisoft.media.dataImport.ImportEpisode;
import com.kiwisoft.media.dataImport.NoEpisodeDialog;
import com.kiwisoft.media.dataImport.SerienJunkiesDeLoader;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SerienJunkiesDeLoaderAction extends SimpleContextAction<Show>
{
	private JFrame parent;

	public SerienJunkiesDeLoaderAction(JFrame frame)
	{
		super("Lade Episoden von SerienJunkies.de");
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Configurator configurator=Configurator.getInstance();
		String url=configurator.getString("SerienJunkiesDe.url", "");
		Show show=getObject();
		EpisodeLoaderDialog dialog=new EpisodeLoaderDialog(parent, show, url);
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			url=dialog.getUrl();
			configurator.setString("SerienJunkiesDe.url", url);
			configurator.saveUserValues();
			SerienJunkiesDeLoader process=new SerienJunkiesDeLoader(show, url, dialog.getFirstSeason(), dialog.getLastSeason(), dialog.isAutoCreate())
			{
				protected Episode createEpisode(Show show, ImportEpisode info)
				{
					NoEpisodeDialog dialog=new NoEpisodeDialog(parent, show, info);
					dialog.setVisible(true);
					if (dialog.isOk()) return dialog.getEpisode();
					return null;
				}
			};
			ProgressDialog progressDialog=new ProgressDialog(parent, process);
			progressDialog.show();
		}
	}
}
