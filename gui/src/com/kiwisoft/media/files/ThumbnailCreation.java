package com.kiwisoft.media.files;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
 */
public class ThumbnailCreation implements Job
{
	private ProgressSupport progress;

	public String getName()
	{
		return "Create Thumbnails";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progress=new ProgressSupport(this, progressListener);
		String rootPath=MediaConfiguration.getRootPath();

		progress.startStep("Loading sidebar images...");
		Set<MediaFile> images=new HashSet<MediaFile>();
		images.addAll(getSidebarImages("shows", "logo_id"));
		images.addAll(getSidebarImages("seasons", "logo_id"));
		images.addAll(getSidebarImages("books", "cover_id"));
		images.addAll(getSidebarImages("movies", "poster_id"));
		images.addAll(getSidebarImages("persons", "picture_id"));
		images.addAll(getSidebarImages("cast", "picture_id"));
		images.addAll(getSidebarImages("channels", "logo_id"));
		progress.info("Found "+images.size()+" sidebar images with missing thumbnails.");

		progress.startStep("Creating thumbnails...");
		progress.initialize(true, images.size(), null);
		if (!createThumbnails(images, rootPath, MediaFile.THUMBNAIL_SIDEBAR, 170, -1, "sb")) return false;

		progress.startStep("Loading gallery images...");
		images=new HashSet<MediaFile>();
		images.addAll(getGalleryImages("mediafile_shows"));         
		images.addAll(getGalleryImages("mediafile_persons"));
		progress.info("Found "+images.size()+" gallery images with missing thumbnails.");

		progress.startStep("Creating thumbnails...");
		progress.initialize(true, images.size(), null);
		if (!createThumbnails(images, rootPath, MediaFile.THUMBNAIL, 160, 120, "thb")) return false;

		return true;
	}

	private Set<MediaFile> getGalleryImages(String mappingTable)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class, "_ join "+mappingTable+" map on map.mediafile_id=mediafiles.id",
											  "mediafiles.thumbnail_id is null and mediafiles.mediatype_id=?" +
											  " and (mediafiles.width>160 or mediafiles.height>120)", MediaType.IMAGE.getId());
	}

	private Set<MediaFile> getSidebarImages(String table, String column)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class, table,
													 table+"."+column+"=mediafiles.id and mediafiles.mediatype_id=?"+
													 " and mediafiles.thumbnail_sidebar_id is null"+
													 " and mediafiles.width>170", MediaType.IMAGE.getId());
	}

	private boolean createThumbnails(Set<MediaFile> images, String rootPath, final String property, int width, int height, String suffix)
	{
		for (final MediaFile image : images)
		{
			if (progress.isStoppedByUser()) return false;
			if (image.getReference(property)==null && !MediaFileUtils.isThumbnailSize(image.getWidth(), image.getHeight(), width, height))
			{
				File sourceFile=image.getPhysicalFile();
				if (sourceFile.exists())
				{
					File thumbnailFile=new File(sourceFile.getParentFile(), FileUtils.getNameWithoutExtension(sourceFile)+"_"+suffix+".jpg");
					if (!thumbnailFile.exists()) MediaFileUtils.resize(sourceFile, width, height, thumbnailFile);
					final Dimension thumbnailSize=MediaFileUtils.getImageSize(thumbnailFile);
					if (thumbnailSize!=null)
					{
						if (MediaFileUtils.isThumbnailSize(thumbnailSize.width, thumbnailSize.height, width, height))
						{
							final String thumbnailPath=FileUtils.getRelativePath(rootPath, thumbnailFile.getAbsolutePath());
							boolean ok=DBSession.execute(new MyTransactional(image, property, thumbnailPath, thumbnailSize));
							if (!ok) return false;
							progress.info("Created thumbnail '"+thumbnailFile.getAbsolutePath()+"'.");
						}
						else progress.error("Thumbnail '"+thumbnailFile.getAbsolutePath()+"' was created with wrong size!");
					}
					else progress.error("Error creating thumbnail '"+thumbnailFile.getAbsolutePath()+"'!");
				}
				else progress.error("'"+sourceFile.getAbsolutePath()+"' doesn't exist!");
			}
			progress.progress(1, true);
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}

	private class MyTransactional implements Transactional
	{
		private final MediaFile image;
		private final String property;
		private final String thumbnailPath;
		private final Dimension thumbnailSize;

		public MyTransactional(MediaFile image, String property, String thumbnailPath, Dimension thumbnailSize)
		{
			this.image=image;
			this.property=property;
			this.thumbnailPath=thumbnailPath;
			this.thumbnailSize=thumbnailSize;
		}

		public void run() throws Exception
		{
			image.setThumbnail(property, MediaConfiguration.PATH_ROOT, thumbnailPath, thumbnailSize.width, thumbnailSize.height);
		}

		public void handleError(Throwable throwable, boolean rollback)
		{
			progress.error(throwable);
		}
	}
}
