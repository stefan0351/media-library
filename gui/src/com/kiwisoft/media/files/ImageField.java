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
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.style.StyleUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
*/
class ImageField extends JPanel
{
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
				setFile(file);
				MediaConfiguration.setRecentMediaPath(file.getParent());
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
						setFile(null);
						break;
				}
			}
		}
	}

	public void setImageFile(ImageFile imageFile)
	{
		if (imageFile!=null) setFileName(imageFile.getFile());
		else setFileName(null);
	}

	public void setFileName(String fileName)
	{
		if (StringUtils.isEmpty(fileName)) setFile(null);
		else setFile(FileUtils.getFile(MediaConfiguration.getRootPath(), fileName));
	}

	public Set<File> getFilesToBeDeleted()
	{
		return filesToBeDeleted;
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
				setFile(this.file);
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
