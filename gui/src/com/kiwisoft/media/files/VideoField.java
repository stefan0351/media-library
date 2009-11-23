package com.kiwisoft.media.files;

import java.awt.Color;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.kiwisoft.media.MediaConfiguration;
import static com.kiwisoft.media.files.MediaFileUtils.DURATION_FORMAT;
import com.kiwisoft.swing.GuiUtils;
import static com.kiwisoft.swing.ComponentUtils.createBoldLabel;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.style.StyleUtils;
import com.kiwisoft.utils.*;
import com.kiwisoft.cfg.Configuration;

/**
 * @author Stefan Stiller
 */
class VideoField extends JPanel
{
	private File file;
	private int width;
	private int height;
	private Set<File> filesToBeDeleted=new HashSet<File>();

	private JLabel fileNameField;
	private JLabel folderField;
	private JLabel sizeField;
	private JLabel videoField;
	private JLabel audioField;
	private long duration;
	private String root;
	private String path;

	public VideoField(String name, Action... actions)
	{
		super(new GridBagLayout());
		setBorder(new LineBorder(Color.BLACK));

		add(createTitleBar(name), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(createToolBar(actions), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(createVideoPanel(), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	private JPanel createVideoPanel()
	{
		JPanel panel=new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));

		fileNameField=new JLabel();
		folderField=new JLabel();
		sizeField=new JLabel();
		videoField=new JLabel();
		audioField=new JLabel();

		int row=0;
		panel.add(createBoldLabel("File Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(fileNameField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		row++;
		panel.add(createBoldLabel("Folder:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 0, 0, 0), 0, 0));
		panel.add(folderField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		row++;
		panel.add(createBoldLabel("Size:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 0, 0, 0), 0, 0));
		panel.add(sizeField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		row++;
		panel.add(createBoldLabel("Video Format:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 0, 0, 0), 0, 0));
		panel.add(videoField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		row++;
		panel.add(createBoldLabel("Audio Format:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 0, 0, 0), 0, 0));
		panel.add(audioField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
		return panel;
	}

	private JLabel createTitleBar(String name)
	{
		JLabel label=new JLabel(name);
		label.setOpaque(true);
		label.setBackground(StyleUtils.darker(UIManager.getColor("Panel.background"), 0.8));
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
		toolBar.add(new SelectFileAction());
		toolBar.add(new PlayFileAction());
		for (Action action : actions) toolBar.add(action);
		toolBar.add(new RemoveAction());
		return toolBar;
	}

	public Set<File> getFilesToBeDeleted()
	{
		return filesToBeDeleted;
	}

	private class SelectFileAction extends AbstractAction
	{
		public SelectFileAction()
		{
			super("Select File", Icons.getIcon("open.file"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser fileChooser=new JFileChooser();
			FileFilter filter=MediaFileUtils.getVideoFileFilter();
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.setFileFilter(filter);
			if (file!=null) fileChooser.setSelectedFile(file);
			else
			{
				String path=MediaConfiguration.getRecentMediaPath();
				if (path==null) path=MediaConfiguration.getRootPath();
				if (path!=null) fileChooser.setCurrentDirectory(new File(path));
			}
			if (fileChooser.showOpenDialog(VideoField.this)==JFileChooser.APPROVE_OPTION)
			{
				File file=fileChooser.getSelectedFile();
				String root=MediaFileUtils.getRootPath(file);
				if (root!=null)
				{
					setFile(root, FileUtils.getRelativePath(Configuration.getInstance().getString(root), file.getAbsolutePath()));
					MediaConfiguration.setRecentMediaPath(file.getParent());
				}
				else JOptionPane.showMessageDialog(VideoField.this, "File is not located in a configured directory.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class PlayFileAction extends AbstractAction
	{
		public PlayFileAction()
		{
			super("Play the selected file", Icons.getIcon("play"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (file!=null)
			{
				try
				{
					Utils.start(file);
				}
				catch (Exception ex)
				{
					GuiUtils.handleThrowable(VideoField.this, ex);
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

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (file!=null)
			{
				int option=JOptionPane.showConfirmDialog(VideoField.this, "Delete file from disk?", "Delete?",
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

	public File getFile()
	{
		return file;
	}

	public void setFile(String root, String path)
	{
		this.root=root;
		this.path=path;
		File oldFile=this.file;
		if (!StringUtils.isEmpty(path)) file=FileUtils.getFile(Configuration.getInstance().getString(root), path);
		else file=null;
		if (file!=null)
		{
			fileNameField.setText(file.getName());
			folderField.setText(file.getParent());
			StringBuilder size=new StringBuilder(org.apache.commons.io.FileUtils.byteCountToDisplaySize(file.length()));
			audioField.setText(null);
			videoField.setText(null);
			try
			{
				MediaFileInfo info=MediaFileUtils.getMediaFileInfo(file);
				width=info.getWidth()!=null ? info.getWidth() : -1;
				height=info.getHeight()!=null ? info.getHeight() : -1;
				size.append("; ").append(width>=0 ? width : "?").append("x").append(height>=0 ? height : "?");
				duration=info.getDuration()!=null ? info.getDuration() : -1;
				if (duration>0) size.append("; ").append(DURATION_FORMAT.format(new Time(duration)));
				audioField.setText(info.getAudioFormat());
				videoField.setText(info.getVideoFormat());
			}
			catch (Exception e)
			{
				GuiUtils.handleThrowable(this, e);
			}
			sizeField.setText(size.toString());
		}
		else
		{
			fileNameField.setText(null);
			folderField.setText(null);
			sizeField.setText(null);
			videoField.setText(null);
			audioField.setText(null);
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

	public long getDuration()
	{
		return duration;
	}

	public String getPath()
	{
		return path;
	}

	public String getRoot()
	{
		return root;
	}
}
