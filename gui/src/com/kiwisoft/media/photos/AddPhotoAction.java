package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.actions.ContextAction;

public class AddPhotoAction extends ContextAction<Object>
{
	private ApplicationFrame frame;
	private PhotoBook photoBook;

	public AddPhotoAction(ApplicationFrame frame, PhotoBook photoBook)
	{
		super("Add", Icons.getIcon("add"));
		this.frame=frame;
		this.photoBook=photoBook;
	}

	public void actionPerformed(ActionEvent e)
	{
		ImageFileChooser fileChooser=new ImageFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		if (JFileChooser.APPROVE_OPTION==fileChooser.showOpenDialog(frame))
		{
			File[] files=fileChooser.getSelectedFiles();
			for (final File file : files)
			{
				if (file.exists() && file.isFile())
				{
					final ImageDescriptor descriptor=ImageUtils.getImageFormat(file);
					String type=descriptor.getType();
					if ("PNG".equals(type) || "JPEG".equals(type) || "GIF".equals(type))
					{
						if (!DBSession.execute(new Transactional()
						{
							public void run() throws Exception
							{
								Photo photo=photoBook.createPhoto(file);
								photo.setOriginalWidth(descriptor.getWidth());
								photo.setOriginalHeight(descriptor.getHeight());
							}

							public void handleError(Throwable throwable)
							{
								JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							}
						}))
						{
							break;
						}
					}
				}
			}
		}
	}
}
