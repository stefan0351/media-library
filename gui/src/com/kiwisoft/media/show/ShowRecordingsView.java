package com.kiwisoft.media.show;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.media.video.RecordingDetailsView;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.utils.gui.ApplicationFrame;

public class ShowRecordingsView extends ViewPanel
{
	private Show show;

	private DynamicTable tblRecords;
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

		tblRecords=new DynamicTable(tmRecords);
		tblRecords.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblRecords.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.show.recordings"));
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