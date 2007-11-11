package com.kiwisoft.media;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowInfo;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Season;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.medium.Medium;
import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.media.photos.Photo;

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

	public static String getLink(Book book)
	{
		return "/books/book.jsp?book="+book.getId();
	}

	public static String getLink(Season season)
	{
		return "/shows/episodes.jsp?show="+season.getShowId()+"#season"+season.getNumber();
	}

	public static String getLink(Episode episode)
	{
		return "/shows/episode.jsp?episode="+episode.getId();
	}

	public static String getLink(Person person)
	{
		return "/persons/person.jsp?id="+person.getId();
	}

	public static String getLink(Medium medium)
	{
		return "/media/medium.jsp?id="+medium.getId();
	}

	public static String getLink(Movie movie)
	{
		return "/movies/movie.jsp?movie="+movie.getId();
	}

	public static String getLink(Photo photo)
	{
		return "/picture?type=PictureFile&id="+photo.getOriginalPictureId()+"&rotate="+photo.getRotation();
	}
}
