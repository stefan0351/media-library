package com.kiwisoft.media.pics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.app.DetailsFrame;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.swing.ImageUtils;

/**
 * @author Stefan Stiller
 */
public class PictureViewer extends DetailsView
{
	public static void create(File file, int rotation)
	{
		new DetailsFrame(new PictureViewer(file, rotation)).show();
	}

	private File file;
	private int rotation;

	private ImagePanel imagePanel;

	public PictureViewer(File file, int rotation)
	{
		this.file=file;
		this.rotation=rotation;
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
		setTitle("Picture - "+file.getAbsolutePath());
		if (rotation!=0)
		{
			try
			{
				final File rotatedFile=File.createTempFile("pic", "."+FileUtils.getExtension(file), new File(System.getenv("TEMP")));
				getListenerList().addDisposable(new Disposable()
				{
					public void dispose()
					{
						rotatedFile.delete();
					}
				});
				ImageUtils.rotate(file, rotation, rotatedFile);
				file=rotatedFile;
			}
			catch (IOException e)
			{
				GuiUtils.handleThrowable(PictureViewer.this, e);
			}
		}
		try
		{
			ImageIcon icon=ImageUtils.loadIcon(file.toURI().toURL());
			imagePanel.setImage(icon);
		}
		catch (MalformedURLException e)
		{
			GuiUtils.handleThrowable(PictureViewer.this, e);
		}
	}

	public boolean apply()
	{
		return true;
	}
}
