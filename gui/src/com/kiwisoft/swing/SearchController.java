package com.kiwisoft.swing;

import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.swing.table.BeanTableRow;
import com.kiwisoft.media.Pinnable;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * @author Stefan Stiller
 * @since 11.12.2010
 */
public abstract class SearchController<T> implements Pinnable
{
	private TableController<T> tableController;
	private JTextField searchField;
	private JLabel resultLabel;

	private boolean pinned;
	private Set<String> searches=new LinkedHashSet<String>();

	protected SearchController(TableController<T> tableController)
	{
		this.tableController=tableController;
		searchField=new JTextField();
		searchField.addActionListener(new SearchActionListener());
		resultLabel=new JLabel("No search executed.");
	}

	public JLabel getResultLabel()
	{
		return resultLabel;
	}

	public JTextField getSearchField()
	{
		return searchField;
	}

	@Override
	public boolean isPinned()
	{
		return pinned;
	}

	@Override
	public void setPinned(boolean b)
	{
		pinned=b;
	}

	public void runSearch(String searchText, boolean pinned)
	{
		Set<T> resultSet=doSearch(searchText);
		SortableTableModel<T> tableModel=tableController.getModel();
		if (!pinned) tableModel.clear();
		List<SortableTableRow<T>> rows=new ArrayList<SortableTableRow<T>>(resultSet.size());
		for (T object : resultSet) rows.add(createRow(object));
		tableModel.addRows(rows);
		tableModel.sort();
		int rowCount=rows.size();
		if (rows.isEmpty()) resultLabel.setText("No rows found.");
		else if (rowCount==1) resultLabel.setText("1 row found.");
		else if (rowCount>1000) resultLabel.setText("More than 1000 Row(s) found.");
		else resultLabel.setText(rowCount+" rows found.");
		if (!pinned) searches.clear();
		searches.add(searchText);
	}

	protected abstract Set<T> doSearch(String searchText);

	protected SortableTableRow<T> createRow(T object)
	{
		return new BeanTableRow<T>(object);
	}

	public void addRow(T object)
	{
		SortableTableModel<T> model=tableController.getModel();
		model.addRow(createRow(object));
		model.sort();
	}

	public Set<String> getSearches()
	{
		return searches;
	}

	public SortableTableModel<T> getModel()
	{
		return tableController.getModel();
	}

	private class SearchActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			runSearch(searchField.getText(), pinned);
		}

	}
}
