package com.kiwisoft.media.show;

import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.media.person.*;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.*;

public class ShowCastView extends ViewPanel
{
	// Dates Panel
	private TableController<CastMember> mainCastController;
	private TableController<CastMember> recurringCastController;
	private Show show;
	private CollectionChangeObserver collectionObserver;

	public ShowCastView(Show show)
	{
		this.show=show;
	}

	public String getTitle()
	{
		return show.getTitle()+" - Cast";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<CastMember> mainCastModel=new DefaultSortableTableModel<CastMember>("character", "actor", "voice");
		SortableTableModel<CastMember> recurringCastModel=new DefaultSortableTableModel<CastMember>("character", "actor", "voice");
		createTableData(mainCastModel, recurringCastModel);

		mainCastController=new TableController<CastMember>(mainCastModel, new DefaultTableConfiguration(ShowCastView.class, "cast.main"))
		{
			@Override
			public List<ContextAction<? super CastMember>> getToolBarActions()
			{
				List<ContextAction<? super CastMember>> actions=new ArrayList<ContextAction<? super CastMember>>();
				actions.add(new CastDetailsAction());
				actions.add(new NewCastAction(show, CreditType.MAIN_CAST));
				actions.add(new DeleteCastAction(show, frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super CastMember>> getContextActions()
			{
				List<ContextAction<? super CastMember>> actions=new ArrayList<ContextAction<? super CastMember>>();
				actions.add(new CastDetailsAction());
				actions.add(null);
				actions.add(new NewCastAction(show, CreditType.MAIN_CAST));
				actions.add(new DeleteCastAction(show, frame));
				return actions;
			}

			@Override
			public ContextAction<CastMember> getDoubleClickAction()
			{
				return new CastDetailsAction();
			}
		};
		recurringCastController=new TableController<CastMember>(recurringCastModel, new DefaultTableConfiguration(ShowCastView.class, "cast.recurring"))
		{
			@Override
			public List<ContextAction<? super CastMember>> getToolBarActions()
			{
				List<ContextAction<? super CastMember>> actions=new ArrayList<ContextAction<? super CastMember>>();
				actions.add(new CastDetailsAction());
				actions.add(new NewCastAction(show, CreditType.RECURRING_CAST));
				actions.add(new DeleteCastAction(show, frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super CastMember>> getContextActions()
			{
				List<ContextAction<? super CastMember>> actions=new ArrayList<ContextAction<? super CastMember>>();
				actions.add(new CastDetailsAction());
				actions.add(null);
				actions.add(new NewCastAction(show, CreditType.RECURRING_CAST));
				actions.add(new DeleteCastAction(show, frame));
				return actions;
			}

			@Override
			public ContextAction<CastMember> getDoubleClickAction()
			{
				return new CastDetailsAction();
			}
		};

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Main Cast:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
		row++;
		pnlContent.add(mainCastController.createComponent(),
					   new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, WEST, BOTH, new Insets(5, 0, 0, 0), 0, 0));
		row++;
		pnlContent.add(new JLabel("Recurring Cast:"),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(10, 0, 0, 0), 0, 0));
		row++;
		pnlContent.add(recurringCastController.createComponent(),
					   new GridBagConstraints(0, row, 1, 1, 1.0, 0.5, WEST, BOTH, new Insets(5, 0, 0, 0), 0, 0));

		return pnlContent;
	}

	private void createTableData(SortableTableModel<CastMember> mainCastModel, SortableTableModel<CastMember> recurringCastModel)
	{
		for (CastMember castMember : show.getMainCast()) mainCastModel.addRow(new CastTableRow(castMember));
		mainCastModel.sort();
		for (CastMember castMember : show.getRecurringCast()) recurringCastModel.addRow(new CastTableRow(castMember));
		recurringCastModel.sort();

		collectionObserver=new CollectionChangeObserver();
		show.addCollectionListener(collectionObserver);
	}

	protected void installComponentListeners()
	{
		mainCastController.installListeners();
		recurringCastController.installListeners();
		super.installComponentListeners();
	}

	protected void removeComponentListeners()
	{
		mainCastController.removeListeners();
		recurringCastController.removeListeners();
		super.removeComponentListeners();
	}

	public void dispose()
	{
		show.removeCollectionListener(collectionObserver);
		mainCastController.dispose();
		recurringCastController.dispose();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (Show.MAIN_CAST.equals(event.getPropertyName()))
			{
				SortableTableModel<CastMember> tableModel=mainCastController.getModel();
				handleEvent(event, tableModel);
			}
			else if (Show.RECURRING_CAST.equals(event.getPropertyName()))
			{
				SortableTableModel<CastMember> tableModel=recurringCastController.getModel();
				handleEvent(event, tableModel);
			}
		}

		private void handleEvent(CollectionChangeEvent event, SortableTableModel<CastMember> tableModel)
		{
			switch (event.getType())
			{
				case CollectionChangeEvent.ADDED:
					CastMember newCast=(CastMember)event.getElement();
					CastTableRow row=new CastTableRow(newCast);
					tableModel.addRow(row);
					tableModel.sort();
					break;
				case CollectionChangeEvent.REMOVED:
					int index=tableModel.indexOf(event.getElement());
					if (index>=0) tableModel.removeRowAt(index);
					break;
			}
		}
	}

	private static class CastTableRow extends SortableTableRow<CastMember> implements PropertyChangeListener
	{
		public CastTableRow(CastMember cast)
		{
			super(cast);
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
			CastMember cast=getUserObject();
			switch (column)
			{
				case 0:
					return cast.getCharacterName();
				case 1:
					Person actor=cast.getActor();
					if (actor!=null) return actor.getName();
					return null;
				case 2:
					return cast.getVoice();
			}
			return null;
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), ShowCastView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Show show=ShowManager.getInstance().getShow(new Long(bookmark.getParameter("show")));
		frame.setCurrentView(new ShowCastView(show), true);
	}
}