package com.kiwisoft.media.files;

import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.ImageFileChooser;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.style.StyleUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.cfg.Configuration;

/**
 * @author Stefan Stiller
*/
class ImageField extends JPanel
{
	private String root;
	private String path;
	private File file;
	private int width;
	private int height;
	private Set<File> filesToBeDeleted=new HashSet<File>();

	private ImagePanel imagePanel;

	public ImageField(String name, Dimension size)
	{
		super(new GridBagLayout());
		setBorder(new LineBorder(Color.BLACK));

		add(createTitleBar(name),
			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(createToolBar(),
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(createImagePanel(size),
			new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
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
		label.setBackground(StyleUtils.darker(UIManager.getColor("Panel.background"), 0.8));
		return label;
	}

	private JToolBar createToolBar()
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
		for (Action action : getActions()) toolBar.add(action);
		toolBar.add(new RemoveAction());
		return toolBar;
	}

	protected Action[] getActions()
	{
		return new Action[]{new EditAction()};
	}

	public File getFile()
	{
		return file;
	}

	public void assertIsRoot() throws InvalidDataException
	{
		if (!StringUtils.isEmpty(path) && !MediaConfiguration.PATH_ROOT.equals(root))
			throw new InvalidDataException("Thumbnail must always be in the configured root directory.", this);
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
				String path=MediaConfiguration.getRecentMediaPath();
				if (path==null) path=MediaConfiguration.getRootPath();
				if (path!=null) fileChooser.setCurrentDirectory(new File(path));
			}
			if (fileChooser.showOpenDialog(ImageField.this)==JFileChooser.APPROVE_OPTION)
			{
				File file=fileChooser.getSelectedFile();
				MediaConfiguration.setRecentMediaPath(file.getParent());
				MediaFileInfo fileInfo=MediaFileUtils.getMediaFileInfo(file);
				if (fileInfo.isImage())
				{
					String root=MediaFileUtils.getRootPath(file);
					if (root!=null)
					{
						String filePath=FileUtils.getRelativePath(Configuration.getInstance().getString(root), file.getAbsolutePath());
						setFile(root, filePath);
					}
				}
			}
		}
	}

	private class RemoveAction extends AbstractAction
	{
		public RemoveAction()
		{
			super("Remove", Icons.getIcon("delete"));
		}

		public void actionPerformed(ActionEvent e)
		{
			if (file!=null)
			{
				int option=JOptionPane.showConfirmDialog(ImageField.this, "Delete file from disk?", "Delete?",
											  JOptionPane.YES_NO_CANCEL_OPTION,
											  JOptionPane.QUESTION_MESSAGE);
				switch (option)
				{
					case JOptionPane.YES_OPTION:
						filesToBeDeleted.add(file);
					case JOptionPane.NO_OPTION:
						setFile(null, null);
						break;
				}
			}
		}
	}

	public void setImageFile(ImageFile imageFile)
	{
		if (imageFile!=null) setFile(imageFile.getRoot(), imageFile.getFile());
		else setFile(null, null);
	}

	public Set<File> getFilesToBeDeleted()
	{
		return filesToBeDeleted;
	}

	public String getRoot()
	{
		return root;
	}

	public String getPath()
	{
		return path;
	}

	public void setFile(String root, String path)
	{
		this.root=root;
		this.path=path;
		File oldFile=file;
		if (!StringUtils.isEmpty(path)) file=FileUtils.getFile(Configuration.getInstance().getString(root), path);
		else file=null;
		if (file!=null)
		{
			StringBuilder toolTip=new StringBuilder("<html><b>File:</b> "+file.getAbsolutePath()+"<br>");
			if (file.exists())
			{
				try
				{
					ImageIcon icon=MediaFileUtils.loadIcon(file.toURI().toURL());
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

	protected void edit()
	{
		if (this.file!=null && this.file.exists())
		{
			try
			{
				Utils.run("\""+MediaConfiguration.getImageEditorPath()+"\" \""+this.file.getAbsolutePath()+"\"");
				setFile(root, path);
			}
			catch (Exception e1)
			{
				GuiUtils.handleThrowable(this, e1);
			}
		}
	}

	private class EditAction extends AbstractAction
	{
		public EditAction()
		{
			super("Edit", Icons.getIcon("edit"));
		}

		public void actionPerformed(ActionEvent e)
		{
			edit();
		}
	}
}
