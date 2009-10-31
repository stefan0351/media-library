package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.utils.ODFTemplate;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.collection.Chain;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.CastCreditComparator;

/**
 * @author Stefan Stiller
 */
public class CreateCoverAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public CreateCoverAction(ApplicationFrame frame)
	{
		super(Medium.class, "Create Cover");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			Medium medium=(Medium)getObject();
			Chain<Track> tracks=medium.getTracks();
			if (tracks.size()==1)
			{
				Track track=tracks.getFirst();
				if (track.getMovie()!=null)
				{
					Movie movie=track.getMovie();
					ODFTemplate template=new ODFTemplate(ClassLoader.getSystemResource("com/kiwisoft/media/covers/dvd_movie.odtt"));
					template.setVariable("key", medium.getFullKey());
					template.setVariable("title", medium.getName());
					template.setPicture("poster", movie.getPoster().getPhysicalFile());
					String summary=movie.getSummaryText(track.getLanguage());
					if (StringUtils.isEmpty(summary)) summary=movie.getSummaryText(LanguageManager.getInstance().getLanguageBySymbol("de"));
					if (StringUtils.isEmpty(summary)) summary=movie.getSummaryText(LanguageManager.getInstance().getLanguageBySymbol("en"));
					template.setVariable("description", summary);
					List<CastMember> castMembers=new ArrayList<CastMember>(movie.getCastMembers(CreditType.MAIN_CAST));
					Collections.sort(castMembers, new CastCreditComparator());
					StringBuilder cast=new StringBuilder();
					for (CastMember castMember : castMembers)
					{
						Person actor=castMember.getActor();
						if (actor!=null)
						{
							if (cast.length()>0) cast.append(", ");
							cast.append(actor.getName());
							if (!StringUtils.isEmpty(castMember.getCharacterName()))
							{
								cast.append(" (");
								cast.append(castMember.getCharacterName());
								cast.append(")");
							}
						}
					}
					template.setVariable("cast", cast);
					File file=File.createTempFile("cover", ".odt");
					file.deleteOnExit();
					template.createDocument(file);
					Utils.run("cmd /c start \"Cover\" \""+file.getAbsolutePath()+"\"");
				}
			}
		}
		catch (Exception e1)
		{
			GuiUtils.handleThrowable(frame, e1);
		}
	}
}
