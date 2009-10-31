/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.channel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.swing.icons.IconManager;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.ChannelManager;
import com.kiwisoft.media.channel.NewChannelAction;

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
		SortableTableModel<Channel> tmChannels=new DefaultSortableTableModel<Channel>("icon", "name", "receiving");
		for (Channel channel : ChannelManager.getInstance().getChannels())
		{
			tmChannels.addRow(new ChannelTableRow(channel));
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
						ChannelTableRow row=new ChannelTableRow(newChannel);
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

	private static class ChannelTableRow extends SortableTableRow<Channel> implements PropertyChangeListener
	{
		public ChannelTableRow(Channel channel)
		{
			super(channel);
		}

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					fireRowUpdated();
				}
			});
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			if ("icon".equals(property))
			{
				MediaFile logo=getUserObject().getLogo();
				if (logo!=null) return IconManager.getIconFromFile(logo.getPhysicalFile().getAbsolutePath());
				return null;
			}
			else if ("name".equals(property)) return getUserObject().getName();
			else if ("receiving".equals(property)) return Boolean.valueOf(getUserObject().isReceivable());
			return "";
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
