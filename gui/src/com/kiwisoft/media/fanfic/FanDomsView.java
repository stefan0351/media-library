/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.media.show.ShowLinksAction;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2004/08/28 21:20:14 $
 */
public class FanDomsView extends ViewPanel implements Disposable
{
	private TableController<FanDom> tableController;

	public FanDomsView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Fan Fiction - Domains";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<FanDom> tableModel=new DefaultSortableTableModel<FanDom>(FanDom.NAME);
		for (FanDom domain : FanFicManager.getInstance().getDomains()) tableModel.addRow(new BeanTableRow<FanDom>(domain));
		tableModel.sort();
		getModelListenerList().installPropertyChangeListener(FanFicManager.getInstance(), new UpdateListener());

		tableController=new TableController<FanDom>(tableModel, new DefaultTableConfiguration("fandoms.list", FanDomsView.class, "domains"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new DomainDetailsAction());
				actions.add(new NewDomainAction());
				actions.add(new DeleteDomainAction(frame));
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new DomainDetailsAction());
				actions.add(null);
				actions.add(new NewDomainAction());
				actions.add(new DeleteDomainAction(frame));
				actions.add(null);
				actions.add(new FanFicsAction(frame));
				actions.add(null);
				actions.add(new ShowLinksAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new FanFicsAction(frame);
			}

		};
		return tableController.getComponent();
	}

	@Override
	protected void installComponentListeners()
	{
		super.installComponentListeners();
		tableController.installListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		super.removeComponentListeners();
		tableController.removeListeners();
	}

	@Override
	public void dispose()
	{
		tableController.dispose();
		super.dispose();
	}

	private class UpdateListener extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (FanFicManager.DOMAINS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionPropertyChangeEvent.ADDED:
						FanDom newDomain=(FanDom)event.getElement();
						tableController.getModel().addRow(new BeanTableRow<FanDom>(newDomain));
						break;
					case CollectionPropertyChangeEvent.REMOVED:
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
		return new Bookmark(getTitle(), FanDomsView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new FanDomsView());
	}
}
