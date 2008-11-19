package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.io.File;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.ImageViewer;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

public class ShowPhotoAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowPhotoAction(ApplicationFrame frame)
	{
		super(Photo.class, "Show", Icons.getIcon("photo"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Photo photo=(Photo)getObject();
		if (photo!=null)
		{
			ImageFile picture=photo.getOriginalPicture();
			if (picture!=null)
			{
				File file=picture.getPhysicalFile();
				if (file.exists()) ImageViewer.create(file, photo.getRotation());
				else showMessageDialog(frame, "File '"+file.getAbsolutePath()+"' is currently not available!", "Error", ERROR_MESSAGE);
			}
		}
	}
}
