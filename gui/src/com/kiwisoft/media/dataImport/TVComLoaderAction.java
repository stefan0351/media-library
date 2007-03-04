package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JFrame;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.progress.ProgressDialog;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.media.dataImport.*;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;

public class TVComLoaderAction extends SimpleContextAction<Show>
{
	private JFrame parent;

	public TVComLoaderAction(JFrame frame)
	{
		super("Lade Episoden von TV.com");
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Configurator configurator=Configurator.getInstance();
		String url=configurator.getString("TVCom.url", "");
		Show show=getObject();
		EpisodeLoaderDialog dialog=new EpisodeLoaderDialog(parent, show, url);
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			url=dialog.getUrl();
			configurator.setString("TVCom.url", url);
			configurator.saveUserValues();
			TVComLoader process=new TVComLoader(show, url, dialog.getFirstSeason(), dialog.getLastSeason(), dialog.isAutoCreate())
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
