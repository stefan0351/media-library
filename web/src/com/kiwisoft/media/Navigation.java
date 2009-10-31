package com.kiwisoft.media;

import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.medium.Medium;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.photos.Photo;
import com.kiwisoft.media.photos.PhotoGallery;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Season;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfic.Author;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class Navigation
{
	private Navigation()
	{
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
			try
			{
				return request.getContextPath()+"/file/"+URLEncoder.encode(((MediaFile) value).getFileName(), "UTF-8")+"?type=Media&id="+((MediaFile) value).getId();
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				return request.getContextPath()+"/file/?type=Media&id="+((MediaFile) value).getId();
			}
		}
		else if (value instanceof Episode)
		{
			return request.getContextPath()+"/EpisodeDetails.action?episodeId="+((Episode) value).getId();
		}
		else if (value instanceof PhotoGallery)
		{
			return request.getContextPath()+"/PhotoGalleryDetails.action?galleryId="+((PhotoGallery) value).getId();
		}
		else if (value instanceof Show)
		{
			return request.getContextPath()+"/ShowDetails.action?showId="+((Show)value).getId();
		}
		else if (value instanceof Book)
		{
			return request.getContextPath()+"/BookDetails.action?bookId="+((Book) value).getId();
		}
		else if (value instanceof Season)
		{
			Season season=(Season) value;
			return request.getContextPath()+"/ListEpisodes.action?showId="+season.getShowId()+"#season"+season.getNumber();
		}
		else if (value instanceof Person)
		{
			return request.getContextPath()+"/PersonDetails.action?personId="+((Person) value).getId();
		}
		else if (value instanceof Medium)
		{
			return request.getContextPath()+"/MediumDetails.action?mediumId="+((Medium) value).getId();
		}
		else if (value instanceof Movie)
		{
			return request.getContextPath()+"/MovieDetails.action?movieId="+((Movie) value).getId();
		}
		else if (value instanceof LinkGroup)
		{
			return request.getContextPath()+"/Links.action?groupId="+((LinkGroup) value).getId();
		}
		else if (value instanceof FanFic)
		{
			return request.getContextPath()+"/FanFic.action?fanFicId="+((FanFic) value).getId();
		}
		else if (value instanceof Author)
		{
			return request.getContextPath()+"/ListFanFics.action?authorId="+((Author) value).getId();
		}
		throw new RuntimeException("Unsupported value type: "+value.getClass());
	}

}
