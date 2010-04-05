/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.channel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.ChannelManager;

public class ChannelsView extends ViewPanel
{
	private ChannelListener channelListener;
	private TableController<Channel> tableController;

	public ChannelsView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Channels";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Channel> tmChannels=new DefaultSortableTableModel<Channel>(Channel.LOGO, Channel.NAME, Channel.RECEIVABLE);
		for (Channel channel : ChannelManager.getInstance().getChannels())
		{
			tmChannels.addRow(new ChannelRow(channel));
		}
		tmChannels.sort();
		channelListener=new ChannelListener();
		ChannelManager.getInstance().addCollectionChangeListener(channelListener);

		tableController=new TableController<Channel>(tmChannels, new DefaultTableConfiguration("channels.list", ChannelsView.class, "channels"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new ChannelDetailsAction());
				actions.add(new NewChannelAction());
				actions.add(new DeleteChannelAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new ChannelDetailsAction());
				actions.add(null);
				actions.add(new NewChannelAction());
				actions.add(new DeleteChannelAction(frame));
				actions.add(null);
				actions.add(new UpdateLogosAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new ChannelDetailsAction();
			}
		};
		return tableController.getComponent();
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
		ChannelManager.getInstance().removeCollectionListener(channelListener);
		tableController.dispose();
		super.dispose();
	}

	private class ChannelListener implements CollectionChangeListener
	{
		@Override
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (ChannelManager.CHANNELS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Channel newChannel=(Channel)event.getElement();
						ChannelRow row=new ChannelRow(newChannel);
						tableController.getModel().addRow(row);
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableController.getModel().indexOf(event.getElement());
						if (index>=0) tableController.getModel().removeRowAt(index);
						break;
				}
			}
		}
	}

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		return new Bookmark(getTitle(), ChannelsView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new ChannelsView());
	}
}
