package com.kiwisoft.media;

import java.util.Date;

import com.kiwisoft.cfg.Configuration;

/**
 * @author Stefan Stiller
 */
public class MediaConfiguration
{
	private static final String FANFICS_ENABLED="fanfics.enabled";

	public static final String PATH_PHOTOS_RECENT="path.photos.recent";
	public static final String PATH_MEDIA_RECENT="path.media.recent";
	public static final String PATH_ROOT="path.root";
	public static final String PATH_PHOTOS="path.photos";
	public static final String PATH_VIDEOS="path.videos";
	public static final String PATH_CDDBIDGEN_EXE="path.cddbidgen.exe";
	public static final String PATH_EXPORTS_RECENT="path.exports.recent";
	public static final String PATH_IMAGE_EDITOR="path.image.editor";
	public static final String PATH_VLC_MEDIA_PLAYER="path.player.vlc";

	public static final String URL_CDDB="url.cddb";

	private MediaConfiguration()
	{
	}

	public static String getRootPath()
	{
		return Configuration.getInstance().getString(PATH_ROOT);
	}

	public static String getRecentMediaPath()
	{
		return Configuration.getInstance().getString(PATH_MEDIA_RECENT, null);
	}

	public static void setRecentMediaPath(String path)
	{
		Configuration.getInstance().setString(PATH_MEDIA_RECENT, path);
	}

	public static String getRecentPhotoPath()
	{
		return Configuration.getInstance().getString(PATH_PHOTOS_RECENT, null);
	}

	public static void setRecentPhotoPath(String path)
	{
		Configuration.getInstance().setString(PATH_PHOTOS_RECENT, path);
	}

	public static boolean isFanFicsEnabled()
	{
		return Configuration.getInstance().getBoolean(FANFICS_ENABLED, false);
	}

	public static String getImageEditorPath()
	{
		return Configuration.getInstance().getString(PATH_IMAGE_EDITOR);
	}

	public static String getVLCMediaPlayerPath()
	{
		return Configuration.getInstance().getString(PATH_VLC_MEDIA_PLAYER, "vlc.exe");
	}

	public static String getPhotosPath()
	{
		return Configuration.getInstance().getString(PATH_PHOTOS);
	}

	public static void setPhotosPath(String path)
	{
		Configuration.getInstance().setString(PATH_PHOTOS, path);
	}

	public static String getVideosPath()
	{
		return Configuration.getInstance().getString(PATH_VIDEOS);
	}

	public static void setVideosPath(String path)
	{
		Configuration.getInstance().setString(PATH_VIDEOS, path);
	}

	public static String getCDDBIdGeneratorPath()
	{
		return Configuration.getInstance().getString(PATH_CDDBIDGEN_EXE);
	}

	public static String getCDDBUrl()
	{
		return Configuration.getInstance().getString(URL_CDDB);
	}

	public static boolean isChannelReceivable(Channel channel)
	{
		return Configuration.getInstance().getBoolean("channel"+channel.getId()+".receivable", Boolean.FALSE);
	}

	public static void setChannelReceivable(Channel channel, boolean receivable)
	{
		Configuration.getInstance().setBoolean("channel"+channel.getId()+".receivable", receivable);
	}

	public static void removeChannel(Channel channel)
	{
		Configuration.getInstance().setBoolean("channel"+channel.getId()+".receivable", null);
	}
}
