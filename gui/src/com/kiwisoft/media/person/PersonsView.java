/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.person;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.media.dataImport.TVTVDeLoaderContextAction;
import com.kiwisoft.media.Name;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;

public class PersonsView extends ViewPanel
{
	private PersonListener personListener;
	private TableController<Person> tableController;
	private JLabel resultLabel;

	public PersonsView()
	{
	}

	public String getTitle()
	{
		return "Persons";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Person> tableModel=new DefaultSortableTableModel<Person>("gender", "name");

		personListener=new PersonListener();
		PersonManager.getInstance().addCollectionChangeListener(personListener);

		tableController=new TableController<Person>(tableModel, new DefaultTableConfiguration(PersonsView.class, "persons"))
		{
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new PersonDetailsAction());
				actions.add(new NewPersonAction());
				actions.add(new DeletePersonAction(frame));
				actions.add(new ShowPersonCreditsAction(frame));
				return actions;
			}

			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();

				ComplexAction downloadAction=new ComplexAction("Download");
				downloadAction.addAction(new TVTVDeLoaderContextAction(frame));

				actions.add(new PersonDetailsAction());
				actions.add(null);
				actions.add(new NewPersonAction());
				actions.add(new DeletePersonAction(frame));
				actions.add(null);
				actions.add(new ShowPersonCreditsAction(frame));
				actions.add(downloadAction);
				return actions;
			}

			public ContextAction getDoubleClickAction()
			{
				return new PersonDetailsAction();
			}
		};

		JTextField searchField=new JTextField();
		searchField.addActionListener(new SearchActionListener(searchField));

		resultLabel=new JLabel("No search executed.");

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchField, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);

		return panel;
	}

	protected void installComponentListeners()
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
						Row row=new Row(newPerson);
						tableModel.addRow(row);
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
						break;
				}
			}
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
					return getUserObject().getGender();
				case 1:
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
		return new Bookmark(getTitle(), PersonsView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new PersonsView());
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

			Set<Person> persons;
			if (StringUtils.isEmpty(searchText)) persons=PersonManager.getInstance().getPersons();
			else
			{
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				persons=new HashSet<Person>();
				persons.addAll(DBLoader.getInstance().loadSet(Person.class, null, "name like ? limit 1001", searchText));
				if (persons.size()<1001)
				{
					persons.addAll(DBLoader.getInstance().loadSet(Person.class, "names", "names.type=? and names.ref_id=persons.id" +
																						 " and names.name like ? limit "+(1001-persons.size()),
																 Name.PERSON, searchText));
				}
			}
			SortableTableModel<Person> tableModel=tableController.getModel();
			tableModel.clear();
			List<Row> rows=new ArrayList<Row>(persons.size());
			for (Person person : persons) rows.add(new Row(person));
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
