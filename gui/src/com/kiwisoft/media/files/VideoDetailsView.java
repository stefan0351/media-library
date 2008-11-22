package com.kiwisoft.media.files;

import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
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
	private LookupField<ContentType> contentTypeField;
	private JTextPane descriptionField;
	private VideoField videoField;
	private ImageField thumbnailField;
	private MediaFileReferencesController referencesController;

	private VideoDetailsView(MediaFile picture)
	{
		this.video=picture;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		nameField=new JTextField(40);
		contentTypeField=new LookupField<ContentType>(new ContentTypeLookup(MediaType.VIDEO));
		videoField=new VideoField("Video");
		videoField.setPreferredSize(new Dimension(400, videoField.getPreferredSize().height));
		thumbnailField=new ThumbnailField("Thumbnail", new Dimension(160, 120), MediaFileUtils.THUMBNAIL_WIDTH, MediaFileUtils.THUMBNAIL_HEIGHT, "thb", null)
		{
			@Override
			protected void createThumbnail(int width, int height, String suffix)
			{
				File file=getFile();
				if (file==null || !file.exists())
				{
					File videoFile=videoField.getFile();
					if (videoFile!=null && videoFile.exists())
					{
						String name=FileUtils.getNameWithoutExtension(videoFile);
						file=new File(videoFile.getParentFile(), name+"_"+suffix+".jpg");
						MediaFileUtils.createVideoThumbnail(videoFile, width, height, file);
					}
				}
				if (file!=null && file.exists())
				{
					setFile(file);
				}
			}
		};
		descriptionField=new JTextPane();
		JScrollPane descriptionPane=new JScrollPane(descriptionField);
		descriptionPane.setPreferredSize(new Dimension(400, 50));
		referencesController=new MediaFileReferencesController();
		JComponent referenceField=referencesController.createComponent();
		referenceField.setPreferredSize(new Dimension(200, 150));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Content Type:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(contentTypeField, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Description:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(descriptionPane, new GridBagConstraints(1, row, 2, 1, 1.0, 1.0, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Files:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(videoField, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));
		add(thumbnailField, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 10, 0, 0), 0, 0));
		row++;
		add(referenceField, new GridBagConstraints(1, row, 2, 1, 1.0, 1.0, WEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		nameField.getDocument().addDocumentListener(new FrameTitleUpdater());
		videoField.addPropertyChangeListener("file", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				File file=videoField.getFile();
				if (file!=null && file.exists())
				{
					File thumbnail=thumbnailField.getFile();
					if (thumbnail==null || !thumbnail.exists())
					{
						Map<String, ImageFileInfo> thumbnails=MediaFileUtils.getThumbnails(file);
						ImageFileInfo imageFileInfo=thumbnails.get(MediaFile.THUMBNAIL);
						if (imageFileInfo!=null)
						{
							thumbnailField.setFile(imageFileInfo.getFile());
						}
					}
				}
			}
		});
		referencesController.installListeners();
	}

	private void initializeData()
	{
		if (video!=null)
		{
			nameField.setText(video.getName());
			thumbnailField.setImageFile(video.getThumbnail());
			videoField.setFileName(video.getFile());
			descriptionField.setText(video.getDescription());
			referencesController.addReferences(video.getReferences());
			contentTypeField.setValue(video.getContentType());
		}
	}


	@Override
	public void dispose()
	{
		referencesController.dispose();
		super.dispose();
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
		final Collection<IDObject> references=referencesController.getReferences();

		try
		{
			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					if (video==null) video=MediaFileManager.getInstance().createVideo(MediaConfiguration.PATH_ROOT);
					video.setName(name);
					video.setContentType(contentTypeField.getValue());
					video.setDescription(descriptionField.getText());
					video.setFile(videoPath);
					video.setWidth(videoField.getImageWidth());
					video.setHeight(videoField.getImageHeight());
					video.setDuration(videoField.getDuration());
					video.setThumbnail(MediaConfiguration.PATH_ROOT, thumbnailPath, thumbnailField.getImageWidth(), thumbnailField.getImageHeight());
					video.setReferences(references);
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
			if (StringUtils.isEmpty(name)) setTitle("Video: <unknown>");
			else setTitle("Video: "+name);
		}
	}
}
