package com.kiwisoft.media.files;

import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;

import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.DBLoader;

/**
 * @author Stefan Stiller
 */
public class MediaFileChecker implements Job
{
	public String getName()
	{
		return "Check References";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		final ProgressSupport progress=new ProgressSupport(this, progressListener);
		progress.startStep("Initializing...");
		List<Object> mediaFileIds=DBLoader.getInstance().loadKeys(MediaFile.class, null, "root=? and mediatype=?", MediaConfiguration.PATH_ROOT, MediaFile.IMAGE);
		List<Object> imageFileIds=DBLoader.getInstance().loadKeys(ImageFile.class, null, "root=?", MediaConfiguration.PATH_ROOT);
		progress.startStep("Checking media files...");
		progress.initialize(true, mediaFileIds.size(), null);
		for (Iterator it=mediaFileIds.iterator(); it.hasNext();)
		{
			if (progress.isStoppedByUser()) return false;
			Long mediaFileId=(Long)it.next();
			MediaFile mediaFile=MediaFileManager.getInstance().getImage(mediaFileId);
			File file=mediaFile.getPhysicalFile();
			if (!file.exists()) progress.error("Picture '"+mediaFile.getName()+"' with path '"+mediaFile.getFile()+"' doesn't exist.");
			ImageFile thumbnail=mediaFile.getThumbnailSidebar();
			if (thumbnail!=null)
			{
				imageFileIds.remove(thumbnail.getId());
				file=thumbnail.getPhysicalFile();
				if (!file.exists()) progress.error("Sidebar thumbnail to media file '"+mediaFile.getName()+"' with path '"+thumbnail.getFile()+"' doesn't exist.");
			}
			thumbnail=mediaFile.getThumbnail50x50();
			if (thumbnail!=null)
			{
				imageFileIds.remove(thumbnail.getId());
				file=thumbnail.getPhysicalFile();
				if (!file.exists()) progress.error("50x50 thumbnail to media file '"+mediaFile.getName()+"' with path '"+thumbnail.getFile()+"' doesn't exist.");
			}
			progress.progress();
		}
		progress.startStep("Checking image files...");
		progress.initialize(true, imageFileIds.size(), null);
		for (Iterator it=imageFileIds.iterator(); it.hasNext();)
		{
			if (progress.isStoppedByUser()) return false;
			Long pictureFileId=(Long)it.next();
			ImageFile imageFile=MediaFileManager.getInstance().getImageFile(pictureFileId);
			File file=imageFile.getPhysicalFile();
			if (!file.exists()) progress.error("Image file #"+imageFile.getId()+" with path '"+imageFile.getFile()+"' doesn't exist.");
			progress.progress();
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}
}
