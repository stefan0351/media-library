package com.kiwisoft.media;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowInfo;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.video.Video;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.03.2007
 * Time: 19:48:32
 * To change this template use File | Settings | File Templates.
 */
public class Navigation
{
	private Navigation()
	{
	}

	public static String getLink(Show show)
	{
		ShowInfo link=show.getDefaultInfo();
		if (link!=null) return "/"+link.getPath()+"?show="+show.getId();
		return "/shows/episodes.jsp?show="+show.getId();
	}

	public static String getLink(Episode episode)
	{
		return "/shows/episode.jsp?episode="+episode.getId();
	}

	public static String getLink(Person person)
	{
		return "/persons/person.jsp?id="+person.getId();
	}

	public static String getLink(Video video)
	{
		return "/videos/video.jsp?id="+video.getId();
	}

	public static String getLink(Movie movie)
	{
		return "/movies/movie.jsp?movie="+movie.getId();
	}
}
