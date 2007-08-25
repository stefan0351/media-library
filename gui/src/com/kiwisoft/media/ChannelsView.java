/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.swing.icons.IconManager;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;

public class ChannelsView extends ViewPanel
{
	private ChannelListener channelListener;
	private TableController<Channel> tableController;

	public ChannelsView()
	{
	}

	public String getTitle()
	{
		return "Channels";
	}

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

		tableController=new TableController<Channel>(tmChannels, new DefaultTableConfiguration(ChannelsView.class, "channels"))
		{
			@Override
			public List<ContextAction<? super Channel>> getToolBarActions()
			{
				List<ContextAction<? super Channel>> actions=new ArrayList<ContextAction<? super Channel>>();
				actions.add(new ChannelDetailsAction());
				actions.add(new NewChannelAction());
				actions.add(new DeleteChannelAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super Channel>> getContextActions()
			{
				List<ContextAction<? super Channel>> actions=new ArrayList<ContextAction<? super Channel>>();
				actions.add(new ChannelDetailsAction());
				actions.add(null);
				actions.add(new NewChannelAction());
				actions.add(new DeleteChannelAction(frame));
				return actions;
			}

			@Override
			public ContextAction<Channel> getDoubleClickAction()
			{
				return new ChannelDetailsAction();
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
		ChannelManager.getInstance().removeCollectionListener(channelListener);
		tableController.dispose();
		super.dispose();
	}

	private class ChannelListener implements CollectionChangeListener
	{
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

		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			if ("icon".equals(property))
			{
				String logo=getUserObject().getLogo();
				if (!StringUtils.isEmpty(logo)) return IconManager.getIconFromFile(logo);
				return null;
			}
			else if ("name".equals(property)) return getUserObject().getName();
			else if ("receiving".equals(property)) return Boolean.valueOf(getUserObject().isReceivable());
			return "";
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		return new Bookmark(getTitle(), ChannelsView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new ChannelsView(), true);
	}
}
