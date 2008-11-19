package com.kiwisoft.media.files;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @todo all editing of description
 */
public class VideoDetailsView extends DetailsView
{
	public static void create(MediaFile video)
	{
		new DetailsFrame(new VideoDetailsView(video)).show();
	}

	public static MediaFile createDialog(Window owner, MediaFile video)
	{
		VideoDetailsView view=new VideoDetailsView(video);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.video;
		return null;
	}

	public static MediaFile createDialog(Window owner, String name, File file)
	{
		VideoDetailsView view=new VideoDetailsView(null);
		view.nameField.setText(name);
		view.videoField.setFile(file);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.video;
		return null;
	}

	private MediaFile video;

	// Konfigurations Panel
	private JTextField nameField;
	private VideoField videoField;
	private ImageField thumbnailField;

	private VideoDetailsView(MediaFile picture)
	{
		this.video=picture;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		nameField=new JTextField(40);
		videoField=new VideoField("Video");
		thumbnailField=new ImageField("Thumbnail", new Dimension(160, 120), new CreateThumbnailAction());

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Name:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Files:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(videoField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(thumbnailField,
			new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		nameField.getDocument().addDocumentListener(new VideoDetailsView.FrameTitleUpdater());
//		videoField.addPropertyChangeListener("file", new PropertyChangeListener()
//		{
//			public void propertyChange(PropertyChangeEvent evt)
//			{
//				File file=imageField.getFile();
//				if (file!=null && file.exists())
//				{
//					File thumbnailMini=thumbnail50x50Field.getFile();
//					File thumbnailSide=thumbnailSidebarField.getFile();
//					if (thumbnailMini==null || !thumbnailMini.exists() || thumbnailSide==null || !thumbnailSide.exists())
//					{
//						Map<String, ImageFileInfo> thumbnails=MediaFileUtils.getThumbnails(file);
//						ImageFileInfo imageData=thumbnails.get(MediaFile.THUMBNAIL_50x50);
//						if (imageData!=null && (thumbnailMini==null || !thumbnailMini.exists()))
//						{
//							thumbnail50x50Field.setFile(imageData.getFile());
//						}
//						imageData=thumbnails.get(MediaFile.THUMBNAIL_SIDEBAR);
//						if (imageData!=null && (thumbnailSide==null || !thumbnailSide.exists()))
//						{
//							thumbnailSidebarField.setFile(imageData.getFile());
//						}
//					}
//				}
//			}
//		});
	}

	private void initializeData()
	{
		if (video!=null)
		{
			nameField.setText(video.getName());
			ImageFile thumbnail=video.getThumbnailSidebar();
			if (thumbnail!=null) thumbnailField.setFileName(thumbnail.getFile());
			thumbnail=video.getThumbnailSidebar();
			if (thumbnail!=null) thumbnailField.setFileName(thumbnail.getFile());
			videoField.setFileName(video.getFile());
		}
	}

	public boolean apply() throws InvalidDataException
	{
		Set<File> filesToBeDeleted=new HashSet<File>();
		filesToBeDeleted.addAll(videoField.getFilesToBeDeleted());
		filesToBeDeleted.addAll(thumbnailField.getFilesToBeDeleted());
		final String name=nameField.getText();
		if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
		File file=videoField.getFile();
		if (file==null) throw new InvalidDataException("No video is specified!", videoField);
		filesToBeDeleted.remove(file);
		if (!file.exists()) throw new InvalidDataException("File '"+file.getAbsolutePath()+"' doesn't exist!", videoField);
		final String videoPath=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath());
		final String thumbnailPath;
		try
		{
			thumbnailPath=getThumbnailPath(thumbnailField);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		filesToBeDeleted.remove(thumbnailField.getFile());
		try
		{
			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					if (video==null) video=MediaFileManager.getInstance().createVideo(MediaConfiguration.PATH_ROOT);
					video.setName(name);
					video.setFile(videoPath);
					video.setWidth(videoField.getImageWidth());
					video.setHeight(videoField.getImageHeight());
					video.setThumbnailSidebar(MediaConfiguration.PATH_ROOT, thumbnailPath,
											  thumbnailField.getImageWidth(), thumbnailField.getImageHeight());
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					JOptionPane.showMessageDialog(VideoDetailsView.this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		finally
		{
			try
			{
				for (File f : filesToBeDeleted)
				{
					if (f.exists() && f.isFile()) f.delete();
				}
				filesToBeDeleted.clear();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private String getThumbnailPath(ImageField imageField) throws InvalidDataException, IOException
	{
		File file=imageField.getFile();
		String path=null;
		if (file!=null)
		{
			if (!file.exists()) throw new InvalidDataException("File '"+file.getAbsolutePath()+"' doesn't exist!", imageField);
			path=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath());
		}
		return path;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=nameField.getText();
			if (StringUtils.isEmpty(name)) setTitle("Picture: <unknown>");
			else setTitle("Picture: "+name);
		}
	}

	public class CreateThumbnailAction extends AbstractAction
	{
		public CreateThumbnailAction()
		{
			super("Create", Icons.getIcon("add"));
		}

		public void actionPerformed(ActionEvent e)
		{
			File file=thumbnailField.getFile();
			if (file==null || !file.exists())
			{
				File videoFile=videoField.getFile();
				if (videoFile!=null && videoFile.exists())
				{
					String name=FileUtils.getNameWithoutExtension(videoFile);
					file=new File(videoFile.getParentFile(), name+"_thb.jpg");
					MediaFileUtils.createVideoThumbnail(videoFile, 160, 120, file);
				}
			}
			if (file!=null && file.exists())
			{
				thumbnailField.setFile(file);
			}
		}
	}
}
