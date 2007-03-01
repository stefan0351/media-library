package com.kiwisoft.media.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.progress.ProgressDialog;
import com.kiwisoft.media.dataImport.*;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.EpisodeDetailsView;

public class TVComDataLoaderAction extends AbstractAction
{
	private Show show;
	private JFrame parent;

	public TVComDataLoaderAction(Show show, JFrame frame)
	{
		super("Lade Daten von TV.com");
		this.show=show;
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Configurator configurator=Configurator.getInstance();
		String url=configurator.getString("TVCom.url", "");
		TVComDataLoaderDialog dialog=new TVComDataLoaderDialog(parent, show, url);
		dialog.setVisible(true);
		if (dialog.getValue())
		{
			url=dialog.getUrl();
			configurator.setString("TVCom.url", url);
			configurator.saveUserValues();
			TVComDataLoader process=new TVComDataLoader(show, url, 1, 1)
			{
				protected Episode createEpisode(Show show, XMLEpisodeInfo info)
				{
					return EpisodeDetailsView.createDialog(null, show, info);
				}
			};
			ProgressDialog progressDialog=new ProgressDialog(parent, process);
			progressDialog.show();
		}
	}
}
