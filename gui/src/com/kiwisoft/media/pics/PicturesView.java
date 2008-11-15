package com.kiwisoft.media.pics;

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

public class PicturesView extends ViewPanel
{
	private TableController<Picture> tableController;
	private JLabel resultLabel;
	private JTextField searchField;

	public PicturesView()
	{
	}

	public String getTitle()
	{
		return "Pictures";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Picture> tableModel=new DefaultSortableTableModel<Picture>("name", "file", "width", "height");

		tableController=new TableController<Picture>(tableModel, new DefaultTableConfiguration(PicturesView.class, "table.pictures"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new PictureDetailsAction());
				actions.add(new NewPictureAction());
				actions.add(new ComplexAction("Manage", Icons.getIcon("manage"),
											  new ImportPicturesAction(frame),
											  new CheckPicturesAction(frame),
											  new CreateThumbnailsAction(frame)));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new PictureDetailsAction());
				actions.add(null);
				actions.add(new NewPictureAction());
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new PictureDetailsAction();
			}
		};

		searchField=new JTextField();
		searchField.addActionListener(new PicturesView.SearchActionListener(searchField));

		resultLabel=new JLabel("No search executed.");

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchField, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);

		getModelListenerList().addDisposable(PictureManager.getInstance().addCollectionListener(new CollectionChangeObserver()));

		return panel;
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

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (PictureManager.PICTURES.equals(event.getPropertyName()))
			{
				SortableTableModel<Picture> model=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Picture picture=(Picture)event.getElement();
						model.addRow(new MyRow(picture));
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

	private static class MyRow extends SortableTableRow<Picture> implements PropertyChangeListener
	{
		public MyRow(Picture picture)
		{
			super(picture);
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
			if ("name".equals(property)) return getUserObject().getName();
			else if ("file".equals(property)) return getUserObject().getFile();
			else if ("width".equals(property)) return getUserObject().getWidth();
			else if ("height".equals(property)) return getUserObject().getHeight();
			return null;
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), PicturesView.class);
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
		final PicturesView view=new PicturesView();
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

			Set<Picture> pictures;
			if (StringUtils.isEmpty(searchText)) pictures=PictureManager.getInstance().getPictures();
			else
			{
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				pictures=DBLoader.getInstance().loadSet(Picture.class, null, "name like ? limit 1001", searchText);
			}
			SortableTableModel<Picture> tableModel=tableController.getModel();
			tableModel.clear();
			List<MyRow> rows=new ArrayList<MyRow>(pictures.size());
			for (Picture picture : pictures) rows.add(new MyRow(picture));
			tableModel.addRows(rows);
			tableModel.sort();
			int rowCount=rows.size();
			if (rows.isEmpty()) resultLabel.setText("No pictures found.");
			else if (rowCount==1) resultLabel.setText("1 picture found.");
			else if (rowCount>1000) resultLabel.setText("More than 1000 pictures found.");
			else resultLabel.setText(rowCount+" pictures found.");
		}
	}
}
