package com.kiwisoft.media.dataimport;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.swing.actions.SimpleContextAction;
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
		EpisodeLoaderLinkDialog dialog=new EpisodeLoaderLinkDialog(parent, show, "Load Episodes from SerienJunkies.de");
		dialog.setLink(link);
		dialog.setLinkName("SerienJunkies.de - "+show.getTitle(LanguageManager.GERMAN));
		dialog.setLinkLanguage(LanguageManager.GERMAN);
		dialog.setSearchSite("serienjunkies.de");
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			link=dialog.getLink();
			new EpisodeLoaderDialog(parent, show, new SerienJunkiesDeLoader(link.getUrl())).setVisible(true);
		}
	}
}
