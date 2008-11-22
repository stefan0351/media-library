package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.progress.ProgressDialog;
import com.kiwisoft.persistence.DBLoader;

public class TVComLoaderAction extends SimpleContextAction
{
	private JFrame parent;

	public TVComLoaderAction(JFrame frame)
	{
		super(Show.class, "Load Episodes from TV.com");
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		final Show show=(Show)getObject();
		Link link=DBLoader.getInstance().load(Link.class,
									"_ join linkgroups lg on lg.id=links.linkgroup_id join shows s on s.linkgroup_id=lg.id",
									"s.id=? and links.url like ?",
									show.getId(), "http://www.tv.com%episode_listings.html");
		EpisodeLoaderDialog dialog=new EpisodeLoaderDialog(parent, show, link)
		{
			protected String getLinkName()
			{
				return "TV.com - "+show.getTitle()+" - Episode List";
			}

			protected Language getLinkLanguage()
			{
				return LanguageManager.getInstance().getLanguageBySymbol("en");
			}
		};
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			link=dialog.getLink();
			EpisodeDataLoader process=new TVComLoader(show, link.getUrl(), dialog.getFirstSeason(), dialog.getLastSeason(), dialog.isAutoCreate())
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
			progressDialog.start();
		}
	}
}
