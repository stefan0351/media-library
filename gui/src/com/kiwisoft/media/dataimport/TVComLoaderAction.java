package com.kiwisoft.media.dataimport;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.persistence.DBLoader;

public class TVComLoaderAction extends SimpleContextAction
{
	private JFrame parent;

	public TVComLoaderAction(JFrame frame)
	{
		super(Show.class, "Load Episodes from TV.com");
		parent=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final Show show=(Show)getObject();

		Link link=DBLoader.getInstance().load(Link.class,
									"_ join linkgroups lg on lg.id=links.linkgroup_id join shows s on s.linkgroup_id=lg.id",
									"s.id=? and links.url like ?",
									show.getId(), "http://www.tv.com/%/show/%/%");
		EpisodeLoaderLinkDialog dialog=new EpisodeLoaderLinkDialog(parent, show, "Load Episodes from TV.com");
		dialog.setLink(link);
		dialog.setLinkName("TV.com - "+show.getTitle()+" - Episode List");
		dialog.setLinkLanguage(LanguageManager.ENGLISH);
		dialog.setSearchSite("tv.com");
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			link=dialog.getLink();
			new EpisodeLoaderDialog(parent, show, new TVComLoader(link.getUrl())).setVisible(true);
		}
	}
}
