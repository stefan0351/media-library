package com.kiwisoft.media;

import java.util.Date;

import com.kiwisoft.utils.Configurator;

/**
 * @author Stefan Stiller
 */
public class MediaConfiguration
{
	private static final String THUMBNAIL_ID="thumbnail.id";

	private static final String FANFICS_ENABLED="fanfics.enabled";

	private static final String DOWNLOAD_P7_OFFSET="download.p7.offset";
	private static final String DOWNLOAD_P7_DATE="download.p7.date";

	public static final String PATH_PHOTOS_RECENT="path.photos.recent";
	public static final String PATH_SCHEDULE_RECENT="path.schedule.recent";
	public static final String PATH_PICTURES_RECENT="path.pictures.recent";
	public static final String PATH_ROOT="path.root";
	public static final String PATH_PHOTOS_THUMBNAILS="path.photos.thumbnails";
	public static final String PATH_BOOKS_COVERS="path.books.covers";
	public static final String PATH_FANFICS="path.fanfics";
	public static final String PATH_FANFICS_RECENT="path.fanfics.recent";
	public static final String PATH_WEB_RECENT="path.web.recent";
	public static final String PATH_LOGOS_CHANNELS="path.logos.channels";
	public static final String PATH_IMAGE_EDITOR="path.image.editor";
	public static final String PATH_WEB_DATES="path.web.dates";
	public static final String PATH_LOGOS_CHANNELS_WEB="path.logos.channels.web";

	private MediaConfiguration()
	{
	}

	public static String getBookCoverPath()
	{
		return Configurator.getInstance().getString(PATH_BOOKS_COVERS);
	}

	public static String getPhotoThumbnailPath()
	{
		return Configurator.getInstance().getString(PATH_PHOTOS_THUMBNAILS);
	}

	public static String getRootPath()
	{
		return Configurator.getInstance().getString(PATH_ROOT);
	}

	public static int nextThumbnailId()
	{
		Configurator configurator=Configurator.getInstance();
		int id=configurator.getInt(THUMBNAIL_ID, 1);
		configurator.setInt(THUMBNAIL_ID, id+1);
		return id;
	}

	public static String getRecentPicturePath()
	{
		return Configurator.getInstance().getString(PATH_PICTURES_RECENT, null);
	}

	public static void setRecentPicturePath(String path)
	{
		Configurator.getInstance().setString(PATH_PICTURES_RECENT, path);
	}

	public static String getRecentPhotoPath()
	{
		return Configurator.getInstance().getString(PATH_PHOTOS_RECENT, null);
	}

	public static void setRecentPhotoPath(String path)
	{
		Configurator.getInstance().setString(PATH_PHOTOS_RECENT, path);
	}

	public static String getRecentSchedulePath()
	{
		return Configurator.getInstance().getString(PATH_SCHEDULE_RECENT, null);
	}

	public static void setRecentSchedulePath(String path)
	{
		Configurator.getInstance().setString(PATH_SCHEDULE_RECENT, path);
	}

	public static String getFanFicPath()
	{
		return Configurator.getInstance().getString(PATH_FANFICS);
	}

	public static boolean isFanFicsEnabled()
	{
		return Configurator.getInstance().getBoolean(FANFICS_ENABLED, false);
	}

	public static String getImageEditorPath()
	{
		return Configurator.getInstance().getString(PATH_IMAGE_EDITOR);
	}

	public static String getRecentFanFicPath()
	{
		Configurator configurator=Configurator.getInstance();
		return configurator.getString(PATH_FANFICS_RECENT, getFanFicPath());
	}

	public static void setRecentFanFicPath(String path)
	{
		Configurator.getInstance().setString(PATH_FANFICS_RECENT, path);
	}

	public static String getRecentWebPath()
	{
		return Configurator.getInstance().getString(PATH_WEB_RECENT, getRootPath());
	}

	public static void getRecentWebPath(String path)
	{
		Configurator.getInstance().setString(PATH_WEB_RECENT, path);
	}

	public static void setChannelLogoPath(String path)
	{
		Configurator.getInstance().setString(PATH_LOGOS_CHANNELS, path);
	}

	public static String getChannelLogoPath()
	{
		return Configurator.getInstance().getString(PATH_LOGOS_CHANNELS, null);
	}

	public static int getRecentPro7Offset()
	{
		return Configurator.getInstance().getInt(DOWNLOAD_P7_OFFSET, 7);
	}

	public static Date getRecentPro7Date()
	{
		return Configurator.getInstance().getDate(DOWNLOAD_P7_DATE);
	}

	public static void setRecentPro7Date(Date date)
	{
		Configurator.getInstance().setDate(DOWNLOAD_P7_DATE, date);
	}

	public static void setRecentPro7Offset(int days)
	{
		Configurator.getInstance().setInt(DOWNLOAD_P7_OFFSET, days);
	}

	public static String getWebChannelLogoPath()
	{
		return Configurator.getInstance().getString(PATH_LOGOS_CHANNELS_WEB);
	}

	public static String getWebSchedulePath()
	{
		return Configurator.getInstance().getString(PATH_WEB_DATES);
	}
}
