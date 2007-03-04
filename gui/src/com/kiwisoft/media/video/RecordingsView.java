package com.kiwisoft.media.video;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.db.*;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class RecordingsView extends ViewPanel
{
	// Dates Panel
	private SortableTable tblRecords;
	private RecordsTableModel tmRecords;
	private DoubleClickListener doubleClickListener;
	private Video video;
	private ChainListener<Recording> recordingsListener;
	private JScrollPane scrlRecords;

	public RecordingsView(Video video)
	{
		this.video=video;
	}

	public String getName()
	{
		return "Aufnahmen auf "+video.getName();
	}

	public JComponent createContentPanel(ApplicationFrame frame)
	{
		tmRecords=new RecordsTableModel(video);
		recordingsListener=new RecordingsListener();
		video.getRecordings().addChainListener(recordingsListener);

		tblRecords=new SortableTable(tmRecords);
		tblRecords.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblRecords.initializeColumns(new MediaTableConfiguration("table.recordings"));

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
		video.getRecordings().removeChainListener(recordingsListener);
		tmRecords.clear();
		super.dispose();
	}

	private class RecordingsListener implements ChainListener<Recording>
	{
		public void chainChanged(ChainEvent<Recording> event)
		{
			switch (event.getType())
			{
				case ChainEvent.ADDED:
					Recording newRecording=event.getElement();
					RecordsTableModel.Row row=tmRecords.new Row(newRecording);
					tmRecords.addRow(row);
					break;
				case ChainEvent.REMOVED:
					int index=tmRecords.indexOf(event.getElement());
					if (index>=0) tmRecords.removeRowAt(index);
					break;
				case ChainEvent.CHANGED:
					tmRecords.sort();
					break;
			}
		}
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
			else if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblRecords.getSelectedRows();
				Set<Recording> records=new HashSet<Recording>();
				for (int i=0; i<rows.length; i++) records.add((Recording)tmRecords.getObject(rows[i]));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new MoveRecordsUpAction(records));
				popupMenu.addSeparator();
				popupMenu.add(new NewRecordAction());
				popupMenu.add(new DeleteRecordAction(records));
				popupMenu.show(tblRecords, e.getX(), e.getY());
				e.consume();
			}
		}
	}

	private class NewRecordAction extends AbstractAction
	{
		public NewRecordAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			RecordingDetailsView.create(video);
		}
	}

	private class MoveRecordsUpAction extends AbstractAction
	{
		private Set<Recording> records;

		public MoveRecordsUpAction(Set<Recording> records)
		{
			super("Nach oben");
			this.records=new TreeSet<Recording>(Chain.getComparator());
			this.records.addAll(records);
			setEnabled(!this.records.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			Set<Recording> selectedObjects=new HashSet<Recording>();
			int[] rows=tblRecords.getSelectedRows();
			for (int i=0; i<rows.length; i++)
			{
				RecordsTableModel.Row tableRow=(RecordsTableModel.Row)tmRecords.getRow(rows[i]);
				selectedObjects.add(tableRow.getUserObject());
			}
			tblRecords.clearSelection();

			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				Iterator it=records.iterator();
				while (it.hasNext())
				{
					Recording recording=(Recording)it.next();
					video.getRecordings().moveUp(recording);
				}
				transaction.close();
			}
			catch (Exception e1)
			{
				try
				{
					if (transaction!=null) transaction.rollback();
				}
				catch (SQLException e2)
				{
					e2.printStackTrace();
				}
				e1.printStackTrace();
				JOptionPane.showMessageDialog(RecordingsView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}

			Iterator it=selectedObjects.iterator();
			while (it.hasNext())
			{
				Object o=it.next();
				int rowIndex=tmRecords.indexOf(o);
				if (rowIndex>=0) tblRecords.getSelectionModel().addSelectionInterval(rowIndex, rowIndex);
			}
		}
	}

	private class DeleteRecordAction extends AbstractAction
	{
		private Collection records;

		public DeleteRecordAction(Collection records)
		{
			super("Löschen");
			this.records=records;
			setEnabled(!records.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			Iterator it=records.iterator();
			while (it.hasNext())
			{
				Recording recording=(Recording)it.next();
				if (recording.isUsed())
				{
					JOptionPane.showMessageDialog(RecordingsView.this,
												  "Die Aufnahme '"+recording.getEvent()+"' kann nicht gelöscht werden.",
												  "Meldung",
												  JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			int option=JOptionPane.showConfirmDialog(RecordingsView.this,
													 "Aufnahmen wirklick löschen?",
													 "Löschen?",
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					it=records.iterator();
					while (it.hasNext())
					{
						Recording record=(Recording)it.next();
						video.dropRecording(record);
					}
					transaction.close();
				}
				catch (Exception e1)
				{
					try
					{
						if (transaction!=null) transaction.rollback();
					}
					catch (SQLException e2)
					{
						e2.printStackTrace();
					}
					e1.printStackTrace();
					JOptionPane.showMessageDialog(RecordingsView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		Bookmark bookmark=new Bookmark(getName(), RecordingsView.class);
		bookmark.setParameter("video", String.valueOf(video.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long typeId=new Long(bookmark.getParameter("video"));
		Video video=VideoManager.getInstance().getVideo(typeId);
		frame.setCurrentView(new RecordingsView(video), true);
	}

}