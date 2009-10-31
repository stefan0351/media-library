/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.medium;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.PinAction;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.SearchView;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MediumSearchView extends SearchView<Medium>
{
	@Override
	public String getTitle()
	{
		return "Media";
	}

	@Override
	protected TableController<Medium> createResultTable(final ApplicationFrame frame)
	{
		return new TableController<Medium>(new MediaTableModel(), new DefaultTableConfiguration(MediaTableModel.class))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MediumDetailsAction());
				actions.add(new NewMediumAction());
				actions.add(new DeleteMediumAction(frame));
				actions.add(new TracksAction(frame));
				actions.add(new PinAction(MediumSearchView.this));
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
	}

	@Override
	protected SortableTableRow<Medium> createRow(Medium object)
	{
		return new MediaTableModel.Row(object);
	}

	@Override
	protected void installCollectionListener()
	{
		getModelListenerList().addDisposable(MediumManager.getInstance().addCollectionChangeListener(new CollectionObserver(MediumManager.MEDIA)));
		super.installCollectionListener();
	}

	@Override
	protected Set<Medium> doSearch(String searchText)
	{
		if (StringUtils.isEmpty(searchText)) return MediumManager.getInstance().getAllMedia();
		if (searchText.contains("*")) searchText=searchText.replace('*', '%');
		else searchText="%"+searchText+"%";
		return DBLoader.getInstance().loadSet(Medium.class, null, "name like ? or userkey like ?", searchText, searchText);
	}

}
