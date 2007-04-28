package com.kiwisoft.media.show;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.video.RecordingDetailsAction;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;

public class ShowRecordingsView extends ViewPanel
{
	private Show show;

	private TableController<Recording> tableController;

	public ShowRecordingsView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		return show.getTitle()+" - Records";
	}

	public JComponent createContentPanel(ApplicationFrame frame)
	{
		ShowRecordsTableModel tableModel=new ShowRecordsTableModel(show);
		tableModel.sort();

		tableController=new TableController<Recording>(tableModel, new MediaTableConfiguration("table.show.recordings"))
		{
			@Override
			public List<ContextAction<? super Recording>> getToolBarActions()
			{
				List<ContextAction<? super Recording>> actions=new ArrayList<ContextAction<? super Recording>>(1);
				actions.add(new RecordingDetailsAction());
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
		tableController.dispose();
		super.dispose();
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