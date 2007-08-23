package com.kiwisoft.media.medium;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.collection.ChainEvent;
import com.kiwisoft.collection.ChainListener;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.ChainMoveDownAction;
import com.kiwisoft.utils.gui.table.ChainMoveUpAction;
import com.kiwisoft.utils.gui.table.DefaultTableConfiguration;
import com.kiwisoft.utils.gui.table.TableController;

public class TracksView extends ViewPanel
{
	private Medium medium;

	private TableController<Track> tableController;
	private ChainListener<Track> tracksListener;

	public TracksView(Medium video)
	{
		this.medium=video;
	}

	public String getTitle()
	{
		StringBuilder name=new StringBuilder(20);
		name.append("Tracks of ").append(medium.getName());
		String key=medium.getFullKey();
		if (!StringUtils.isEmpty(key)) name.append(" (").append(key).append(")");
		return name.toString();
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		TracksTableModel tableModel=new TracksTableModel(medium);
		tracksListener=new TracksListener();
		medium.getTracks().addChainListener(tracksListener);

		tableController=new TableController<Track>(tableModel, new DefaultTableConfiguration(TracksView.class, "tracks"))
		{
			@Override
			public List<ContextAction<? super Track>> getToolBarActions()
			{
				List<ContextAction<? super Track>> actions=new ArrayList<ContextAction<? super Track>>();
				actions.add(new TrackDetailsAction());
				actions.add(new NewTrackAction(medium));
				actions.add(new DeleteTrackAction(frame, medium));
				actions.add(new ChainMoveUpAction(this, medium.getTracks()));
				actions.add(new ChainMoveDownAction(this, medium.getTracks()));
				return actions;
			}

			@Override
			public List<ContextAction<? super Track>> getContextActions()
			{
				List<ContextAction<? super Track>> actions=new ArrayList<ContextAction<? super Track>>();
				actions.add(new TrackDetailsAction());
				actions.add(null);
				actions.add(new NewTrackAction(medium));
				actions.add(new DeleteTrackAction(frame, medium));
				actions.add(null);
				actions.add(new ChainMoveUpAction(this, medium.getTracks()));
				actions.add(new ChainMoveDownAction(this, medium.getTracks()));
				return actions;
			}

			@Override
			public ContextAction<Track> getDoubleClickAction()
			{
				return new TrackDetailsAction();
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
		medium.getTracks().removeChainListener(tracksListener);
		tableController.dispose();
		super.dispose();
	}

	private class TracksListener implements ChainListener<Track>
	{
		public void chainChanged(ChainEvent<Track> event)
		{
			TracksTableModel tableModel=(TracksTableModel)tableController.getModel();
			switch (event.getType())
			{
				case ChainEvent.ADDED:
					Track newTrack=event.getElement();
					TracksTableModel.Row row=tableModel.new Row(newTrack);
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
		Bookmark bookmark=new Bookmark(getTitle(), TracksView.class);
		bookmark.setParameter("medium", String.valueOf(medium.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Long typeId=new Long(bookmark.getParameter("medium"));
		Medium video=MediumManager.getInstance().getMedium(typeId);
		frame.setCurrentView(new TracksView(video), true);
	}

}