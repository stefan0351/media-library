package com.kiwisoft.media.files;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;

import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Time;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Season;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.Channel;

/**
 * @author Stefan Stiller
 */
public class MediaFile extends ImageFile
{
	public static final String NAME="name";
	public static final String THUMBNAIL_50x50="thumbnail50x50";
	public static final String THUMBNAIL_SIDEBAR="thumbnailSidebar";
	public static final String THUMBNAIL="thumbnail";
	public static final String MEDIA_TYPE="mediaType";
	public static final String CONTENT_TYPE="contentType";
	public static final String DESCRIPTION="description";
	public static final String DURATION="duration";
	public static final String SHOWS="shows";
	public static final String PERSONS="persons";
	public static final String EPISODES="episodes";
	public static final String MOVIES="movies";

	private String name;
	private String description;
	private long duration;
	private Time durationTime;

	public MediaFile(MediaType mediaType, String root)
	{
		super(root);
		setMediaType(mediaType);
	}

	public MediaFile(DBDummy dummy)
	{
		super(dummy);
	}


	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, name);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		String oldDescription=this.description;
		this.description=description;
		setModified(DESCRIPTION, oldDescription, description);
	}

	public void setThumbnail(String property, String root, String path, int imageWidth, int imageHeight)
	{
		ImageFile thumbnail=(ImageFile)getReference(property);
		if (StringUtils.isEmpty(path))
		{
			if (thumbnail!=null)
			{
				setReference(property, null);
				thumbnail.delete();
			}
		}
		else
		{
			if (thumbnail==null)
			{
				thumbnail=new ImageFile(root);
				setReference(property, thumbnail);
			}
			thumbnail.setRoot(root);
			thumbnail.setFile(path);
			thumbnail.setWidth(imageWidth);
			thumbnail.setHeight(imageHeight);
		}
	}

	public long getDuration()
	{
		return duration;
	}

	public void setDuration(long duration)
	{
		durationTime=null;
		long oldDuration=this.duration;
		this.duration=duration;
		setModified(DURATION, oldDuration, duration);
	}

	public Time getDurationTime()
	{
		if (durationTime==null) durationTime=new Time(duration);
		return durationTime;
	}

	public MediaType getMediaType()
	{
		return (MediaType)getReference(MEDIA_TYPE);
	}

	public void setMediaType(MediaType mediaType)
	{
		setReference(MEDIA_TYPE, mediaType);
	}

	public ContentType getContentType()
	{
		return (ContentType)getReference(CONTENT_TYPE);
	}

	public void setContentType(ContentType contentType)
	{
		setReference(CONTENT_TYPE, contentType);
	}

	public ImageFile findThumbnail()
	{
		ImageFile thumbnail=getThumbnail();
		if (thumbnail==null && getMediaType()==MediaType.IMAGE)
		{
			if (MediaFileUtils.isThumbnailSize(getWidth(), getHeight(), MediaFileUtils.THUMBNAIL_WIDTH, MediaFileUtils.THUMBNAIL_HEIGHT))
			{
				thumbnail=this;
			}
		}
		return thumbnail;
	}

	public ImageFile getThumbnail()
	{
		return (ImageFile)getReference(THUMBNAIL);
	}

	public void setThumbnail(ImageFile thumbnail)
	{
		setReference(THUMBNAIL, thumbnail);
	}

	public void setThumbnail(String root, String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL, root, path, imageWidth, imageHeight);
	}

	public ImageFile getThumbnail50x50()
	{
		return (ImageFile)getReference(THUMBNAIL_50x50);
	}

	public void setThumbnail50x50(ImageFile thumbnail)
	{
		setReference(THUMBNAIL_50x50, thumbnail);
	}

	public void setThumbnail50x50(String root, String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL_50x50, root, path, imageWidth, imageHeight);
	}

	public ImageFile findSidebarThumbnail()
	{
		ImageFile thumbnail=getThumbnailSidebar();
		if (thumbnail==null && getMediaType()==MediaType.IMAGE)
		{
			if (MediaFileUtils.isThumbnailSize(getWidth(), getHeight(), MediaFileUtils.THUMBNAIL_SIDEBAR_WIDTH, MediaFileUtils.THUMBNAIL_SIDEBAR_HEIGHT))
			{
				thumbnail=this;
			}
		}
		return thumbnail;
	}

	public ImageFile getThumbnailSidebar()
	{
		return (ImageFile)getReference(THUMBNAIL_SIDEBAR);
	}

	public void setThumbnailSidebar(ImageFile thumbnail)
	{
		setReference(THUMBNAIL_SIDEBAR, thumbnail);
	}

	public void setThumbnailSidebar(String root, String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL_SIDEBAR, root, path, imageWidth, imageHeight);
	}

	public void setReferences(Collection<IDObject> references)
	{
		Set<Show> shows=new HashSet<Show>();
		Set<Episode> episodes=new HashSet<Episode>();
		Set<Movie> movies=new HashSet<Movie>();
		Set<Person> persons=new HashSet<Person>();
		for (IDObject reference : references)
		{
			if (reference instanceof Show) shows.add((Show)reference);
			else if (reference instanceof Episode) episodes.add((Episode)reference);
			else if (reference instanceof Movie) movies.add((Movie)reference);
			else if (reference instanceof Person) persons.add((Person)reference);
		}
		setShows(shows);
		setEpisodes(episodes);
		setMovies(movies);
		setPersons(persons);
	}

	public Set<Show> getShows()
	{
		return getAssociations(SHOWS);
	}

	public void setShows(Set<Show> shows)
	{
		setAssociations(SHOWS, shows);
	}

	public void addShow(Show show)
	{
		if (!containsAssociation(SHOWS, show)) createAssociation(SHOWS, show);
	}

	public Set<Episode> getEpisodes()
	{
		return getAssociations(EPISODES);
	}

	public void setEpisodes(Set<Episode> episodes)
	{
		setAssociations(EPISODES, episodes);
	}

	public void addEpisode(Episode episode)
	{
		if (!containsAssociation(EPISODES, episode)) createAssociation(EPISODES, episode);
	}

	public Set<Movie> getMovies()
	{
		return getAssociations(MOVIES);
	}

	public void setMovies(Set<Movie> movies)
	{
		setAssociations(MOVIES, movies);
	}

	public void addMovie(Movie movie)
	{
		if (!containsAssociation(MOVIES, movie)) createAssociation(MOVIES, movie);
	}

	public Set<Person> getPersons()
	{
		return getAssociations(PERSONS);
	}

	public void setPersons(Set<Person> persons)
	{
		setAssociations(PERSONS, persons);
	}

	public void addPerson(Person person)
	{
		if (!containsAssociation(PERSONS, person)) createAssociation(PERSONS, person);
	}

	public Set<IDObject> getReferences()
	{
		Set<IDObject> references=new HashSet<IDObject>();
		references.addAll(getShows());
		references.addAll(getEpisodes());
		references.addAll(getMovies());
		references.addAll(getPersons());
		return references;
	}

	@Override
	public boolean isUsed()
	{
		if (super.isUsed()) return true;
		DBLoader dbLoader=DBLoader.getInstance();
		if (dbLoader.count(Show.class, null, "logo_id=?", getId())>0) return true;
		if (dbLoader.count(Movie.class, null, "poster_id=?", getId())>0) return true;
		if (dbLoader.count(Person.class, null, "picture_id=?", getId())>0) return true;
		if (dbLoader.count(CastMember.class, null, "picture_id=?", getId())>0) return true;
		if (dbLoader.count(Book.class, null, "cover_id=?", getId())>0) return true;
		if (dbLoader.count(Channel.class, null, "logo_id=?", getId())>0) return true;
		if (dbLoader.count(Season.class, null, "logo_id=?", getId())>0) return true;
		return false;
	}

	@Override
	public void delete()
	{
		ImageFile thumbnail=getThumbnail();
		if (thumbnail!=null) thumbnail.delete();
		thumbnail=getThumbnail50x50();
		if (thumbnail!=null) thumbnail.delete();
		thumbnail=getThumbnailSidebar();
		if (thumbnail!=null) thumbnail.delete();
		super.delete();
	}

	@Override
	public void deletePhysically()
	{
		ImageFile thumbnail=getThumbnail();
		if (thumbnail!=null) thumbnail.deletePhysically();
		thumbnail=getThumbnail50x50();
		if (thumbnail!=null) thumbnail.deletePhysically();
		thumbnail=getThumbnailSidebar();
		if (thumbnail!=null) thumbnail.deletePhysically();
		super.deletePhysically();
	}
}

