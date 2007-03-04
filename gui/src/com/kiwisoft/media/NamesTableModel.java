/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 20, 2003
 * Time: 5:47:20 PM
 */
package com.kiwisoft.media;

import java.util.HashMap;
import java.util.Map;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class NamesTableModel extends SortableTableModel<String>
{
	private static final String[] COLUMNS={"name", "language"};

	public NamesTableModel()
	{
		addRow(new Row(null, LanguageManager.getInstance().getLanguageBySymbol("de")));
		sort();
	}

	public void addName(String name, Language language)
	{
		addRow(new Row(name, language));
	}

	public Map<String, Language> getNames()
	{
		Map<String, Language> names=new HashMap<String, Language>();
		for (int i=0; i<getRowCount(); i++)
		{
			Row row=(Row)getRow(i);
			String name=row.getName();
			if (!StringUtils.isEmpty(name)) names.put(name, row.getLanguage());
		}
		return names;
	}

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}


	public class Row extends SortableTableRow<String>
	{
		private String name;
		private Language language;

		public Row(String name, Language language)
		{
			super(name);
			this.name=name;
			this.language=language;
		}

		protected boolean keepAtEnd()
		{
			if (name==null) return true;
			return super.keepAtEnd();
		}

		public Class getCellClass(int col, String property)
		{
			if (col==1) return Language.class;
			return String.class;
		}

		public boolean isEditable(int column, String property)
		{
			return true;
		}

		public Object getDisplayValue(int column, String property)
		{
			switch (column)
			{
				case 0:
					return name;
				case 1:
					return language;
			}
			return null;
		}

		public String getName()
		{
			return name;
		}

		public Language getLanguage()
		{
			return language;
		}

		public int setValue(Object value, int column, String property)
		{
			switch (column)
			{
				case 0:
					String oldName=name;
					if (value instanceof String)
					{
						name=(String)value;
						if (StringUtils.isEmpty(name)) name=null;
					}
					else name=null;
					if (oldName==null && name!=null) addRow(new Row(null, LanguageManager.getInstance().getLanguageBySymbol("de")));
					if (name==null) removeRow(this);
					return ROW_UPDATE;
				case 1:
					if (value instanceof Language) language=(Language)value;
					return CELL_UPDATE;
			}
			return NO_UPDATE;
		}
	}
}
