package com.kiwisoft.media.show;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.media.medium.Track;
import com.kiwisoft.media.medium.TrackDetailsAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.TableController;

public class ShowTracksView extends ViewPanel
{
	private Show show;

	private TableController<Track> tableController;

	public ShowTracksView(Show show)
	{
		this.show=show;
	}

	@Override
	public String getTitle()
	{
		return show.getTitle()+" - Tracks";
	}

	@Override
	public JComponent createContentPanel(ApplicationFrame frame)
	{
		ShowTracksTableModel tableModel=new ShowTracksTableModel(show);
		tableModel.sort();

		tableController=new TableController<Track>(tableModel, new DefaultTableConfiguration(ShowTracksTableModel.class))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>(1);
				actions.add(new TrackDetailsAction());
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new TrackDetailsAction();
			}
		};
		return tableController.createComponent();
	}

	@Override
	protected void installComponentListeners()
	{
		tableController.installListeners();
		super.installComponentListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		super.removeComponentListeners();
	}

	@Override
	public void dispose()
	{
		tableController.dispose();
		super.dispose();
	}

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), ShowTracksView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Show show=ShowManager.getInstance().getShow(new Long(bookmark.getParameter("show")));
		frame.setCurrentView(new ShowTracksView(show));
	}

}