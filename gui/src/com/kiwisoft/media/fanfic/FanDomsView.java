/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.fanfic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.*;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/08/28 21:20:14 $
 */
public class FanDomsView extends ViewPanel implements Disposable
{
	private UpdateListener updateListener;
	private TableController<FanDom> tableController;

	public FanDomsView()
	{
	}

	public String getTitle()
	{
		return "Fan Fiction - Domains";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<FanDom> tableModel=new DefaultSortableTableModel<FanDom>("name");
		for (FanDom domain : FanFicManager.getInstance().getDomains()) tableModel.addRow(new Row(domain));
		tableModel.sort();
		updateListener=new UpdateListener();
		FanFicManager.getInstance().addCollectionChangeListener(updateListener);

		tableController=new TableController<FanDom>(tableModel, new DefaultTableConfiguration(FanDomsView.class, "domains"))
		{
			@Override
			public List<ContextAction<? super FanDom>> getToolBarActions()
			{
				List<ContextAction<? super FanDom>> actions=new ArrayList<ContextAction<? super FanDom>>();
				actions.add(new DomainDetailsAction());
				actions.add(new NewDomainAction());
				actions.add(new DeleteDomainAction(frame));
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super FanDom>> getContextActions()
			{
				List<ContextAction<? super FanDom>> actions=new ArrayList<ContextAction<? super FanDom>>();
				actions.add(new DomainDetailsAction());
				actions.add(null);
				actions.add(new NewDomainAction());
				actions.add(new DeleteDomainAction(frame));
				actions.add(null);
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public ContextAction<FanDom> getDoubleClickAction()
			{
				return new FanFicsAction<FanDom>(frame);
			}

		};
		return tableController.createComponent();
	}

	protected void installComponentListeners()
	{
		super.installComponentListeners();
		tableController.installListeners();
	}

	protected void removeComponentListeners()
	{
		super.removeComponentListeners();
		tableController.removeListeners();
	}

	public void dispose()
	{
		FanFicManager.getInstance().removeCollectionListener(updateListener);
		tableController.dispose();
	}

	private class UpdateListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFicManager.DOMAINS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						FanDom newDomain=(FanDom)event.getElement();
						tableController.getModel().addRow(new Row(newDomain));
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableController.getModel().indexOf(event.getElement());
						if (index>=0) tableController.getModel().removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class Row extends SortableTableRow<FanDom> implements PropertyChangeListener
	{
		public Row(FanDom domain)
		{
			super(domain);
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
		return new Bookmark(getTitle(), FanDomsView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new FanDomsView(), true);
	}
}
