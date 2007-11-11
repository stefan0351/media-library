package com.kiwisoft.media.photos;

import java.io.File;
import java.io.IOException;

import com.kiwisoft.swing.progress.Job;
import com.kiwisoft.swing.progress.ProgressSupport;
import com.kiwisoft.swing.progress.ProgressListener;
import com.kiwisoft.media.pics.ImageData;
import com.kiwisoft.media.pics.PictureDetails;
import com.kiwisoft.media.pics.PictureUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

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
					final PictureDetails descriptor=PictureUtils.getImageFormat(file);
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
		private PictureDetails picture;
		private ImageData thumbnail;

		public CreatePhotoTx(PictureDetails picture, ImageData thumbnail)
		{
			this.picture=picture;
			this.thumbnail=thumbnail;
		}

		public void run() throws Exception
		{
			photoGallery.createPhoto(picture, thumbnail);
		}

		public void handleError(Throwable throwable, boolean rollback)
		{
			progressSupport.error(throwable);
		}
	}
}
