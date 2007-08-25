package com.kiwisoft.media.pics;

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.ImageUtils;
import com.kiwisoft.swing.progress.Job;
import com.kiwisoft.swing.progress.ProgressListener;
import com.kiwisoft.swing.progress.ProgressSupport;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
 */
public class PicturesImport implements Job
{
	private File directory;
	private String rootPath;

	public PicturesImport(File directory)
	{
		this.directory=directory;
		rootPath=MediaConfiguration.getRootPath();
	}

	public String getName()
	{
		return "Pictures Import";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		final ProgressSupport progressSupport=new ProgressSupport(this, progressListener);
		progressSupport.startStep("Loading files...");
		File[] files=directory.listFiles(new MyFileFilter());
		progressSupport.startStep("Importing files...");
		progressSupport.initialize(true, files.length, null);
		for (final File imageFile : files)
		{
			if (progressSupport.isStoppedByUser()) return false;
			final String relativePath=FileUtils.getRelativePath(rootPath, imageFile.getAbsolutePath());
			Set<Picture> pictures=PictureManager.getInstance().getPictureByFile(relativePath);
			if (pictures.isEmpty())
			{
				progressSupport.info("Importing picture '"+relativePath+"'.");
				final Dimension imageSize=ImageUtils.getImageSize(imageFile);
				if (imageSize!=null)
				{
					final Map<String, ImageData> thumbnails=PictureManager.getThumbnails(imageFile);
					if (!DBSession.execute(new Transactional()
					{
						public void run() throws Exception
						{
							Picture picture=PictureManager.getInstance().createPicture();
							picture.setName(FileUtils.getNameWithoutExtension(imageFile));
							picture.setFile(relativePath);
							picture.setWidth(imageSize.width);
							picture.setHeight(imageSize.height);
							for (Map.Entry<String, ImageData> entry : thumbnails.entrySet())
							{
								ImageData imageData=entry.getValue();
								Dimension size=imageData.getSize();
								picture.setThumbnail(entry.getKey(), FileUtils.getRelativePath(rootPath, imageData.getFile().getAbsolutePath()),
													 size.width, size.height);
							}
						}

						public void handleError(Throwable throwable, boolean rollback)
						{
							progressSupport.error(throwable);
						}
					}))
					{
						return false;
					}
				}
			}
			progressSupport.progress(1, true);
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}

	private static class MyFileFilter implements FileFilter
	{
		private String[] extensions={"jpg", "jpeg", "gif", "png"};
		private Pattern[] excluded={Pattern.compile(".+_(mini|small|sb)\\.[a-z]+", Pattern.CASE_INSENSITIVE)};

		public boolean accept(File file)
		{
			boolean found=false;
			if (file.isFile())
			{
				String fileName=file.getName().toLowerCase();
				for (String extension : extensions)
				{
					if (fileName.endsWith("."+extension))
					{
						found=true;
						break;
					}
				}
				if (found)
				{
					for (Pattern pattern : excluded)
					{
						if (pattern.matcher(fileName).matches())
						{
							found=false;
							break;
						}
					}
				}
			}
			return found;
		}
	}

}
