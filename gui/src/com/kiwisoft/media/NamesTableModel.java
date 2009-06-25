/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 20, 2003
 * Time: 5:47:20 PM
 */
package com.kiwisoft.media;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.DefaultSortableTableModel;

public class NamesTableModel extends DefaultSortableTableModel<String>
{
	private static final String NAME="name";
	private static final String LANGUAGE="language";

	public NamesTableModel(boolean language)
	{
		super(language ? new String[]{NAME, LANGUAGE} : new String[]{NAME});
		addRow(new Row(null, language ? LanguageManager.getInstance().getLanguageBySymbol("de") : null));
		sort();
	}

	public void addName(String name, Language language)
	{
		addRow(new Row(name, language));
	}

	public Map<String, Language> getNameMap()
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

	public Set<String> getNameSet()
	{
		Set<String> names=new HashSet<String>();
		for (int i=0; i<getRowCount(); i++)
		{
			Row row=(Row)getRow(i);
			String name=row.getName();
			if (!StringUtils.isEmpty(name)) names.add(name);
		}
		return names;
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

		@Override
		public int getSortPriority()
		{
			if (name==null) return 1;
			return 0;
		}

		@Override
		public Class getCellClass(int col, String property)
		{
			if (LANGUAGE.equals(property)) return Language.class;
			return String.class;
		}

		@Override
		public boolean isEditable(int column, String property)
		{
			return true;
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			if (NAME.equals(property)) return name;
			if (LANGUAGE.equals(property)) return language;
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

		@Override
		public int setValue(Object value, int column, String property)
		{
			if (NAME.equals(property))
			{
				String oldName=name;
				if (value instanceof String)
				{
					name=(String)value;
					if (StringUtils.isEmpty(name)) name=null;
				}
				else name=null;
				if (oldName==null && name!=null) addRow(new Row(null, LanguageManager.getInstance().getLanguageBySymbol("de")));
				else if (oldName!=null && name==null) removeRow(this);
				return ROW_UPDATE;
			}
			else if (LANGUAGE.equals(property))
			{
				if (value instanceof Language) language=(Language)value;
				return CELL_UPDATE;
			}
			return NO_UPDATE;
		}
	}
}
