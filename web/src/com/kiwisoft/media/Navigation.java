package com.kiwisoft.media;

import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.medium.Medium;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.photos.Photo;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Season;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowInfo;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

public class Navigation
{
	private Navigation()
	{
	}

	public static String getLink(HttpServletRequest request, Show show)
	{
		return request.getContextPath()+"/ShowDetails.action?showId="+show.getId();
	}

	public static String getLink(HttpServletRequest request, Book book)
	{
		return request.getContextPath()+"/BookDetails.action?bookId="+book.getId();
	}

	public static String getLink(HttpServletRequest request, Season season)
	{
		return request.getContextPath()+"/ListEpisodes.action?showId="+season.getShowId()+"#season"+season.getNumber();
	}

	public static String getLink(HttpServletRequest request, Person person)
	{
		return request.getContextPath()+"/PersonDetails.action?personId="+person.getId();
	}

	public static String getLink(HttpServletRequest request, Medium medium)
	{
		return request.getContextPath()+"/MediumDetails.action?mediumId="+medium.getId();
	}

	public static String getLink(HttpServletRequest request, Movie movie)
	{
		return request.getContextPath()+"/MovieDetails.action?movieId="+movie.getId();
	}

	public static String getLink(HttpServletRequest request, LinkGroup linkGroup)
	{
		return request.getContextPath()+"/Links.action?groupId="+linkGroup.getId();
	}

	public static String getLink(HttpServletRequest request, Object value)
	{
		if (value==null) return "";
		if (value instanceof Photo)
		{
			Photo photo=(Photo) value;
			ImageFile picture=photo.getOriginalPicture();
			return request.getContextPath()+"/file/"+picture.getFileName()+"?type=ImageFile&id="+picture.getId()+"&rotate="+photo.getRotation();
		}
		else if (value instanceof MediaFile)
		{
			return request.getContextPath()+"/file/"+URLEncoder.encode(((MediaFile) value).getFileName())+"?type=Media&id="+((MediaFile) value).getId();
		}
		else if (value instanceof Episode)
		{
			return request.getContextPath()+"/EpisodeDetails.action?episodeId="+((Episode) value).getId();
		}
		else throw new RuntimeException("Unsupported value type: "+value.getClass());
	}

}
