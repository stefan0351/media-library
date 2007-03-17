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

import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class ChannelsView extends ViewPanel
{
	private ChannelListener channelListener;
	private TableController<Channel> tableController;

	public ChannelsView()
	{
	}

	public String getName()
	{
		return "Channels";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		ChannelsTableModel tmChannels=new ChannelsTableModel();
		for (Channel channel : ChannelManager.getInstance().getChannels())
		{
			tmChannels.addRow(new ChannelTableRow(channel));
		}
		tmChannels.sort();
		channelListener=new ChannelListener();
		ChannelManager.getInstance().addCollectionChangeListener(channelListener);

		tableController=new TableController<Channel>(tmChannels, new MediaTableConfiguration("table.channels"))
		{
			@Override
			public List<ContextAction<Channel>> getToolBarActions()
			{
				List<ContextAction<Channel>> actions=new ArrayList<ContextAction<Channel>>();
				actions.add(new ChannelDetailsAction());
				actions.add(new NewChannelAction());
				actions.add(new DeleteChannelAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<Channel>> getContextActions()
			{
				List<ContextAction<Channel>> actions=new ArrayList<ContextAction<Channel>>();
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

	private static class ChannelsTableModel extends SortableTableModel<Channel>
	{
		private static final String[] COLUMNS={"name", "receiving"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
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
			switch (column)
			{
				case 0:
					return getUserObject().getName();
				case 1:
					return Boolean.valueOf(getUserObject().isReceivable());
			}
			return "";
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		return new Bookmark(getName(), ChannelsView.class);
	}

	@SuppressWarnings({"UNUSED_SYMBOL"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new ChannelsView(), true);
	}
}
