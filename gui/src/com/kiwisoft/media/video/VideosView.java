/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.video;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.UIUtils;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class VideosView extends ViewPanel
{
	private SortableTable tblVideos;
	private VideosTableModel tmVideos;
	private DoubleClickListener doubleClickListener;
	private VideoListener videoListener;
	private MediumType type;
	private JScrollPane scrollVideos;

	public VideosView(MediumType type)
	{
		this.type=type;
	}

	public String getName()
	{
		return type.getPluralName();
	}

	protected JComponent createContentPanel()
	{
		tmVideos=new VideosTableModel(type);
		videoListener=new VideoListener();
		VideoManager.getInstance().addCollectionChangeListener(videoListener);

		tblVideos=new SortableTable(tmVideos);
		tblVideos.setAutoCreateColumnsFromModel(true);
		tblVideos.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblVideos.initializeColumns(new MediaTableConfiguration("table.videos"));

//		scrollVideos=new JScrollPane(tblVideos);
		scrollVideos=UIUtils.createMutableScrollPane(tblVideos);
		return scrollVideos;
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblVideos.addMouseListener(doubleClickListener);
		scrollVideos.addMouseListener(doubleClickListener);
	}

	protected void removeComponentListeners()
	{
		tblVideos.removeMouseListener(doubleClickListener);
		scrollVideos.addMouseListener(doubleClickListener);
	}

	public void dispose()
	{
		VideoManager.getInstance().removeCollectionListener(videoListener);
		tmVideos.clear();
		super.dispose();
	}

	private class VideoListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (VideoManager.VIDEOS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Video newVideo=(Video)event.getElement();
						if (newVideo.getType()==type)
						{
							VideosTableModel.Row row=new VideosTableModel.Row(newVideo);
							tmVideos.addRow(row);
						}
						break;
					case CollectionChangeEvent.REMOVED:
					{
						int index=tmVideos.indexOf(event.getElement());
						if (index>=0) tmVideos.removeRowAt(index);
					}
					break;
					case CollectionChangeEvent.CHANGED:
						Video video=(Video)event.getElement();
					{
						int index=tmVideos.indexOf(video);
						if (video.getType()==type)
						{
							if (index<0) tmVideos.addRow(new VideosTableModel.Row(video));
						}
						else
						{
							if (index>=0) tmVideos.removeRowAt(index);
						}
					}
				}
			}
		}
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblVideos.rowAtPoint(e.getPoint());
				SortableTableRow row=tmVideos.getRow(rowIndex);
				if (row!=null)
				{
					MediaManagerFrame wizard=(MediaManagerFrame)getTopLevelAncestor();
					wizard.setCurrentView(new RecordingsView((Video)row.getUserObject()), true);
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblVideos.getSelectedRows();
				Set videos=new HashSet();
				for (int i=0; i<rows.length; i++) videos.add(tmVideos.getObject(rows[i]));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new ShowPropertiesAction(videos));
				popupMenu.addSeparator();
				popupMenu.add(new NewVideoAction());
				popupMenu.add(new DeleteVideoAction(videos));
				popupMenu.show(tblVideos, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
	}

	private static class ShowPropertiesAction extends AbstractAction
	{
		private Video video;

		public ShowPropertiesAction(Set videos)
		{
			super("Eigenschaften");
			if (videos.size()==1) video=(Video)videos.iterator().next();
			setEnabled(video!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			VideoDetailsView.create(video);
		}
	}

	private class NewVideoAction extends AbstractAction
	{
		public NewVideoAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			VideoDetailsView.create(type);
		}
	}

	public static class DeleteVideoAction extends AbstractAction
	{
		private Video video;

		public DeleteVideoAction(Set videos)
		{
			super("Löschen");
			if (videos.size()==1) video=(Video)videos.iterator().next();
			setEnabled(video!=null);
		}

		public void actionPerformed(ActionEvent event)
		{
			if (video.isUsed())
			{
				JOptionPane.showMessageDialog(null,
											  "Das Video '"+video.getName()+"' kann nicht gelöscht werden.",
											  "Meldung",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(null,
													 "Das Video '"+video.getName()+"' wirklick löschen?",
													 "Löschen?",
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					VideoManager.getInstance().dropVideo(video);
					transaction.close();
				}
				catch (Exception e)
				{
					if (transaction!=null)
					{
						try
						{
							transaction.rollback();
						}
						catch (SQLException e1)
						{
							e1.printStackTrace();
							JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getName(), VideosView.class);
		bookmark.setParameter("mediumType", String.valueOf(type.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long typeId=new Long(bookmark.getParameter("mediumType"));
		MediumType type=MediumType.get(typeId);
		frame.setCurrentView(new VideosView(type), true);
	}

}
