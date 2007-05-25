package com.kiwisoft.media.photos;

import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JOptionPane;

import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.media.pics.PictureViewer;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

public class ShowPhotoAction extends SimpleContextAction<Photo>
{
	private ApplicationFrame frame;

	public ShowPhotoAction(ApplicationFrame frame)
	{
		super("Show", Icons.getIcon("photo"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Photo photo=getObject();
		if (photo!=null)
		{
			PictureFile picture=photo.getOriginalPicture();
			if (picture!=null)
			{
				File file=FileUtils.getFile(MediaConfiguration.getRootPath(), picture.getFile());
				if (file.exists()) PictureViewer.create(file, photo.getRotation());
				else showMessageDialog(frame, "File '"+file.getAbsolutePath()+"' is currently not available!", "Error", ERROR_MESSAGE);
			}
		}
	}
}
