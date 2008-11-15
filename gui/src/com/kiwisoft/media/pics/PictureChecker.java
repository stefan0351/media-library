package com.kiwisoft.media.pics;

import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;

import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.utils.FileUtils;

/**
 * @author Stefan Stiller
 */
public class PictureChecker implements Job
{
	public String getName()
	{
		return "Create Thumbnails";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		final ProgressSupport progress=new ProgressSupport(this, progressListener);
		progress.startStep("Initializing...");
		List<Object> pictureIds=DBLoader.getInstance().loadKeys(Picture.class, null, "root=?", MediaConfiguration.PATH_ROOT);
		List<Object> pictureFileIds=DBLoader.getInstance().loadKeys(PictureFile.class, null, "root=?", MediaConfiguration.PATH_ROOT);
		progress.startStep("Checking pictures...");
		progress.initialize(true, pictureIds.size(), null);
		for (Iterator it=pictureIds.iterator(); it.hasNext();)
		{
			if (progress.isStoppedByUser()) return false;
			Long pictureId=(Long)it.next();
			Picture picture=PictureManager.getInstance().getPicture(pictureId);
			File file=picture.getPhysicalFile();
			if (!file.exists()) progress.error("Picture '"+picture.getName()+"' with path '"+picture.getFile()+"' doesn't exist.");
			PictureFile thumbnail=picture.getThumbnailSidebar();
			if (thumbnail!=null)
			{
				pictureFileIds.remove(thumbnail.getId());
				file=thumbnail.getPhysicalFile();
				if (!file.exists()) progress.error("Sidebar thumbnail to picture '"+picture.getName()+"' with path '"+thumbnail.getFile()+"' doesn't exist.");
			}
			thumbnail=picture.getThumbnail50x50();
			if (thumbnail!=null)
			{
				pictureFileIds.remove(thumbnail.getId());
				file=thumbnail.getPhysicalFile();
				if (!file.exists()) progress.error("50x50 thumbnail to picture '"+picture.getName()+"' with path '"+thumbnail.getFile()+"' doesn't exist.");
			}
			progress.progress();
		}
		progress.startStep("Checking picture files...");
		progress.initialize(true, pictureFileIds.size(), null);
		for (Iterator it=pictureFileIds.iterator(); it.hasNext();)
		{
			if (progress.isStoppedByUser()) return false;
			Long pictureFileId=(Long)it.next();
			PictureFile pictureFile=PictureManager.getInstance().getPictureFile(pictureFileId);
			File file=pictureFile.getPhysicalFile();
			if (!file.exists()) progress.error("Picture file #"+pictureFile.getId()+" with path '"+pictureFile.getFile()+"' doesn't exist.");
			progress.progress();
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}
}
