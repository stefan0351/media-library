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

import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.gui.ImagePanel;
import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.utils.FileUtils;

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

		photoComponent=new ImagePanel(new Dimension(Photo.THUMBNAIL_WIDTH, Photo.THUMBNAIL_HEIGHT));
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
		PictureFile thumbnail=photo.getThumbnail();
		if (thumbnail!=null)
		{
			File file=FileUtils.getFile(MediaConfiguration.getRootPath(), thumbnail.getFile());
			if (file.exists())
			{
				try
				{
					photoComponent.setImage(new ImageIcon(file.toURI().toURL()));
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
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
}
