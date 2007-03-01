package com.kiwisoft.media.show;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.video.RecordingDetailsView;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class ShowRecordingsView extends ViewPanel
{
	private Show show;

	private SortableTable tblRecords;
	private ShowRecordsTableModel tmRecords;
	private DoubleClickListener doubleClickListener;
	private JScrollPane scrlRecords;

	public ShowRecordingsView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		return show.getName()+" - Aufnahmen";
	}

	public JComponent createContentPanel()
	{
		tmRecords=new ShowRecordsTableModel(show);

		tblRecords=new SortableTable(tmRecords);
		tblRecords.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblRecords.initializeColumns(new MediaTableConfiguration("table.show.recordings"));
		tmRecords.sort();

		scrlRecords=new JScrollPane(tblRecords);
		return scrlRecords;
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		scrlRecords.addMouseListener(doubleClickListener);
		tblRecords.addMouseListener(doubleClickListener);
	}

	protected void removeComponentListeners()
	{
		scrlRecords.removeMouseListener(doubleClickListener);
		tblRecords.removeMouseListener(doubleClickListener);
	}

	public void dispose()
	{
		tmRecords.clear();
		super.dispose();
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblRecords.rowAtPoint(e.getPoint());
				SortableTableRow row=tmRecords.getRow(rowIndex);
				if (row!=null) RecordingDetailsView.create((Recording)row.getUserObject());
				e.consume();
			}
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getName(), ShowRecordingsView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Show show=ShowManager.getInstance().getShow(new Long(bookmark.getParameter("show")));
		frame.setCurrentView(new ShowRecordingsView(show), true);
	}

}