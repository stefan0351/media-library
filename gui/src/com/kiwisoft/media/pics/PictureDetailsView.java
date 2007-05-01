package com.kiwisoft.media.pics;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.*;

public class PictureDetailsView extends DetailsView
{
	public static void create(Picture picture)
	{
		new DetailsFrame(new PictureDetailsView(picture)).show();
	}

	public static Picture createDialog(JFrame owner, Picture picture)
	{
		PictureDetailsView view=new PictureDetailsView(picture);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.picture;
		return null;
	}

	public static Picture createDialog(JFrame owner, String name)
	{
		PictureDetailsView view=new PictureDetailsView(null);
		view.nameField.setText(name);
		DetailsDialog dialog=new DetailsDialog(owner, view);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK) return view.picture;
		return null;
	}

	private Picture picture;

	// Konfigurations Panel
	private JTextField nameField;
	private ImageField imageField;
	private ImageField thumbnail50x50Field;
	private ImageField thumbnailSidebarField;

	private PictureDetailsView(Picture picture)
	{
		this.picture=picture;
		createContentPanel();
		initializeData();
	}

	protected void createContentPanel()
	{
		nameField=new JTextField(40);
		imageField=new ImageField(new Dimension(250, 250));
		imageField.setBorder(new TitledBorder("Original"));
		thumbnail50x50Field=new ImageField(new Dimension(50, 50));
		thumbnail50x50Field.setBorder(new TitledBorder("50x50"));
		thumbnailSidebarField=new ImageField(new Dimension(170, 170));
		thumbnailSidebarField.setBorder(new TitledBorder("Sidebar"));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(nameField, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(imageField, new GridBagConstraints(1, row, 1, 2, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(thumbnail50x50Field, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(thumbnailSidebarField, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, NORTHWEST, NONE, new Insets(5, 5, 0, 0), 0, 0));

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
						Map<String,PictureManager.ImageData> thumbnails=PictureManager.getThumbnails(file);
						PictureManager.ImageData imageData=thumbnails.get(Picture.THUMBNAIL_50x50);
						if (imageData!=null && (thumbnailMini==null || !thumbnailMini.exists()))
						{
							thumbnail50x50Field.setFile(imageData.getFile());
						}
						imageData=thumbnails.get(Picture.THUMBNAIL_SIDEBAR);
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
		if (picture!=null)
		{
			nameField.setText(picture.getName());
			Thumbnail thumbnail=picture.getThumbnail50x50();
			if (thumbnail!=null) thumbnail50x50Field.setFileName(thumbnail.getFile());
			thumbnail=picture.getThumbnailSidebar();
			if (thumbnail!=null) thumbnailSidebarField.setFileName(thumbnail.getFile());
			imageField.setFileName(picture.getFile());
		}
	}

	public boolean apply()
	{
		try
		{
			final String name=nameField.getText();
			if (StringUtils.isEmpty(name)) throw new InvalidDataException("Name is missing!", nameField);
			File file=imageField.getFile();
			if (file==null) throw new InvalidDataException("No image is specified!", imageField);
			if (!file.exists()) throw new InvalidDataException("File '"+file.getAbsolutePath()+"' doesn't exist!", imageField);
			final String imagePath=FileUtils.getRelativePath(Configurator.getInstance().getString("path.root"), file.getAbsolutePath());
			final String thumbnail50x50Path=getThumbnailPath(thumbnail50x50Field);
			final String thumbnailSidebarPath=getThumbnailPath(thumbnailSidebarField);
			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					if (picture==null) picture=PictureManager.getInstance().createPicture();
					picture.setName(name);
					picture.setFile(imagePath);
					picture.setWidth(imageField.getImageWidth());
					picture.setHeight(imageField.getImageHeight());
					picture.setThumbnail50x50(thumbnail50x50Path, thumbnail50x50Field.getImageWidth(), thumbnail50x50Field.getImageHeight());
					picture.setThumbnailSidebar(thumbnailSidebarPath, thumbnailSidebarField.getImageWidth(), thumbnailSidebarField.getImageHeight());
				}

				public void handleError(Throwable throwable)
				{
					JOptionPane.showMessageDialog(PictureDetailsView.this, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		catch (InvalidDataException e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid Data", JOptionPane.INFORMATION_MESSAGE);
			e.getComponent().requestFocus();
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
			path=FileUtils.getRelativePath(Configurator.getInstance().getString("path.root"), file.getAbsolutePath());
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

	private static class ImageField extends JPanel implements MouseListener
	{
		private File file;
		private int width;
		private int height;

		private ImagePanel imagePanel;

		public ImageField(Dimension size)
		{
			super(new BorderLayout());
			imagePanel=new ImagePanel(size);
			add(imagePanel, BorderLayout.CENTER);
			addMouseListener(this);
		}

		public void setFileName(String fileName)
		{
			if (StringUtils.isEmpty(fileName)) setFile(null);
			else setFile(new File(Configurator.getInstance().getString("path.root"), fileName));
		}

		public File getFile()
		{
			return file;
		}

		public void setFile(File file)
		{
			File oldFile=this.file;
			this.file=file;
			if (file!=null)
			{
				StringBuilder toolTip=new StringBuilder("<html><b>File:</b> "+file.getAbsolutePath()+"<br>");
				if (file.exists())
				{
					try
					{
						ImageIcon icon=ImageUtils.loadIcon(file.toURL());
						imagePanel.setImage(icon);
						width=icon.getIconWidth();
						height=icon.getIconHeight();
						toolTip.append("<b>Size:</b> ").append(width).append("x").append(height);
					}
					catch (Exception e)
					{
						JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						imagePanel.setImage(Icons.getIcon("no-photo-available"));
					}
				}
				else imagePanel.setImage(Icons.getIcon("no-photo-available"));
				toolTip.append("</html>");
				setToolTipText(toolTip.toString());
			}
			else
			{
				imagePanel.setImage(null);
				setToolTipText(null);
			}
			firePropertyChange("file", oldFile, this.file);
		}

		/**
		 * Invoked when the mouse button has been clicked (pressed
		 * and released) on a component.
		 */
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				ImageFileChooser fileChooser=new ImageFileChooser();
				if (file!=null) fileChooser.setSelectedFile(file);
				else
				{
					String path=Configurator.getInstance().getString("path.pictures.recent", null);
					if (path==null) path=Configurator.getInstance().getString("path.root");
					if (path!=null) fileChooser.setCurrentDirectory(new File(path));
				}
				if (fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
				{
					File file=fileChooser.getSelectedFile();
					setFile(file);
					Configurator.getInstance().setString("path.pictures.recent", file.getParent());
				}
			}
		}

		/**
		 * Invoked when a mouse button has been pressed on a component.
		 */
		public void mousePressed(MouseEvent e)
		{
		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 */
		public void mouseReleased(MouseEvent e)
		{
		}

		/**
		 * Invoked when the mouse enters a component.
		 */
		public void mouseEntered(MouseEvent e)
		{
		}

		/**
		 * Invoked when the mouse exits a component.
		 */
		public void mouseExited(MouseEvent e)
		{
		}

		public int getImageWidth()
		{
			return width;
		}

		public int getImageHeight()
		{
			return height;
		}
	}
}
