package com.kiwisoft.media.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.progress.ProgressDialog;
import com.kiwisoft.media.dataImport.*;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;

public class TVComDataLoaderAction extends AbstractAction
{
	private Show show;
	private JFrame parent;

	public TVComDataLoaderAction(JFrame frame, Show show)
	{
		super("Lade Daten von TV.com");
		this.show=show;
		parent=frame;
		setEnabled(show!=null);
	}

	public void actionPerformed(ActionEvent e)
	{
		Configurator configurator=Configurator.getInstance();
		String url=configurator.getString("TVCom.url", "");
		TVComDataLoaderDialog dialog=new TVComDataLoaderDialog(parent, show, url);
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			url=dialog.getUrl();
			configurator.setString("TVCom.url", url);
			configurator.saveUserValues();
			TVComDataLoader process=new TVComDataLoader(show, url, dialog.getFirstSeason(), dialog.getLastSeason(), dialog.isAutoCreate())
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
