package com.kiwisoft.media;

import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowInfo;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Season;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.medium.Medium;
import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.photos.Photo;

public class Navigation
{
	private Navigation()
	{
	}

	public static String getLink(HttpServletRequest request, Show show)
	{
		ShowInfo link=show.getDefaultInfo();
		if (link!=null) return request.getContextPath()+"/resource?file="+link.getPath()+"&show="+show.getId();
		return request.getContextPath()+"/shows/episodes.jsp?show="+show.getId();
	}

	public static String getLink(HttpServletRequest request, Book book)
	{
		return request.getContextPath()+"/books/book.jsp?book="+book.getId();
	}

	public static String getLink(HttpServletRequest request, Season season)
	{
		return request.getContextPath()+"/shows/episodes.jsp?show="+season.getShowId()+"#season"+season.getNumber();
	}

	public static String getLink(HttpServletRequest request, Episode episode)
	{
		return request.getContextPath()+"/shows/episode.jsp?episode="+episode.getId();
	}

	public static String getLink(HttpServletRequest request, Person person)
	{
		return request.getContextPath()+"/persons/person.jsp?id="+person.getId();
	}

	public static String getLink(HttpServletRequest request, Medium medium)
	{
		return request.getContextPath()+"/media/medium.jsp?id="+medium.getId();
	}

	public static String getLink(HttpServletRequest request, Movie movie)
	{
		return request.getContextPath()+"/movies/movie.jsp?movie="+movie.getId();
	}

	public static String getLink(HttpServletRequest request, Photo photo)
	{
		return request.getContextPath()+"/picture?type=PictureFile&id="+photo.getOriginalPictureId()+"&rotate="+photo.getRotation();
	}
}
