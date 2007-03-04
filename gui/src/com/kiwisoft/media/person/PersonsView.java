/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.person;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.actions.ComplexAction;
import com.kiwisoft.media.dataImport.TVTVDeLoaderAction;
import com.kiwisoft.media.person.DeletePersonAction;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.media.MediaTableConfiguration;

public class PersonsView extends ViewPanel
{
	private PersonListener personListener;
	private TableController<Person> tableController;

	public PersonsView()
	{
	}

	public String getName()
	{
		return "Personen";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Person> tableModel=new ActorsTableModel();

		personListener=new PersonListener();
		PersonManager.getInstance().addCollectionChangeListener(personListener);

		tableController=new TableController<Person>(tableModel, new MediaTableConfiguration("table.actors"))
		{
			public List<ContextAction<Person>> getToolBarActions()
			{
				List<ContextAction<Person>> actions=new ArrayList<ContextAction<Person>>();
				actions.add(new PersonPropertiesAction());
				actions.add(new NewPersonAction());
				actions.add(new DeletePersonAction(frame));
				return actions;
			}

			public List<ContextAction<Person>> getContextActions()
			{
				List<ContextAction<Person>> actions=new ArrayList<ContextAction<Person>>();

				ComplexAction<Person> downloadAction=new ComplexAction<Person>("Download");
				downloadAction.addAction(new TVTVDeLoaderAction<Person>(frame));

				actions.add(new PersonPropertiesAction());
				actions.add(null);
				actions.add(new NewPersonAction());
				actions.add(new DeletePersonAction(frame));
				actions.add(null);
				actions.add(downloadAction);
				return actions;
			}

			public ContextAction<Person> getDoubleClickAction()
			{
				return new PersonPropertiesAction();
			}
		};

		final JTextField searchField=new JTextField();
		searchField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String searchText=searchField.getText();

				Set<Person> persons;
				if (StringUtils.isEmpty(searchText)) persons=PersonManager.getInstance().getPersons();
				else
				{
					if (searchText.contains("*")) searchText=searchText.replace('*', '%');
					else searchText="%"+searchText+"%";
					persons=DBLoader.getInstance().loadSet(Person.class, null, "name like ?", searchText);
				}
				SortableTableModel<Person> tableModel=tableController.getModel();
				tableModel.clear();
				for (Person person : persons) tableModel.addRow(new Row(person));
				tableModel.sort();
			}
		});


		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchField, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);

		return panel;
	}

	protected void installComponentListener()
	{
		tableController.installListeners();
	}

	protected void removeComponentListeners()
	{
		tableController.removeListeners();
	}

	public void dispose()
	{
		PersonManager.getInstance().removeCollectionListener(personListener);
		tableController.dispose();
		super.dispose();
	}

	private class PersonListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (PersonManager.PERSONS.equals(event.getPropertyName()))
			{
				SortableTableModel<Person> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Person newPerson=(Person)event.getElement();
						if (newPerson.isActor())
						{
							Row row=new Row(newPerson);
							tableModel.addRow(row);
						}
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class ActorsTableModel extends SortableTableModel<Person>
	{
		private static final String[] COLUMNS={"name"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class Row extends SortableTableRow<Person> implements PropertyChangeListener
	{
		public Row(Person person)
		{
			super(person);
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
		return new Bookmark(getName(), PersonsView.class);
	}

	@SuppressWarnings({"UNUSED_SYMBOL"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new PersonsView(), true);
	}
}
