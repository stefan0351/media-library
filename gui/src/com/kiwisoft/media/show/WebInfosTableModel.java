package com.kiwisoft.media.show;

import java.io.IOException;

import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.WebInfo;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
 */
public class WebInfosTableModel<T extends WebInfo> extends SortableTableModel<T>
{
	private static final String[] COLUMNS={"name", "path", "language", "default"};

	private Row defaultRow;
	private boolean requiresDefault;

	public WebInfosTableModel(boolean requiresDefault)
	{
		this.requiresDefault=requiresDefault;
	}

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int column)
	{
		return COLUMNS[column];
	}

	public Row createRow()
	{
		Row row=new Row(null);
		addRow(row);
		if (requiresDefault && getRowCount()==1) defaultRow=row;
		return row;
	}

	public void addInfo(T info)
	{
		Row row=new Row(info);
		addRow(row);
		if (info.isDefault()) defaultRow=row;
		else if (requiresDefault && getRowCount()==1) defaultRow=row;
	}

	public class Row extends SortableTableRow<T>
	{
		private String name;
		private String path;
		private Language language;

		public Row(T info)
		{
			super(info);
			if (info!=null)
			{
				name=info.getName();
				path=FileUtils.getFile(MediaConfiguration.getRootPath(), info.getPath()).getAbsolutePath();
				language=info.getLanguage();
			}
			else language=LanguageManager.getInstance().getLanguageBySymbol("de");
		}

		public String getName()
		{
			return name;
		}

		public Language getLanguage()
		{
			return language;
		}

		public boolean isDefault()
		{
			return defaultRow==this;
		}

		public String getPath() throws IOException
		{
			if (!StringUtils.isEmpty(path))
			{
				String relPath=FileUtils.getRelativePath(MediaConfiguration.getRootPath(), path);
				relPath=StringUtils.replaceStrings(relPath, "\\", "/");
				return relPath;
			}
			return null;
		}

		public String getCellFormat(int column, String property)
		{
			if (column==1) return "ExistingFile";
			return super.getCellFormat(column, property);
		}

		public Class getCellClass(int col, String property)
		{
			switch (col)
			{
				case 0:
				case 1:
					return String.class;
				case 2:
					return Language.class;
				case 3:
					return Boolean.class;
			}
			return super.getCellClass(col, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			switch (column)
			{
				case 0:
					return name;
				case 1:
					return path;
				case 2:
					return language;
				case 3:
					return Boolean.valueOf(defaultRow==this);
			}
			return null;
		}

		public boolean isEditable(int column, String property)
		{
			switch (column)
			{
				case 0:
				case 1:
				case 2:
					return true;
				case 3:
					return !requiresDefault || getRowCount()>1;
			}
			return false;
		}

		public int setValue(Object value, int column, String property)
		{
			switch (column)
			{
				case 0:
					name=(String)value;
					return CELL_UPDATE;
				case 1:
					path=(String)value;
					return CELL_UPDATE;
				case 2:
					language=(Language)value;
					return CELL_UPDATE;
				case 3:
					if (defaultRow!=this && Boolean.TRUE.equals(value))
					{
						defaultRow=this;
						return TABLE_UPDATE;
					}
					else if (!requiresDefault && defaultRow==this && !Boolean.TRUE.equals(value))
					{
						defaultRow=null;
						return CELL_UPDATE;
					}
			}
			return NO_UPDATE;
		}

		public void setName(String name)
		{
			this.name=name;
			fireRowUpdated();
		}

		public void setPath(String path)
		{
			this.path=path;
			fireRowUpdated();
		}

		public void setLanguage(Language language)
		{
			this.language=language;
			fireRowUpdated();
		}
	}
}
