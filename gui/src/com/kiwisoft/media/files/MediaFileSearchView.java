package com.kiwisoft.media.files;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.SearchView;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.PinAction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Stefan Stiller
 */
public class MediaFileSearchView extends SearchView<MediaFile>
{
	@Override
	public String getTitle()
	{
		return "Media Files";
	}

	@Override
	protected TableController<MediaFile> createResultTable(final ApplicationFrame frame)
	{
		SortableTableModel<MediaFile> tableModel=new DefaultSortableTableModel<MediaFile>("name", "file", "width", "height", "duration");
		return new TableController<MediaFile>(tableModel, new DefaultTableConfiguration("mediafiles.list", MediaFileSearchView.class, "table.files"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MediaFileDetailsAction());
				actions.add(new NewMediaFileAction(frame));
				actions.add(new DeleteMediaFileAction(frame));
				actions.add(new ComplexAction("Manage", Icons.getIcon("manage"),
											  new CheckPicturesAction(frame),
											  new CreateThumbnailsAction(frame)));
				actions.add(new PinAction(MediaFileSearchView.this));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MediaFileDetailsAction());
				actions.add(null);
				actions.add(new NewMediaFileAction(frame));
				actions.add(new DeleteMediaFileAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new MediaFileDetailsAction();
			}
		};
	}

	@Override
	protected SortableTableRow<MediaFile> createRow(MediaFile object)
	{
		return new MyRow(object);
	}

	@Override
	protected Set<MediaFile> doSearch(String searchText)
	{
		if (StringUtils.isEmpty(searchText))
			return DBLoader.getInstance().loadSet(MediaFile.class, null, "limit 1001");
		if (searchText.contains("*")) searchText=searchText.replace('*', '%');
		else searchText="%"+searchText+"%";
		return DBLoader.getInstance().loadSet(MediaFile.class, null, "name like ? limit 1001", searchText);
	}

	@Override
	protected void installCollectionListener()
	{
		getModelListenerList().addDisposable(MediaFileManager.getInstance().addCollectionListener(new CollectionObserver(MediaFileManager.MEDIA_FILES)));
		super.installCollectionListener();
	}

	private static class MyRow extends SortableTableRow<MediaFile> implements PropertyChangeListener
	{
		public MyRow(MediaFile picture)
		{
			super(picture);
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
			fireRowUpdated();
		}

		@Override
		public String getCellFormat(int column, String property)
		{
			if ("duration".equals(property)) return "mediafile";
			return super.getCellFormat(column, property);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			if ("name".equals(property)) return getUserObject();
			else if ("file".equals(property)) return getUserObject().getFile();
			else if ("width".equals(property))
				return getUserObject().getMediaType()!=MediaType.AUDIO ? getUserObject().getWidth() : null;
			else if ("height".equals(property))
				return getUserObject().getMediaType()!=MediaType.AUDIO ? getUserObject().getHeight() : null;
			else if ("duration".equals(property))
				return getUserObject().getMediaType()!=MediaType.IMAGE ? getUserObject().getDurationTime() : null;
			return null;
		}


		@Override
		public Comparable getSortValue(int column, String property)
		{
			if ("name".equals(property)) return getUserObject().getName();
			return super.getSortValue(column, property);
		}
	}
}
