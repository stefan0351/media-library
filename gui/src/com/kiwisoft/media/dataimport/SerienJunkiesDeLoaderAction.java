package com.kiwisoft.media.dataimport;

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

public class SerienJunkiesDeLoaderAction extends SimpleContextAction
{
	private JFrame parent;

	public SerienJunkiesDeLoaderAction(JFrame frame)
	{
		super(Show.class, "Load Episodes from SerienJunkies.de");
		parent=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final Show show=(Show)getObject();
		Link link=DBLoader.getInstance().load(Link.class,
									"_ join linkgroups lg on lg.id=links.linkgroup_id join shows s on s.linkgroup_id=lg.id",
									"s.id=? and links.url like ?",
									show.getId(), "http://www.serienjunkies.de/%/%");
		final Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
		EpisodeLoaderDialog dialog=new EpisodeLoaderDialog(parent, show, link)
		{
			@Override
			protected String getLinkName()
			{
				return "SerienJunkies.de - "+show.getTitle(german);
			}

			@Override
			protected Language getLinkLanguage()
			{
				return german;
			}
		};
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			link=dialog.getLink();
			EpisodeDataLoader process=new SerienJunkiesDeLoader(show, link.getUrl(), dialog.getFirstSeason(), dialog.getLastSeason(), dialog.isAutoCreate())
			{
				@Override
				protected Episode createEpisode(Show show, EpisodeData info)
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
