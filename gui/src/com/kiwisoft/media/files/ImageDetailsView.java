package com.kiwisoft.media.files;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.*;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.*;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 * @todo all editing of description
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
	private ImageField imageField;
	private ImageField thumbnail50x50Field;
	private ImageField thumbnailSidebarField;

	private ImageDetailsView(MediaFile picture)
	{
		this.image=picture;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		nameField=new JTextField(40);
		imageField=new ImageField("Original", new Dimension(250, 250), new EditAction());
		thumbnail50x50Field=new ImageField("50x50", new Dimension(50, 50), new EditThumbnail50x50Action());
		thumbnailSidebarField=new ImageField("SideBar", new Dimension(170, 170), new CreateThumbnailSideBarAction());

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Images:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(imageField, new GridBagConstraints(1, row, 1, 2, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(thumbnailSidebarField, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));
		add(thumbnail50x50Field, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));

		nameField.getDocument().addDocumentListener(new FrameTitleUpdater());
		imageField.addPropertyChangeListener("file", new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				File file=imageField.getFile();
				if (file!=null && file.exists())
				{
					File thumbnailMini=thumbnail50x50Field.getFile();
					File thumbnailSide=thumbnailSidebarField.getFile();
					if (thumbnailMini==null || !thumbnailMini.exists() || thumbnailSide==null || !thumbnailSide.exists())
					{
						Map<String, ImageFileInfo> thumbnails=MediaFileUtils.getThumbnails(file);
						ImageFileInfo imageData=thumbnails.get(MediaFile.THUMBNAIL_50x50);
						if (imageData!=null && (thumbnailMini==null || !thumbnailMini.exists()))
						{
							thumbnail50x50Field.setFile(imageData.getFile());
						}
						imageData=thumbnails.get(MediaFile.THUMBNAIL_SIDEBAR);
						if (imageData!=null && (thumbnailSide==null || !thumbnailSide.exists()))
						{
							thumbnailSidebarField.setFile(imageData.getFile());
						}
					}
				}
			}
		});
	}

	private void initializeData()
	{
		if (image!=null)
		{
			nameField.setText(image.getName());
			ImageFile thumbnail=image.getThumbnail50x50();
			if (thumbnail!=null) thumbnail50x50Field.setFileName(thumbnail.getFile());
			thumbnail=image.getThumbnailSidebar();
			if (thumbnail!=null) thumbnailSidebarField.setFileName(thumbnail.getFile());
			imageField.setFileName(image.getFile());
		}
	}

	public boolean apply()
	{
		try
		{
			Set<File> filesToBeDeleted=new HashSet<File>();
			filesToBeDeleted.addAll(imageField.getFilesToBeDeleted());
			filesToBeDeleted.addAll(thumbnail50x50Field.getFilesToBeDeleted());
			filesToBeDeleted.addAll(thumbnailSidebarField.getFilesToBeDeleted());
			final String name=nameField.getText();
			if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
			File file=imageField.getFile();
			if (file==null) throw new InvalidDataException("No image is specified!", imageField);
			filesToBeDeleted.remove(file);
			if (!file.exists()) throw new InvalidDataException("File '"+file.getAbsolutePath()+"' doesn't exist!", imageField);
			final String imagePath=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath());
			final String thumbnail50x50Path=getThumbnailPath(thumbnail50x50Field);
			filesToBeDeleted.remove(thumbnail50x50Field.getFile());
			final String thumbnailSidebarPath=getThumbnailPath(thumbnailSidebarField);
			filesToBeDeleted.remove(thumbnailSidebarField.getFile());
			try
			{
				return DBSession.execute(new Transactional()
				{
					public void run() throws Exception
					{
						if (image==null) image=MediaFileManager.getInstance().createImage(MediaConfiguration.PATH_ROOT);
						image.setName(name);
						image.setFile(imagePath);
						image.setWidth(imageField.getImageWidth());
						image.setHeight(imageField.getImageHeight());
						image.setThumbnail50x50(MediaConfiguration.PATH_ROOT, thumbnail50x50Path,
												  thumbnail50x50Field.getImageWidth(), thumbnail50x50Field.getImageHeight());
						image.setThumbnailSidebar(MediaConfiguration.PATH_ROOT, thumbnailSidebarPath,
													thumbnailSidebarField.getImageWidth(), thumbnailSidebarField.getImageHeight());
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

	public class EditThumbnail50x50Action extends EditAction
	{
		public void actionPerformed(ActionEvent e)
		{
			File file=thumbnail50x50Field.getFile();
			if (file==null || !file.exists())
			{
				File imageFile=imageField.getFile();
				if (imageFile!=null && imageFile.exists())
				{
					String name=FileUtils.getNameWithoutExtension(imageFile);
					file=new File(imageFile.getParentFile(), name+"_mini.jpg");
					MediaFileUtils.convert(imageFile, file);
				}
			}
			edit(thumbnail50x50Field, file);
		}

	}

	protected class EditAction extends AbstractAction
	{
		public EditAction()
		{
			super("Edit", Icons.getIcon("edit"));
		}

		public void actionPerformed(ActionEvent e)
		{
			edit(imageField, imageField.getFile());
		}

		protected void edit(ImageField imageField, File file)
		{
			if (file!=null && file.exists())
			{
				try
				{
					Utils.run("\""+MediaConfiguration.getImageEditorPath()+"\" \""+file.getAbsolutePath()+"\"");
					imageField.setFile(file);
				}
				catch (Exception e1)
				{
					GuiUtils.handleThrowable(imageField, e1);
				}
			}
		}

	}

	public class CreateThumbnailSideBarAction extends AbstractAction
	{
		public CreateThumbnailSideBarAction()
		{
			super("Create", Icons.getIcon("add"));
		}

		public void actionPerformed(ActionEvent e)
		{
			File file=thumbnailSidebarField.getFile();
			if (file==null || !file.exists())
			{
				File imageFile=imageField.getFile();
				if (imageFile!=null && imageFile.exists())
				{
					String name=FileUtils.getNameWithoutExtension(imageFile);
					file=new File(imageFile.getParentFile(), name+"_sb.jpg");
					MediaFileUtils.resize(imageFile, 170, -1, file);
				}
			}
			if (file!=null && file.exists())
			{
				thumbnailSidebarField.setFile(file);
			}
		}
	}
}
