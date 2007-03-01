/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.movie.Movie;

public class Name extends IDObject
{
	public static final int SHOW=1;
	public static final int EPISODE=2;
	public static final int CHANNEL=3;
	public static final int MOVIE=4;

	public static final String LANGUAGE="language";

	private String name;
	private Long refId;
	private int type;

	public Name(Show show)
	{
		setReference(show);
		setType(SHOW);
	}

	public Name(Movie movie)
	{
		setReference(movie);
		setType(MOVIE);
	}

	public Name(Episode episode)
	{
		setReference(episode);
		setType(EPISODE);
	}

	public Name(Channel channel)
	{
		setReference(channel);
		setType(CHANNEL);
	}

	public Name(DBDummy dummy)
	{
		super(dummy);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
		setModified();
	}

	public IDObject getReference()
	{
		switch (type)
		{
			case SHOW:
				return DBLoader.getInstance().load(Show.class, refId);
			case EPISODE:
				return DBLoader.getInstance().load(Episode.class, refId);
			case CHANNEL:
				return DBLoader.getInstance().load(Channel.class, refId);
		}
		return null;
	}

	public void setReference(IDObject reference)
	{
		refId=reference.getId();
		setModified();
	}

	private void setType(int type)
	{
		this.type=type;
		setModified();
	}

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}
}
