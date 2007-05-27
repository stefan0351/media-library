package com.kiwisoft.media.photos;

import java.io.File;
import java.io.IOException;

import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.ImageDescriptor;
import com.kiwisoft.utils.gui.ImageUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.media.pics.ImageData;

/**
 * @author Stefan Stiller
*/
public class PhotoImportJob implements Job
{
	private PhotoGallery photoGallery;
	private File[] files;
	private ProgressSupport progressSupport;

	public PhotoImportJob(PhotoGallery photocGallery, File[] files)
	{
		this.photoGallery=photocGallery;
		this.files=files;
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progressSupport=new ProgressSupport(this, progressListener);
		progressSupport.startStep("Creating thumbnails...");
		progressSupport.initialize(true, files.length, null);
		for (final File file : files)
		{
			if (file.exists())
			{
				if (file.isFile())
				{
					final ImageDescriptor descriptor=ImageUtils.getImageFormat(file);
					String type=descriptor.getType();
					if ("PNG".equals(type) || "JPEG".equals(type) || "GIF".equals(type))
					{
						ImageData thumbnail=PhotoManager.getInstance().createThumbnail(file, 0);
						if (thumbnail==null)
						{
							progressSupport.error("Faild to create thumbnail for file '"+file+"'.");
							return false;
						}
						if (!DBSession.execute(new CreatePhotoTx(descriptor, thumbnail))) return false;
					}
					else progressSupport.warning("'"+file.getPath()+"' is not a supported image.");
				}
				else progressSupport.warning("'"+file.getPath()+"' is a directory.");
			}
			else progressSupport.error("File '"+file.getPath()+"' doesn't exist.");
			progressSupport.progress(1, true);
		}
		return true;
	}

	public String getName()
	{
		return "Import Photos";
	}

	public void dispose() throws IOException
	{
		photoGallery=null;
		files=null;
		progressSupport=null;
	}

	private class CreatePhotoTx implements Transactional
	{
		private ImageDescriptor picture;
		private ImageData thumbnail;

		public CreatePhotoTx(ImageDescriptor picture, ImageData thumbnail)
		{
			this.picture=picture;
			this.thumbnail=thumbnail;
		}

		public void run() throws Exception
		{
			photoGallery.createPhoto(picture, thumbnail);
		}

		public void handleError(Throwable throwable)
		{
			progressSupport.error(throwable);
		}
	}
}
