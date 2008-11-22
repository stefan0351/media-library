package com.kiwisoft.media.files;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.*;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.swing.*;
import com.kiwisoft.swing.lookup.LookupField;

/**
 * @author Stefan Stiller
 */
public class ImageDetailsView extends DetailsView
{
	public static void create(MediaFile image)
	{
		new DetailsFrame(new ImageDetailsView(image)).show();
	}

	public static MediaFile createDialog(Window owner, MediaFile image)
	{
		ImageDetailsView view=new ImageDetailsView(image);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.image;
		return null;
	}

	public static MediaFile createDialog(Window owner, String name, File file)
	{
		ImageDetailsView view=new ImageDetailsView(null);
		view.nameField.setText(name);
		view.imageField.setFile(file);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.image;
		return null;
	}

	private MediaFile image;

	// Konfigurations Panel
	private JTextField nameField;
	private JTextPane descriptionField;
	private LookupField<ContentType> contentTypeField;
	private ImageField imageField;
	private ImageField thumbnailField;
	private ImageField thumbnail50x50Field;
	private ImageField thumbnailSidebarField;
	private MediaFileReferencesController referencesController;

	private ImageDetailsView(MediaFile picture)
	{
		this.image=picture;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		nameField=new JTextField(40);
		imageField=new ImageField("Original", new Dimension(250, 250));
		contentTypeField=new LookupField<ContentType>(new ContentTypeLookup(MediaType.IMAGE));
		thumbnailField=new ThumbnailField("Thumbnail", new Dimension(160, 120), MediaFileUtils.THUMBNAIL_WIDTH, MediaFileUtils.THUMBNAIL_HEIGHT, "thb", imageField);
		thumbnail50x50Field=new ThumbnailField("50x50", new Dimension(50, 50), 50, 50, "mini", imageField);
		thumbnailSidebarField=new ThumbnailField("SideBar", new Dimension(170, 170), 170, -1, "sb", imageField);
		descriptionField=new JTextPane();
		JScrollPane descriptionPane=new JScrollPane(descriptionField);
		descriptionPane.setPreferredSize(new Dimension(400, 50));
		referencesController=new MediaFileReferencesController();
		JComponent referenceField=referencesController.createComponent();
		referenceField.setPreferredSize(new Dimension(200, 150));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Content Type:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(contentTypeField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Description:"),
			new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(descriptionPane, new GridBagConstraints(1, row, 3, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(new JLabel("Images:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(imageField, new GridBagConstraints(1, row, 1, 2, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(thumbnailField, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(thumbnailSidebarField, new GridBagConstraints(3, row, 1, 2, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(thumbnail50x50Field, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(referenceField, new GridBagConstraints(1, row, 3, 1, 1.0, 1.0, NORTHWEST, BOTH, new Insets(10, 5, 0, 0), 0, 0));

		nameField.getDocument().addDocumentListener(new FrameTitleUpdater());
		imageField.addPropertyChangeListener("file", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				File file=imageField.getFile();
				if (file!=null && file.exists())
				{
					Map<String, ImageFileInfo> thumbnails=null;
					File thumbnail=thumbnailField.getFile();
					if (thumbnail==null || !thumbnail.exists())
					{
						thumbnails=MediaFileUtils.getThumbnails(file);
						ImageFileInfo imageData=thumbnails.get(MediaFile.THUMBNAIL);
						if (imageData!=null) thumbnailField.setFile(imageData.getFile());
					}
					thumbnail=thumbnail50x50Field.getFile();
					if (thumbnail==null || !thumbnail.exists())
					{
						if (thumbnails==null) thumbnails=MediaFileUtils.getThumbnails(file);
						ImageFileInfo imageData=thumbnails.get(MediaFile.THUMBNAIL_50x50);
						if (imageData!=null) thumbnail50x50Field.setFile(imageData.getFile());
					}
					thumbnail=thumbnailSidebarField.getFile();
					if (thumbnail==null || !thumbnail.exists())
					{
						if (thumbnails==null) thumbnails=MediaFileUtils.getThumbnails(file);
						ImageFileInfo imageData=thumbnails.get(MediaFile.THUMBNAIL_SIDEBAR);
						if (imageData!=null) thumbnailSidebarField.setFile(imageData.getFile());
					}
				}
			}
		});
		referencesController.installListeners();
	}


	@Override
	public void dispose()
	{
		referencesController.dispose();
		super.dispose();
	}

	private void initializeData()
	{
		if (image!=null)
		{
			nameField.setText(image.getName());
			thumbnailField.setImageFile(image.getThumbnail());
			thumbnail50x50Field.setImageFile(image.getThumbnail50x50());
			thumbnailSidebarField.setImageFile(image.getThumbnailSidebar());
			imageField.setImageFile(image);
			descriptionField.setText(image.getDescription());
			referencesController.addReferences(image.getReferences());
			contentTypeField.setValue(image.getContentType());
		}
	}

	public boolean apply()
	{
		try
		{
			Set<File> filesToBeDeleted=new HashSet<File>();
			filesToBeDeleted.addAll(imageField.getFilesToBeDeleted());
			filesToBeDeleted.addAll(thumbnailField.getFilesToBeDeleted());
			filesToBeDeleted.addAll(thumbnail50x50Field.getFilesToBeDeleted());
			filesToBeDeleted.addAll(thumbnailSidebarField.getFilesToBeDeleted());
			final String name=nameField.getText();
			if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
			File file=imageField.getFile();
			if (file==null) throw new InvalidDataException("No image is specified!", imageField);
			filesToBeDeleted.remove(file);
			if (!file.exists()) throw new InvalidDataException("File '"+file.getAbsolutePath()+"' doesn't exist!", imageField);
			final String imagePath=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath());
			final String thumbnailPath=getThumbnailPath(thumbnailField);
			filesToBeDeleted.remove(thumbnailField.getFile());
			final String thumbnail50x50Path=getThumbnailPath(thumbnail50x50Field);
			filesToBeDeleted.remove(thumbnail50x50Field.getFile());
			final String thumbnailSidebarPath=getThumbnailPath(thumbnailSidebarField);
			filesToBeDeleted.remove(thumbnailSidebarField.getFile());
			final Collection<IDObject> references=referencesController.getReferences();

			try
			{
				return DBSession.execute(new Transactional()
				{
					public void run() throws Exception
					{
						if (image==null) image=MediaFileManager.getInstance().createImage(MediaConfiguration.PATH_ROOT);
						image.setName(name);
						image.setContentType(contentTypeField.getValue());
						image.setDescription(descriptionField.getText());
						image.setFile(imagePath);
						image.setWidth(imageField.getImageWidth());
						image.setHeight(imageField.getImageHeight());
						image.setThumbnail(MediaConfiguration.PATH_ROOT, thumbnailPath,
												  thumbnailField.getImageWidth(), thumbnailField.getImageHeight());
						image.setThumbnail50x50(MediaConfiguration.PATH_ROOT, thumbnail50x50Path,
												  thumbnail50x50Field.getImageWidth(), thumbnail50x50Field.getImageHeight());
						image.setThumbnailSidebar(MediaConfiguration.PATH_ROOT, thumbnailSidebarPath,
													thumbnailSidebarField.getImageWidth(), thumbnailSidebarField.getImageHeight());
						image.setReferences(references);
					}

					public void handleError(Throwable throwable, boolean rollback)
					{
						JOptionPane.showMessageDialog(ImageDetailsView.this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		catch (InvalidDataException e)
		{
			e.handle();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
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
}
