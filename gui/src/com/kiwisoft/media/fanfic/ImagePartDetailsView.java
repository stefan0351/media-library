package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.files.MediaFileInfo;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.ImageFileChooser;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.style.StyleUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

public class ImagePartDetailsView extends DetailsView
{
	private JLabel imageHeader;

	public static void create(FanFicPart part)
	{
		new DetailsFrame(new ImagePartDetailsView(part)).show();
	}

	private FanFicPart part;

	private JTextField titleField;
	private ImagePanel imageField;
	private File newImageFile;

	private ImagePartDetailsView(FanFicPart part)
	{
		this.part=part;
		createContentPanel();
		initializeData();
		setTitle(part.getFanFic().getTitle()+" - Part "+(part.getFanFic().getParts().indexOf(part)+1));
	}

	private void initializeData()
	{
		titleField.setText(part.getName());
		setImageFile(part.getContentFile(), false);
	}

	private void setImageFile(File file, boolean newImage)
	{
		if (file!=null && file.exists())
		{
			if (newImage) newImageFile=file;
			try
			{
				String extension=FilenameUtils.getExtension(file.getName());
				ImageIcon icon=MediaFileUtils.loadIcon(file.toURI().toURL());
				imageField.setImage(icon);
				imageHeader.setText("Image ("+extension+"; "+icon.getIconWidth()+"x"+icon.getIconHeight()+")");
			}
			catch (Exception e)
			{
				GuiUtils.handleThrowable(this, e);
			}
		}
		else
		{
			if (newImage) newImageFile=null;
			imageField.setImage(null);
		}
	}

	@Override
	public boolean apply()
	{
		final String title=titleField.getText();

		return DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				part.setName(title);
				System.out.println("FanFicPartDetailsView.apply: part.getId() = "+part.getId());
				if (newImageFile!=null)
				{
					String extension=FilenameUtils.getExtension(newImageFile.getName());
					part.putContent(new FileInputStream(newImageFile), extension, null);
				}
			}

			@Override
			public void handleError(Throwable throwable, boolean rollback)
			{
				GuiUtils.handleThrowable(ImagePartDetailsView.this, throwable);
			}
		});
	}

	protected void createContentPanel()
	{
		titleField=new JTextField();
		imageField=new ImagePanel(new Dimension(600, 400));
		imageField.setBackground(Color.WHITE);
		imageField.setBorder(new EmptyBorder(5, 5, 5, 5));

		imageHeader=new JLabel("Image");
		imageHeader.setOpaque(true);
		imageHeader.setBackground(StyleUtils.darker(UIManager.getColor("Panel.background"), 0.8));

		JPanel imagePanel=new JPanel(new GridBagLayout());
		imagePanel.setBorder(new LineBorder(Color.BLACK));

		JToolBar imageToolBar=new JToolBar()
		{
			@Override
			protected JButton createActionComponent(Action a)
			{
				JButton button=super.createActionComponent(a);
				button.setMargin(new Insets(2, 2, 2, 2));
				return button;
			}
		};
		imageToolBar.setFloatable(false);
		imageToolBar.setMargin(null);
		imageToolBar.add(new SelectFileAction());

		imagePanel.add(imageHeader, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		imagePanel.add(imageToolBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		imagePanel.add(imageField, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		setLayout(new GridBagLayout());
		int row=0;
		add(new JLabel("Title:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(titleField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(imagePanel, new GridBagConstraints(0, row, 2, 1, 1.0, 1.0, WEST, BOTH, new Insets(10, 0, 0, 0), 0, 0));
	}


	@Override
	public JComponent getDefaultFocusComponent()
	{
		return titleField;
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
			ImageFileChooser fileChooser=new ImageFileChooser();
			if (newImageFile!=null) fileChooser.setSelectedFile(newImageFile);
			else
			{
				String path=MediaConfiguration.getRecentMediaPath();
				if (path==null) path=MediaConfiguration.getRootPath();
				if (path!=null) fileChooser.setCurrentDirectory(new File(path));
			}
			if (fileChooser.showOpenDialog(ImagePartDetailsView.this)==JFileChooser.APPROVE_OPTION)
			{
				File file=fileChooser.getSelectedFile();
				MediaConfiguration.setRecentMediaPath(file.getParent());
				MediaFileInfo fileInfo=MediaFileUtils.getMediaFileInfo(file);
				if (fileInfo.isImage()) setImageFile(file, true);
			}
		}
	}

}