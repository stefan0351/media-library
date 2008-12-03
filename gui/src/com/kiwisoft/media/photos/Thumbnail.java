package com.kiwisoft.media.photos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.ImageFileInfo;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.ImagePanel;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.JobQueue;

/**
 * @author Stefan Stiller
 */
public class Thumbnail extends JPanel implements ChainLink, Disposable, PropertyChangeListener
{
	private Photo photo;
	private boolean selected;
	private ImagePanel photoComponent;
	private JLabel infoLabel;

	public Thumbnail(Photo photo)
	{
		super(new BorderLayout(0, 0));
		this.photo=photo;

		photoComponent=new ImagePanel(new Dimension(MediaFileUtils.THUMBNAIL_WIDTH, MediaFileUtils.THUMBNAIL_HEIGHT));
		photoComponent.setOpaque(false);
		infoLabel=new JLabel();
		infoLabel.setOpaque(true);
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setSelected(false);

		setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
		add(photoComponent, BorderLayout.CENTER);
		add(infoLabel, BorderLayout.SOUTH);

		updateThumbnail();
		updateLabel();

		photo.addPropertyChangeListener(this);
	}

	private void updateThumbnail()
	{
		ImageFile thumbnail=photo.getThumbnail();
		File file=null;
		if (thumbnail!=null) file=thumbnail.getPhysicalFile();
		if (file!=null && file.exists())
		{
			try
			{
				photoComponent.setImage(MediaFileUtils.loadIcon(file.toURI().toURL()));
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			File photoFile=photo.getOriginalPicture().getPhysicalFile();
			if (photoFile.exists())
			{
				JobQueue.getDefaultQueue().addJob(new ThumbnailCreationJob(photo));
			}
		}
	}

	private void updateLabel()
	{
		infoLabel.setText(photo.getDescription());
		infoLabel.setToolTipText(photo.getDescription());
	}

	public Photo getPhoto()
	{
		return photo;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		this.selected=selected;
		if (selected) setBackground(UIManager.getColor("Table.selectionBackground"));
		else setBackground(Color.WHITE);
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		if (Photo.THUMBNAIL.equals(evt.getPropertyName())) updateThumbnail();
		else if (Photo.DESCRIPTION.equals(evt.getPropertyName())) updateLabel();
	}

	public void dispose()
	{
		photo.removePropertyChangeListener(this);
		photo=null;
	}

	public void setChainPosition(int position)
	{
		photo.setChainPosition(position);
	}

	public int getChainPosition()
	{
		return photo.getChainPosition();
	}

	private class ThumbnailCreationJob implements Runnable
	{
		private Photo photo;

		public ThumbnailCreationJob(Photo photo)
		{
			this.photo=photo;
		}

		public void run()
		{
			File photoFile=photo.getOriginalPicture().getPhysicalFile();
			final ImageFileInfo thumbnailInfo=PhotoManager.getInstance().createThumbnail(photoFile, photo.getRotation());
			if (thumbnailInfo!=null)
			{
				boolean success=DBSession.execute(new Transactional()
				{
					public void run() throws Exception
					{
						ImageFile oldThumbnail=photo.getThumbnail();
						ImageFile newThumbnail=new ImageFile(MediaConfiguration.PATH_ROOT, thumbnailInfo);
						photo.setThumbnail(newThumbnail);
						if (oldThumbnail!=null) oldThumbnail.delete();
					}

					public void handleError(Throwable throwable, boolean rollback)
					{
						throwable.printStackTrace();
					}
				});
				if (success)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								photoComponent.setImage(MediaFileUtils.loadIcon(photo.getThumbnail().getPhysicalFile().toURI().toURL()));
							}
							catch (MalformedURLException e)
							{
								e.printStackTrace();
							}
						}
					});
				}
			}
		}
	}
}
