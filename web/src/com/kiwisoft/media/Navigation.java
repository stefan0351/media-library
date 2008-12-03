package com.kiwisoft.media;

import java.net.URLEncoder;
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
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFile;

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
		String link="/movies/movie.jsp?movie="+movie.getId();
		if (request!=null) return request.getContextPath()+link;
		return link;
	}

	public static String getLink(HttpServletRequest request, Photo photo)
	{
		ImageFile picture=photo.getOriginalPicture();
		return request.getContextPath()+"/file/"+picture.getFileName()+"?type=ImageFile&id="+picture.getId()+"&rotate="+photo.getRotation();
	}

	public static String getLink(HttpServletRequest request, LinkGroup linkGroup)
	{
		return request.getContextPath()+"/links.jsp?group="+linkGroup.getId();
	}

	public static String getLink(HttpServletRequest request, MediaFile mediaFile)
	{
		return request.getContextPath()+"/file/"+URLEncoder.encode(mediaFile.getFileName())+"?type=Media&id="+mediaFile.getId();
	}
}
