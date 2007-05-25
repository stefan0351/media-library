package com.kiwisoft.media.photos;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.kiwisoft.utils.gui.AutoGridLayout;

/**
 * @author Stefan Stiller
 */
public class ThumbnailPanel extends JPanel
{
	private List<Thumbnail> thumbnails;
	private ListSelectionModel selectionModel;

	public ThumbnailPanel()
	{
		super(new AutoGridLayout(new Dimension(170, 170), 10, 10));
		thumbnails=new ArrayList<Thumbnail>();
		selectionModel=new DefaultListSelectionModel();
		addMouseListener(new MyMouseListener());
		selectionModel.addListSelectionListener(new MyListSelectionListener());
	}

	public void addListSelectionListener(ListSelectionListener selectionListener)
	{
		selectionModel.addListSelectionListener(selectionListener);
	}

	public void addThumbnail(Thumbnail thumbnail)
	{
		if (thumbnails.add(thumbnail))
		{
			int index=thumbnails.indexOf(thumbnail);
			selectionModel.insertIndexInterval(index, 1, true);
		}
		add(thumbnail);
	}

	public void removeThumbnail(Thumbnail thumbnail)
	{
		int index=thumbnails.indexOf(thumbnail);
		if (index>=0)
		{
			thumbnails.remove(index);
			remove(thumbnail);
			selectionModel.removeIndexInterval(index, index);
		}
	}

	public List<Thumbnail> getThumbnails()
	{
		return new ArrayList<Thumbnail>(thumbnails);
	}

	public List<Thumbnail> getSelectedThumbnails()
	{
		int indexMin=selectionModel.getMinSelectionIndex();
		int indexMax=selectionModel.getMaxSelectionIndex();
		if ((indexMin==-1) || (indexMax==-1)) return Collections.emptyList();
		List<Thumbnail> selectedThumbnails=new ArrayList<Thumbnail>();
		for (int i=indexMin; i<=indexMax; i++)
		{
			if (selectionModel.isSelectedIndex(i))
			{
				selectedThumbnails.add(thumbnails.get(i));
			}
		}
		return selectedThumbnails;
	}

	public void clearSelection()
	{
		selectionModel.clearSelection();
	}

	public int indexOf(Photo photo)
	{
		for (int i=0; i<thumbnails.size(); i++)
		{
			Thumbnail thumbnail=thumbnails.get(i);
			if (thumbnail.getPhoto()==photo) return i;
		}
		return -1;
	}

	public void addSelectionInterval(int rowIndex, int rowIndex1)
	{
		selectionModel.addSelectionInterval(rowIndex, rowIndex1);
	}

	public void sort(Comparator<? super Thumbnail> comparator)
	{
		clearSelection();
		Collections.sort(thumbnails, comparator);
		removeAll();
		for (Thumbnail thumbnail : thumbnails) add(thumbnail);
		updateUI();
	}

	private class MyMouseListener extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			Component component=getComponentAt(e.getPoint());
			int index=thumbnails.indexOf(component);
			if (index>=0)
			{
				if ((e.getModifiers()&InputEvent.CTRL_MASK)>0) selectionModel.addSelectionInterval(index, index);
				else selectionModel.setSelectionInterval(index, index);
			}
//			selectionModel.setAnchorSelectionIndex(index);
//			selectionModel.setLeadSelectionIndex(index);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
//			int index=indexAtPoint(e.getPoint());
//			selectionModel.setLeadSelectionIndex(index);
//			selectionModel.setValueIsAdjusting(false);
		}
	}

	private class MyListSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				for (int i=e.getFirstIndex(); i<=e.getLastIndex() && i<thumbnails.size(); i++)
				{
					Thumbnail thumbnail=thumbnails.get(i);
					thumbnail.setSelected(selectionModel.isSelectedIndex(i));
				}
			}
		}
	}
}
