package com.kiwisoft.media.files;

import java.util.Set;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.kiwisoft.collection.*;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.format.FormatStringComparator;

/**
 * @author Stefan Stiller
 */
public class MediaFileManager implements CollectionChangeSource
{
	public static final String MEDIA_FILES="mediaFiles";

	private static MediaFileManager instance;

	public static MediaFileManager getInstance()
	{
		if (instance==null) instance=new MediaFileManager();
		return instance;
	}

	private MediaFileManager()
	{
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	public Set<MediaFile> getMediaFiles(MediaType mediaType)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class, null, "mediatype_id=?", mediaType.getId());
	}

	public Set<MediaFile> getImages()
	{
		return getMediaFiles(MediaType.IMAGE);
	}

	public Set<MediaFile> getMediaFileByFile(String root, String relativePath)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class, null, "mediatype_id=? and file=? and root=?", MediaType.IMAGE.getId(), relativePath, root);
	}

	private MediaFile createMediaFile(MediaType mediaType, String root)
	{
		MediaFile image=new MediaFile(mediaType, root);
		fireElementAdded(MEDIA_FILES, image);
		return image;
	}

	public MediaFile createImage(String root)
	{
		return createMediaFile(MediaType.IMAGE, root);
	}

	public MediaFile createVideo(String root)
	{
		return createMediaFile(MediaType.VIDEO, root);
	}

	public MediaFile createAudio(String root)
	{
		return createMediaFile(MediaType.AUDIO, root);
	}

	public void dropMediaFile(MediaFile mediaFile, boolean deletePhysically)
	{
		if (deletePhysically) mediaFile.deletePhysically();
		else mediaFile.delete();
		fireElementRemoved(MEDIA_FILES, mediaFile);
	}

	public MediaFile getMediaFile(Long id)
	{
		return DBLoader.getInstance().load(MediaFile.class, id);
	}

	public MediaFile getImage(Long id)
	{
		return DBLoader.getInstance().load(MediaFile.class, null, "id=? and mediatype_id=?", id, MediaType.IMAGE.getId());
	}

	public ImageFile getImageFile(Long id)
	{
		return DBLoader.getInstance().load(ImageFile.class, id);
	}

	@Override
	public Disposable addCollectionListener(CollectionChangeListener listener)
	{
		return collectionChangeSupport.addListener(listener);
	}

	@Override
	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	protected void fireElementAdded(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementAdded(propertyName, element);
	}

	protected void fireElementRemoved(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementRemoved(propertyName, element);
	}

	public int getNumberOfMediaFiles(Show show, MediaType mediaType)
	{
		return getNumberOfMediaFiles("mediafile_shows", "show_id", show.getId(), mediaType, null);
	}

	public int getNumberOfMediaFiles(Episode episode, MediaType mediaType)
	{
		return getNumberOfMediaFiles("mediafile_episodes", "episode_id", episode.getId(), mediaType, null);
	}

	public int getNumberOfMediaFiles(Person person, MediaType mediaType)
	{
		return getNumberOfMediaFiles("mediafile_persons", "person_id", person.getId(), mediaType, null);
	}

	public int getNumberOfMediaFiles(Person person, ContentType contentType)
	{
		return getNumberOfMediaFiles("mediafile_persons", "person_id", person.getId(), null, contentType);
	}

	private int getNumberOfMediaFiles(String associationTable, String ownerColumn, Long ownerId, MediaType mediaType, ContentType contentType)
	{
		try
		{
			Connection connection=DBSession.getInstance().getConnection();
			StringBuilder sql=new StringBuilder("select count(*)");
			sql.append(" from ").append(associationTable);
			sql.append(" map join mediafiles mf on mf.id=map.mediafile_id");
			sql.append(" where map.").append(ownerColumn).append("=?");
			if (mediaType!=null) sql.append(" and mf.mediatype_id=?");
			if (contentType!=null) sql.append(" and mf.contenttype_id=?");
			PreparedStatement statement=connection.prepareStatement(sql.toString());
			try
			{
				int index=1;
				statement.setLong(index++, ownerId);
				if (mediaType!=null) statement.setLong(index++, mediaType.getId());
				if (contentType!=null) statement.setLong(index, contentType.getId());
				ResultSet resultSet=statement.executeQuery();
				if (resultSet.next())
				{
					return resultSet.getInt(1);
				}
			}
			finally
			{
				statement.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	public Set<MediaFile> getMediaFiles(Show show, MediaType mediaType)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class,
									   "_ join mediafile_shows map on map.mediafile_id=mediafiles.id",
									   "mediafiles.mediatype_id=? and map.show_id=?",
									   mediaType.getId(), show.getId());
	}

	public Set<MediaFile> getMediaFiles(Episode episode, MediaType mediaType)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class,
									   "_ join mediafile_episodes map on map.mediafile_id=mediafiles.id",
									   "mediafiles.mediatype_id=? and map.episode_id=?",
									   mediaType.getId(), episode.getId());
	}

	public Set<MediaFile> getMediaFiles(Person person, MediaType mediaType)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class,
									   "_ join mediafile_persons map on map.mediafile_id=mediafiles.id",
									   "mediafiles.mediatype_id=? and map.person_id=?",
									   mediaType.getId(), person.getId());
	}

	public SetMap<String, MediaFile> groupMediaFiles(Set<MediaFile> files)
	{
		SetMap<String, MediaFile> mediaFiles=new SortedSetMap<String, MediaFile>(String.CASE_INSENSITIVE_ORDER, new FormatStringComparator());
		for (MediaFile mediaFile : files)
		{
			if (mediaFile.getContentType()!=null) mediaFiles.add(mediaFile.getContentType().getPluralName(), mediaFile);
			else mediaFiles.add("Unsorted", mediaFile);
		}
		return mediaFiles;
	}
}
