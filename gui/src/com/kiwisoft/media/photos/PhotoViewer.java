package com.kiwisoft.media.photos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JScrollPane;
import javax.swing.ImageIcon;

import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.ImagePanel;
import com.kiwisoft.utils.gui.ImageUtils;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class PhotoViewer extends DetailsView
{
	public static void create(Photo photo)
	{
		new DetailsFrame(new PhotoViewer(photo)).show();
	}

	private Photo photo;

	private ImagePanel imagePanel;

	public PhotoViewer(Photo photo)
	{
		this.photo=photo;
		createContentPanel();
		initializeData();
	}

	private void createContentPanel()
	{
		imagePanel=new ImagePanel(new Dimension(800, 600));

		setLayout(new BorderLayout());
		add(new JScrollPane(imagePanel), BorderLayout.CENTER);
	}

	private void initializeData()
	{
		if (photo!=null)
		{
			String fileName=photo.getOriginalFile();
			if (!StringUtils.isEmpty(fileName))
			{
				File file=new File(fileName);
				if (file.exists())
				{
					try
					{
						ImageIcon icon=ImageUtils.loadIcon(file.toURL());
						imagePanel.setImage(icon);
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	public boolean apply()
	{
		return true;
	}
}
