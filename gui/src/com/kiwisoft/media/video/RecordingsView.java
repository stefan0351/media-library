package com.kiwisoft.media.video;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.ChainEvent;
import com.kiwisoft.utils.db.ChainListener;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;

public class RecordingsView extends ViewPanel
{
	private Video video;

	private TableController<Recording> tableController;
	private ChainListener<Recording> recordingsListener;

	public RecordingsView(Video video)
	{
		this.video=video;
	}

	public String getName()
	{
		StringBuilder name=new StringBuilder(20);
		name.append("Records on ").append(video.getName());
		if (!StringUtils.isEmpty(video.getUserKey())) name.append(" (").append(video.getUserKey()).append(")");
		return name.toString();
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		RecordsTableModel tableModel=new RecordsTableModel(video);
		recordingsListener=new RecordingsListener();
		video.getRecordings().addChainListener(recordingsListener);

		tableController=new TableController<Recording>(tableModel, new MediaTableConfiguration("table.recordings"))
		{
			@Override
			public List<ContextAction<Recording>> getToolBarActions()
			{
				List<ContextAction<Recording>> actions=new ArrayList<ContextAction<Recording>>();
				actions.add(new RecordingDetailsAction());
				actions.add(new NewRecordAction(video));
				actions.add(new DeleteRecordAction(frame, video));
				actions.add(new MoveUpAction(video.getRecordings()));
				actions.add(new MoveDownAction(video.getRecordings()));
				return actions;
			}

			@Override
			public List<ContextAction<Recording>> getContextActions()
			{
				List<ContextAction<Recording>> actions=new ArrayList<ContextAction<Recording>>();
				actions.add(new RecordingDetailsAction());
				actions.add(null);
				actions.add(new NewRecordAction(video));
				actions.add(new DeleteRecordAction(frame, video));
				actions.add(null);
				actions.add(new MoveUpAction(video.getRecordings()));
				actions.add(new MoveDownAction(video.getRecordings()));
				return actions;
			}

			@Override
			public ContextAction<Recording> getDoubleClickAction()
			{
				return new RecordingDetailsAction();
			}
		};
		return tableController.createComponent();
	}

	protected void installComponentListeners()
	{
		tableController.installListeners();
		super.installComponentListeners();
	}

	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		super.removeComponentListeners();
	}

	public void dispose()
	{
		video.getRecordings().removeChainListener(recordingsListener);
		tableController.dispose();
		super.dispose();
	}

	private class RecordingsListener implements ChainListener<Recording>
	{
		public void chainChanged(ChainEvent<Recording> event)
		{
			RecordsTableModel tableModel=(RecordsTableModel)tableController.getModel();
			switch (event.getType())
			{
				case ChainEvent.ADDED:
					Recording newRecording=event.getElement();
					RecordsTableModel.Row row=tableModel.new Row(newRecording);
					tableModel.addRow(row);
					break;
				case ChainEvent.REMOVED:
					int index=tableModel.indexOf(event.getElement());
					if (index>=0) tableModel.removeRowAt(index);
					break;
				case ChainEvent.CHANGED:
					tableModel.sort();
					break;
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