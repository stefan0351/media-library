package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.ExtensionFileFilter;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;

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
		final JFileChooser fileChooser=new JFileChooser();
		ExtensionFileFilter imageFilter=new ExtensionFileFilter("Image Files", "jpg", "jpeg", "gif", "png");
		fileChooser.addChoosableFileFilter(imageFilter);
		fileChooser.addChoosableFileFilter(new ExtensionFileFilter("JPEG Files", "jpg", "jpeg"));
		fileChooser.addChoosableFileFilter(new ExtensionFileFilter("GIF Files", "gif"));
		fileChooser.addChoosableFileFilter(new ExtensionFileFilter("PNG Files", "png"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileFilter(imageFilter);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setAccessory(createPreviewComponent(fileChooser));
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

	private JComponent createPreviewComponent(JFileChooser fileChooser)
	{
		ImagePanel imagePanel=new ImagePanel(new Dimension(200, 200));

		JPanel panel=new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
		panel.add(imagePanel);

		new PreviewUpdater(fileChooser, imagePanel);

		return panel;
	}

	private static class PreviewUpdater implements PropertyChangeListener
	{
		private final JFileChooser fileChooser;
		private final ImagePanel accessory;

		public PreviewUpdater(JFileChooser fileChooser, ImagePanel accessory)
		{
			this.fileChooser=fileChooser;
			this.accessory=accessory;
			fileChooser.addPropertyChangeListener(JFileChooser.SELECTED_FILES_CHANGED_PROPERTY, this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			File[] files=fileChooser.getSelectedFiles();
			if (files!=null && files.length==1)
			{
				File file=files[0];
				if (!file.isDirectory() && file.exists())
				{
					try
					{
						ImageIcon icon=new ImageIcon(file.toURL());
						accessory.setImage(icon);
					}
					catch (MalformedURLException e1)
					{
					}
				}
			}
		}
	}
}
