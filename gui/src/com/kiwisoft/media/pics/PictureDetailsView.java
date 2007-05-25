package com.kiwisoft.media.pics;

import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.*;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.media.utils.GuiUtils;

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
		imageField=new ImageField("Original", new Dimension(250, 250));
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
						Map<String, ImageData> thumbnails=PictureManager.getThumbnails(file);
						ImageData imageData=thumbnails.get(Picture.THUMBNAIL_50x50);
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
			PictureFile thumbnail=picture.getThumbnail50x50();
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

	private static class ImageField extends JPanel
	{
		private File file;
		private int width;
		private int height;

		private ImagePanel imagePanel;

		public ImageField(String name, Dimension size, Action... actions)
		{
			super(new GridBagLayout());
			setBorder(new LineBorder(Color.BLACK));

			add(createTitleBar(name),
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(createToolBar(actions),
				new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(createImagePanel(size),
				new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}

		private ImagePanel createImagePanel(Dimension size)
		{
			imagePanel=new ImagePanel(size);
			imagePanel.setBackground(Color.WHITE);
			imagePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			return imagePanel;
		}

		private JLabel createTitleBar(String name)
		{
			JLabel label=new JLabel(name);
			label.setOpaque(true);
			label.setBackground(Utils.darker(UIManager.getColor("Panel.background"), 0.8));
			return label;
		}

		private JToolBar createToolBar(Action[] actions)
		{
			JToolBar toolBar=new JToolBar()
			{
				@Override
				protected JButton createActionComponent(Action a)
				{
					JButton button=super.createActionComponent(a);
					button.setMargin(new Insets(2, 2, 2, 2));
					return button;
				}
			};
			toolBar.setFloatable(false);
			toolBar.setMargin(null);
			toolBar.add(new OpenFileAction());
			for (Action action : actions) toolBar.add(action);
			return toolBar;
		}

		private class OpenFileAction extends AbstractAction
		{
			public OpenFileAction()
			{
				super("Open File", Icons.getIcon("open.file"));
			}

			public void actionPerformed(ActionEvent e)
			{
				ImageFileChooser fileChooser=new ImageFileChooser();
				if (file!=null) fileChooser.setSelectedFile(file);
				else
				{
					String path=Configurator.getInstance().getString("path.pictures.recent", null);
					if (path==null) path=Configurator.getInstance().getString("path.root");
					if (path!=null) fileChooser.setCurrentDirectory(new File(path));
				}
				if (fileChooser.showOpenDialog(ImageField.this)==JFileChooser.APPROVE_OPTION)
				{
					File file=fileChooser.getSelectedFile();
					setFile(file);
					Configurator.getInstance().setString("path.pictures.recent", file.getParent());
				}
			}
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

		public int getImageWidth()
		{
			return width;
		}

		public int getImageHeight()
		{
			return height;
		}
	}

	public class EditThumbnail50x50Action extends AbstractAction
	{
		public EditThumbnail50x50Action()
		{
			super("Edit", Icons.getIcon("edit"));
		}

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
					ImageUtils.convert(imageFile, file);
				}
			}
			if (file!=null && file.exists())
			{
				try
				{
					Utils.run("\""+Configurator.getInstance().getString("image.editor")+"\""
							  +" \""+file.getAbsolutePath()+"\"", null, null);
					thumbnail50x50Field.setFile(file);
				}
				catch (Exception e1)
				{
					GuiUtils.handleThrowable(thumbnail50x50Field, e1);
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
					ImageUtils.resize(imageFile, 170, -1, file);
				}
			}
			if (file!=null && file.exists())
			{
				thumbnailSidebarField.setFile(file);
			}
		}
	}
}
