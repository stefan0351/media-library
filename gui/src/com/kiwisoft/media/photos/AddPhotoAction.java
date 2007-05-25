package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.pics.ImageData;
import com.kiwisoft.media.MediaConfiguration;

public class AddPhotoAction extends ContextAction<Object>
{
	private ApplicationFrame frame;
	private PhotoGallery photoGallery;

	public AddPhotoAction(ApplicationFrame frame, PhotoGallery photoGallery)
	{
		super("Add", Icons.getIcon("add"));
		this.frame=frame;
		this.photoGallery=photoGallery;
	}

	public void actionPerformed(ActionEvent e)
	{
		ImageFileChooser fileChooser=new ImageFileChooser();
		String path=MediaConfiguration.getRecentPhotoPath();
		if (StringUtils.isEmpty(path)) path=MediaConfiguration.getRootPath();
		if (!StringUtils.isEmpty(path)) fileChooser.setCurrentDirectory(new File(path));
		fileChooser.setMultiSelectionEnabled(true);
		if (JFileChooser.APPROVE_OPTION==fileChooser.showOpenDialog(frame))
		{
			File[] files=fileChooser.getSelectedFiles();
			for (final File file : files)
			{
				MediaConfiguration.setRecentPhotoPath(file.getParent());
				if (file.exists() && file.isFile())
				{
					final ImageDescriptor descriptor=ImageUtils.getImageFormat(file);
					String type=descriptor.getType();
					if ("PNG".equals(type) || "JPEG".equals(type) || "GIF".equals(type))
					{
						ImageData thumbnail=PhotoManager.getInstance().createThumbnail(file, 0);
						if (thumbnail==null)
						{
							JOptionPane.showMessageDialog(frame, "Faild to create thumbnail for file '"+file+"'.");
							break;
						}
						if (!DBSession.execute(new CreatePhotoTx(descriptor, thumbnail))) break;
					}
				}
			}
		}
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
			JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
