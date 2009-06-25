package com.kiwisoft.media.files;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.*;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.DefaultSortableTableModel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.Bookmark;

/**
 * @author Stefan Stiller
 */
public class MediaFilesView extends ViewPanel
{
	private TableController<MediaFile> tableController;
	private JLabel resultLabel;
	private JTextField searchField;

	public MediaFilesView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Media Files";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<MediaFile> tableModel=new DefaultSortableTableModel<MediaFile>("name", "file", "width", "height", "duration");

		tableController=new TableController<MediaFile>(tableModel, new DefaultTableConfiguration("mediafiles.list", MediaFilesView.class, "table.files"))
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

		searchField=new JTextField();
		searchField.addActionListener(new MediaFilesView.SearchActionListener(searchField));

		resultLabel=new JLabel("No search executed.");

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchField, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);

		getModelListenerList().addDisposable(MediaFileManager.getInstance().addCollectionListener(new CollectionChangeObserver()));

		return panel;
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

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (MediaFileManager.MEDIA_FILES.equals(event.getPropertyName()))
			{
				SortableTableModel<MediaFile> model=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						MediaFile mediaFile=(MediaFile)event.getElement();
						model.addRow(new MyRow(mediaFile));
						model.sort();
						break;
					case CollectionChangeEvent.REMOVED:
						int index=model.indexOf(event.getElement());
						if (index>=0) model.removeRowAt(index);
						break;
				}
			}
		}
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

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), MediaFilesView.class);
		String searchText=searchField.getText();
		if (!StringUtils.isEmpty(searchText))
		{
			bookmark.setName(getTitle()+": "+searchText);
			bookmark.setParameter("searchText", searchText);
		}
		return bookmark;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		final MediaFilesView view=new MediaFilesView();
		frame.setCurrentView(view);
		final String searchText=bookmark.getParameter("searchText");
		if (!StringUtils.isEmpty(searchText))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					view.searchField.setText(searchText);
					view.searchField.postActionEvent();
				}
			});
		}
	}

	private class SearchActionListener implements ActionListener
	{
		private final JTextField searchField;

		public SearchActionListener(JTextField searchField)
		{
			this.searchField=searchField;
		}

		public void actionPerformed(ActionEvent e)
		{
			String searchText=searchField.getText();

			Set<MediaFile> mediaFiles;
			if (StringUtils.isEmpty(searchText))
			{
				mediaFiles=DBLoader.getInstance().loadSet(MediaFile.class, null, "limit 1001");
			}
			else
			{
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				mediaFiles=DBLoader.getInstance().loadSet(MediaFile.class, null, "name like ? limit 1001", searchText);
			}
			SortableTableModel<MediaFile> tableModel=tableController.getModel();
			tableModel.clear();
			List<MyRow> rows=new ArrayList<MyRow>(mediaFiles.size());
			for (MediaFile mediaFile : mediaFiles) rows.add(new MyRow(mediaFile));
			tableModel.addRows(rows);
			tableModel.sort();
			int rowCount=rows.size();
			if (rows.isEmpty()) resultLabel.setText("No media files found.");
			else if (rowCount==1) resultLabel.setText("1 media file found.");
			else if (rowCount>1000) resultLabel.setText("More than 1000 media files found.");
			else resultLabel.setText(rowCount+" media files found.");
		}
	}
}
