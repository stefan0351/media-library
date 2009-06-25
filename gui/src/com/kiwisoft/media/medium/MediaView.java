/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.medium;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.media.Pinnable;
import com.kiwisoft.media.PinAction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MediaView extends ViewPanel implements Pinnable
{
	private MediumListener mediumListener;
	private TableController<Medium> tableController;
	private JLabel resultLabel;
	private JTextField searchField;
	private boolean pinned;

	public MediaView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Media";
	}

	@Override
	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		MediaTableModel tableModel=new MediaTableModel();
		tableController=new TableController<Medium>(tableModel, new DefaultTableConfiguration(MediaTableModel.class))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MediumDetailsAction());
				actions.add(new NewMediumAction());
				actions.add(new DeleteMediumAction(frame));
				actions.add(new TracksAction(frame));
				actions.add(new PinAction(MediaView.this));
//				actions.add(new CDDBAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MediumDetailsAction());
				actions.add(new MediaBulkChangeAction(frame));
				actions.add(null);
				actions.add(new NewMediumAction());
				actions.add(new DeleteMediumAction(frame));
				actions.add(null);
				actions.add(new SetMediumObsoleteAction(frame));
				actions.add(new SetMediumActiveAction(frame));
				actions.add(null);
				actions.add(new TracksAction(frame));
				actions.add(new CreateCoverAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new MediumDetailsAction();
			}
		};

		mediumListener=new MediumListener();
		MediumManager.getInstance().addCollectionChangeListener(mediumListener);

		searchField=new JTextField();
		searchField.addActionListener(new SearchActionListener(searchField));

		resultLabel=new JLabel("No search executed.");

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchField, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);

		return panel;
	}

	@Override
	protected void installComponentListeners()
	{
		tableController.installListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		tableController.removeListeners();
	}

	@Override
	public void dispose()
	{
		MediumManager.getInstance().removeCollectionListener(mediumListener);
		tableController.dispose();
		super.dispose();
	}

	public boolean isPinned()
	{
		return pinned;
	}

	public void setPinned(boolean b)
	{
		pinned=b;
	}

	private class MediumListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (MediumManager.MEDIA.equals(event.getPropertyName()))
			{
				SortableTableModel<Medium> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Medium newMedium=(Medium) event.getElement();
						MediaTableModel.Row row=new MediaTableModel.Row(newMedium);
						tableModel.addRow(row);
						break;
					case CollectionChangeEvent.REMOVED:
					{
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
						break;
					}
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
		Bookmark bookmark=new Bookmark(getTitle(), MediaView.class);
		String searchText=searchField.getText();
		if (!StringUtils.isEmpty(searchText))
		{
			bookmark.setName(getTitle()+": "+searchText);
			bookmark.setParameter("searchText", searchText);
		}
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		final MediaView view=new MediaView();
		final String searchText=bookmark.getParameter("searchText");
		frame.setCurrentView(view);
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

			Set<Medium> media;
			if (StringUtils.isEmpty(searchText))
			{
				media=MediumManager.getInstance().getAllMedia();
			}
			else
			{
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				media=DBLoader.getInstance().loadSet(Medium.class, null, "name like ? or userkey like ?", searchText, searchText);
			}
			SortableTableModel<Medium> tableModel=tableController.getModel();
			if (!pinned) tableModel.clear();
			List<MediaTableModel.Row> rows=new ArrayList<MediaTableModel.Row>(media.size());
			for (Medium medium : media) rows.add(new MediaTableModel.Row(medium));
			tableModel.addRows(rows);
			tableModel.sort();
			int rowCount=rows.size();
			if (rows.isEmpty()) resultLabel.setText("No rows found.");
			else if (rowCount==1) resultLabel.setText("1 row found.");
			else if (rowCount>1000) resultLabel.setText("More than 1000 Row(s) found.");
			else resultLabel.setText(rowCount+" rows found.");
		}
	}

}
