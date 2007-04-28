package com.kiwisoft.media.photos;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

import com.kiwisoft.utils.gui.Disposable;
import com.kiwisoft.utils.gui.ImagePanel;

/**
 * @author Stefan Stiller
 */
public class Thumbnail extends JPanel implements Disposable, PropertyChangeListener
{
	private Photo photo;
	private boolean selected;
	private ImagePanel photoComponent;

	public Thumbnail(Photo photo)
	{
		super(new BorderLayout(0, 10));
		setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
		this.photo=photo;

		Icon thumbnail=PhotoManager.getInstance().getThumbnail(photo);
		photoComponent=new ImagePanel(thumbnail);
		photoComponent.setOpaque(false);
		JLabel infoLabel=new JLabel(photo.getOriginalWidth()+"x"+photo.getOriginalHeight());
		infoLabel.setOpaque(true);
		infoLabel.setBackground(Color.LIGHT_GRAY);
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setSelected(false);
		add(photoComponent, BorderLayout.CENTER);
		add(infoLabel, BorderLayout.SOUTH);

		photo.addPropertyChangeListener(Photo.ROTATION, this);
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
		Icon thumbnail=PhotoManager.getInstance().getThumbnail(photo);
		photoComponent.setImage(thumbnail);
	}

	public void dispose()
	{
		photo.removePropertyChangeListener(Photo.ROTATION, this);
		photo=null;
	}
}
