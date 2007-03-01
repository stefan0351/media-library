package com.kiwisoft.utils;

import java.util.*;

import com.kiwisoft.utils.gui.table.TableSortDescription;
import com.kiwisoft.utils.gui.table.SortableTableRow;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 11:10:46
 * To change this template use File | Settings | File Templates.
 */
public class SortableWebTable<T>
{
	protected List<Row<T>> rows=new ArrayList<Row<T>>();
	private List<TableSortDescription> sortColumns=new ArrayList<TableSortDescription>(1);
	private Comparator<Row<T>> comparator=new DefaultComparator();
	private String[] columns;

	public SortableWebTable(String... columns)
	{
		this.columns=columns;
	}

	public int getColumnCount()
	{
		return columns.length;
	}

	public String getColumnName(int column)
	{
		return columns[column];
	}

	public boolean isResortable()
	{
		return true;
	}

	public int addRow(Row<T> row)
	{
		rows.add(row);
		return rows.indexOf(row);
	}

	public int getRowCount()
	{
		return rows.size();
	}

	public Row<T> getRow(int row)
	{
		if (row>=0 && row<rows.size()) return rows.get(row);
		else return null;
	}

	public T getObject(int row)
	{
		return getRow(row).getUserObject();
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Row<T> row=getRow(rowIndex);
		if (row==null) return null;
		return row.getDisplayValue(columnIndex, getColumnName(columnIndex));
	}

	public void addSortColumn(TableSortDescription sortDescription)
	{
		sortColumns.add(sortDescription);
	}

	public TableSortDescription getSortDescription(int columnIndex)
	{
		for (Iterator<TableSortDescription> it=sortColumns.iterator(); it.hasNext();)
		{
			TableSortDescription sortDescription=it.next();
			if (sortDescription.getColumn()==columnIndex) return sortDescription;
		}
		return null;
	}

	public void sort()
	{
		if (!sortColumns.isEmpty())
		{
			Collections.sort(rows, comparator);
		}
	}

	public static abstract class Row<T>
	{
		private T userObject;

		protected Row(T userObject)
		{
			this.userObject=userObject;
		}

		public Comparable getSortValue(int column, String property)
		{
			Object value=getDisplayValue(column, property);
			if (value instanceof Comparable)
				return (Comparable) value;
			else if (value==null)
				return "";
			else
				return String.valueOf(value);
		}

		public abstract Object getDisplayValue(int column, String property);

		public boolean keepAtEnd()
		{
			return false;
		}

		public T getUserObject()
		{
			return userObject;
		}
	}

	private class DefaultComparator implements Comparator<Row<T>>
	{
		public int compare(Row<T> row1, Row<T> row2)
		{
			if (row1.keepAtEnd()!=row2.keepAtEnd())
			{
				if (row1.keepAtEnd()) return 1;
				else return -1;
			}
			for (Iterator<TableSortDescription> it=sortColumns.iterator(); it.hasNext();)
			{
				TableSortDescription sortDescription=it.next();
				int column=sortDescription.getColumn();
				Comparable value1=row1.getSortValue(column, getColumnName(column));
				if (value1 instanceof String) value1=((String)value1).toUpperCase();
				Comparable value2=row2.getSortValue(column, getColumnName(column));
				if (value2 instanceof String) value2=((String)value2).toUpperCase();
				int compareResult;
				int direction=sortDescription.getDirection().intValue();
				if (value1.getClass()==value2.getClass()) compareResult=direction*value1.compareTo(value2);
				else compareResult=direction*value1.getClass().getName().compareTo(value2.getClass().getName());
				if (compareResult!=0) return compareResult;
			}
			return 0;
		}
	}
}
