package com.kiwisoft.media.files;

import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * @author Stefan Stiller
 */
public class MediaFileChecker implements Job
{
	@Override
	public String getName()
	{
		return "Check References";
	}

	@Override
	public boolean run(ProgressListener progressListener) throws Exception
	{
		final ProgressSupport progress=new ProgressSupport(this, progressListener);
		progress.startStep("Initializing...");
		List<Object> mediaFileIds=DBLoader.getInstance().loadKeys(MediaFile.class, null, "root=? and mediatype_id=?",
																  MediaConfiguration.PATH_ROOT, MediaType.IMAGE.getId());
		List<Object> imageFileIds=DBLoader.getInstance().loadKeys(ImageFile.class, null, "root=?", MediaConfiguration.PATH_ROOT);

		progress.startStep("Checking media files...");
		progress.initialize(true, mediaFileIds.size(), null);
		Set<String> usedFiles=new HashSet<String>();
		for (Iterator it=mediaFileIds.iterator(); it.hasNext();)
		{
			if (progress.isStoppedByUser()) return false;
			Long mediaFileId=(Long) it.next();
			MediaFile mediaFile=MediaFileManager.getInstance().getImage(mediaFileId);
			File file=mediaFile.getPhysicalFile();
			if (!file.exists()) progress.error("Picture '"+mediaFile.getName()+"' with path '"+mediaFile.getFile()+"' doesn't exist.");
			else usedFiles.add(file.getCanonicalPath());
			ImageFile thumbnail=mediaFile.getThumbnailSidebar();
			if (thumbnail!=null)
			{
				imageFileIds.remove(thumbnail.getId());
				file=thumbnail.getPhysicalFile();
				if (!file.exists())
					progress.error("Sidebar thumbnail to media file '"+mediaFile.getName()+"' with path '"+thumbnail.getFile()+"' doesn't exist.");
				else usedFiles.add(file.getCanonicalPath());
			}
			thumbnail=mediaFile.getThumbnail50x50();
			if (thumbnail!=null)
			{
				imageFileIds.remove(thumbnail.getId());
				file=thumbnail.getPhysicalFile();
				if (!file.exists())
					progress.error("50x50 thumbnail to media file '"+mediaFile.getName()+"' with path '"+thumbnail.getFile()+"' doesn't exist.");
				else usedFiles.add(file.getCanonicalPath());
			}
			progress.progress();
		}

		progress.startStep("Checking image files...");
		progress.initialize(true, imageFileIds.size(), null);
		for (Iterator it=imageFileIds.iterator(); it.hasNext();)
		{
			if (progress.isStoppedByUser()) return false;
			Long pictureFileId=(Long) it.next();
			ImageFile imageFile=MediaFileManager.getInstance().getImageFile(pictureFileId);
			File file=imageFile.getPhysicalFile();
			if (!file.exists()) progress.error("Image file #"+imageFile.getId()+" with path '"+imageFile.getFile()+"' doesn't exist.");
			else usedFiles.add(file.getCanonicalPath());
			progress.progress();
		}

		progress.startStep("Search for unused files...");
		progress.initialize(false, 100, null);
		List<File> files=new ArrayList<File>();
		Collections.addAll(files, new File(MediaConfiguration.getRootPath(), "books"+File.separator+"covers").listFiles(new ImageFileFilter()));
		Collections.addAll(files, new File(MediaConfiguration.getRootPath(), "movies"+File.separator+"posters").listFiles(new ImageFileFilter()));
		Collections.addAll(files, new File(MediaConfiguration.getRootPath(), "movies"+File.separator+"posters2").listFiles(new ImageFileFilter()));
		Collections.addAll(files, new File(MediaConfiguration.getRootPath(), "persons"+File.separator+"images").listFiles(new ImageFileFilter()));
		Collections.addAll(files, new File(MediaConfiguration.getRootPath(), "photos"+File.separator+"thumbnails").listFiles(new ImageFileFilter()));   // todo analyze sub folders
		Collections.addAll(files, new File(MediaConfiguration.getRootPath(), "shows"+File.separator+"posters").listFiles(new ImageFileFilter()));
		Collections.addAll(files, new File(MediaConfiguration.getRootPath(), "wallpapers").listFiles(new ImageFileFilter()));
		progress.initialize(true, files.size(), null);
		for (File file : files)
		{
			if (progress.isStoppedByUser()) return false;
			if (!usedFiles.contains(file.getCanonicalPath()))
			{
				progress.warning("File '"+file.getAbsolutePath()+"' seems to be not used anywhere.");
			}
			progress.progress();
		}

		return true;
	}

	@Override
	public void dispose() throws IOException
	{
	}

	private static class ImageFileFilter implements FileFilter
	{
		@Override
			public boolean accept(File file)
		{
			if (file.isFile())
			{
				String name=file.getName().toLowerCase();
				return name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".png");
			}
			return false;
		}
	}
}
