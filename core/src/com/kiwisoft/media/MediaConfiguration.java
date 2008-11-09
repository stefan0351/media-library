package com.kiwisoft.media;

import java.util.Date;

import com.kiwisoft.cfg.Configuration;

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
	public static final String PATH_PICTURES_RECENT="path.pictures.recent";
	public static final String PATH_ROOT="path.root";
	public static final String PATH_PHOTOS="path.photos";
	public static final String PATH_CDDBIDGEN_EXE="path.cddbidgen.exe";
	public static final String PATH_PHOTOS_THUMBNAILS="path.photos.thumbnails";
	public static final String PATH_BOOKS_COVERS="path.books.covers";
	public static final String PATH_FANFICS="path.fanfics";
	public static final String PATH_FANFICS_RECENT="path.fanfics.recent";
	public static final String PATH_EXPORTS_RECENT="path.exports.recent";
	public static final String PATH_WEB_RECENT="path.web.recent";
	public static final String PATH_IMAGE_EDITOR="path.image.editor";
	public static final String PATH_WEB_DATES="path.web.dates";
	public static final String PATH_LOGOS_CHANNELS_WEB="path.logos.channels.web";
	public static final String PATH_DOWNLOADS="path.downloads";

	public static final String URL_CDDB="url.cddb";

	private MediaConfiguration()
	{
	}

	public static String getBookCoverPath()
	{
		return Configuration.getInstance().getString(PATH_BOOKS_COVERS);
	}

	public static String getPhotoThumbnailPath()
	{
		return Configuration.getInstance().getString(PATH_PHOTOS_THUMBNAILS);
	}

	public static String getRootPath()
	{
		return Configuration.getInstance().getString(PATH_ROOT);
	}

	public static int nextThumbnailId()
	{
		Configuration configuration=Configuration.getInstance();
		long id=configuration.getLong(THUMBNAIL_ID, 1L);
		configuration.setLong(THUMBNAIL_ID, id+1);
		return (int)id;
	}

	public static String getRecentPicturePath()
	{
		return Configuration.getInstance().getString(PATH_PICTURES_RECENT, null);
	}

	public static void setRecentPicturePath(String path)
	{
		Configuration.getInstance().setString(PATH_PICTURES_RECENT, path);
	}

	public static String getRecentPhotoPath()
	{
		return Configuration.getInstance().getString(PATH_PHOTOS_RECENT, null);
	}

	public static void setRecentPhotoPath(String path)
	{
		Configuration.getInstance().setString(PATH_PHOTOS_RECENT, path);
	}

	public static String getFanFicPath()
	{
		return Configuration.getInstance().getString(PATH_FANFICS);
	}

	public static boolean isFanFicsEnabled()
	{
		return Configuration.getInstance().getBoolean(FANFICS_ENABLED, false);
	}

	public static String getImageEditorPath()
	{
		return Configuration.getInstance().getString(PATH_IMAGE_EDITOR);
	}

	public static String getRecentFanFicPath()
	{
		Configuration configuration=Configuration.getInstance();
		return configuration.getString(PATH_FANFICS_RECENT, getFanFicPath());
	}

	public static void setRecentFanFicPath(String path)
	{
		Configuration.getInstance().setString(PATH_FANFICS_RECENT, path);
	}

	public static String getRecentWebPath()
	{
		return Configuration.getInstance().getString(PATH_WEB_RECENT, getRootPath());
	}

	public static void getRecentWebPath(String path)
	{
		Configuration.getInstance().setString(PATH_WEB_RECENT, path);
	}

	public static int getRecentPro7Offset()
	{
		return Configuration.getInstance().getLong(DOWNLOAD_P7_OFFSET, 7L).intValue();
	}

	public static Date getRecentPro7Date()
	{
		return Configuration.getInstance().getDate(DOWNLOAD_P7_DATE);
	}

	public static void setRecentPro7Date(Date date)
	{
		Configuration.getInstance().setDate(DOWNLOAD_P7_DATE, date);
	}

	public static void setRecentPro7Offset(int days)
	{
		Configuration.getInstance().setLong(DOWNLOAD_P7_OFFSET, (long)days);
	}

	public static String getWebChannelLogoPath()
	{
		return Configuration.getInstance().getString(PATH_LOGOS_CHANNELS_WEB);
	}

	public static String getWebSchedulePath()
	{
		return Configuration.getInstance().getString(PATH_WEB_DATES);
	}

	public static String getRecentExportPath()
	{
		return Configuration.getInstance().getString(PATH_EXPORTS_RECENT);
	}

	public static void setRecentExportPath(String path)
	{
		Configuration.getInstance().setString(PATH_EXPORTS_RECENT, path);
	}

	public static String getDownloadPath()
	{
		return Configuration.getInstance().getString(PATH_DOWNLOADS);
	}

	public static void setDownloadPath(String path)
	{
		Configuration.getInstance().setString(PATH_DOWNLOADS, path);
	}

	public static String getPhotosPath()
	{
		return Configuration.getInstance().getString(PATH_PHOTOS);
	}

	public static void setPhotosPath(String path)
	{
		Configuration.getInstance().setString(PATH_PHOTOS, path);
	}

	public static String getCDDBIdGeneratorPath()
	{
		return Configuration.getInstance().getString(PATH_CDDBIDGEN_EXE);
	}

	public static String getCDDBUrl()
	{
		return Configuration.getInstance().getString(URL_CDDB);
	}
}
