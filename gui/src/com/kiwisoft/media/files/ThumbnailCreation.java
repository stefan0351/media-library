package com.kiwisoft.media.files;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Set;

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
	public String getName()
	{
		return "Create Thumbnails";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		final ProgressSupport progressSupport=new ProgressSupport(this, progressListener);
		progressSupport.startStep("Loading pictures...");
		String rootPath=MediaConfiguration.getRootPath();
		Set<MediaFile> pictures=DBLoader.getInstance().loadSet(MediaFile.class, null, "mediatype=? and thumbnail_sidebar_id is null and width>170", MediaFile.IMAGE);
		progressSupport.startStep("Creating thumbnails...");
		progressSupport.initialize(true, pictures.size(), null);
		for (final MediaFile picture : pictures)
		{
			if (progressSupport.isStoppedByUser()) return false;
			if (picture.getWidth()>170 && picture.getThumbnailSidebar()==null)
			{
				File file=picture.getPhysicalFile();
				if (file.exists())
				{
					File thumbnailFile=new File(file.getParentFile(), FileUtils.getNameWithoutExtension(file)+"_sb.jpg");
					if (!thumbnailFile.exists()) MediaFileUtils.resize(file, 170, -1, thumbnailFile);
					final Dimension thumbnailSize=MediaFileUtils.getImageSize(thumbnailFile);
					if (thumbnailSize!=null)
					{
						if (thumbnailSize.width==170)
						{
							final String thumbnailPath=FileUtils.getRelativePath(rootPath, thumbnailFile.getAbsolutePath());
							boolean ok=DBSession.execute(new Transactional()
							{
								public void run() throws Exception
								{
									picture.setThumbnail(MediaFile.THUMBNAIL_SIDEBAR, MediaConfiguration.PATH_ROOT, thumbnailPath, thumbnailSize.width, thumbnailSize.height);
								}

								public void handleError(Throwable throwable, boolean rollback)
								{
									progressSupport.error(throwable);
								}
							});
							if (!ok) return false;
							progressSupport.info("Created thumbnail '"+thumbnailFile.getAbsolutePath()+"'.");
						}
						else progressSupport.error("Thumbnail '"+thumbnailFile.getAbsolutePath()+"' was created with wrong size!");
					}
					else progressSupport.error("Error creating thumbnail '"+thumbnailFile.getAbsolutePath()+"'!");
				}
				else progressSupport.error("'"+file.getAbsolutePath()+"' doesn't exist!");
			}
			progressSupport.progress(1, true);
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}
}
