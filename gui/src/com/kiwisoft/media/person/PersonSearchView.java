/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.person;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.PinAction;
import com.kiwisoft.media.dataimport.TVTVDeLoaderContextAction;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.SearchView;
import com.kiwisoft.swing.SearchController;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonSearchView extends SearchView<Person>
{
	public PersonSearchView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Persons";
	}

	@Override
	protected TableController<Person> createResultTable(final ApplicationFrame frame)
	{
		SortableTableModel<Person> tableModel=new DefaultSortableTableModel<Person>(Person.GENDER, Person.NAME);
		return new TableController<Person>(tableModel, new DefaultTableConfiguration("persons.list", PersonSearchView.class, "persons"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new PersonDetailsAction());
				actions.add(new NewPersonAction());
				actions.add(new DeletePersonAction(frame));
				actions.add(new ShowPersonCreditsAction(frame));
				actions.add(new PersonAirdatesAction(frame));
				actions.add(new PinAction(getSearchController()));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();

				ComplexAction downloadAction=new ComplexAction("Download");
				downloadAction.addAction(new TVTVDeLoaderContextAction(frame));

				actions.add(new PersonDetailsAction());
				actions.add(null);
				actions.add(new NewPersonAction());
				actions.add(new DeletePersonAction(frame));
				actions.add(new MergePersonsAction(frame));
				actions.add(null);
				actions.add(new ShowPersonCreditsAction(frame));
				actions.add(new PersonAirdatesAction(frame));
				actions.add(downloadAction);
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new PersonDetailsAction();
			}
		};
	}

	@Override
	protected SearchController<Person> createSearchController(TableController<Person> personTableController)
	{
		return new SearchController<Person>(personTableController)
		{
			@Override
			protected Set<Person> doSearch(String searchText)
			{
				if (StringUtils.isEmpty(searchText)) return DBLoader.getInstance().loadSet(Person.class, null, "limit 1001");
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				Set<Person> persons=new HashSet<Person>();
				persons.addAll(DBLoader.getInstance().loadSet(Person.class, null, "name like ? limit 1001", searchText));
				if (persons.size()<1001)
				{
					persons.addAll(DBLoader.getInstance().loadSet(Person.class, "names", "names.type=? and names.ref_id=persons.id"+
																						 " and names.name like ? limit "+(1001-persons.size()),
																  Name.PERSON, searchText));
				}
				return persons;
			}
		};
	}

	@Override
	protected void installCollectionListener()
	{
		getModelListenerList().addDisposable(PersonManager.getInstance().addCollectionChangeListener(new CollectionObserver(PersonManager.PERSONS)));
		super.installCollectionListener();
	}
}
