package com.kiwisoft.media.fanfic;

import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.TableConstants;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
*/
public class FanFicPartTableRow extends SortableTableRow<FanFicPart>
{
	private String name;
	private String source;
	private String path;

	public FanFicPartTableRow(FanFicPart part)
	{
		super(part);
		if (part!=null)
		{
			name=part.getName();
			source=part.getSource();
			path=FileUtils.getFile(MediaConfiguration.getFanFicPath(), source).getAbsolutePath();
		}
	}

	public FanFicPartTableRow(String source)
	{
		super(null);
		{
			this.source=source;
			path=FileUtils.getFile(MediaConfiguration.getFanFicPath(), source).getAbsolutePath();
		}
	}

	@Override
	public String getCellFormat(int column, String property)
	{
		if ("path".equals(property)) return "FanFicPart";
		return super.getCellFormat(column, property);
	}

	@Override
	public int setValue(Object value, int column, String property)
	{
		if ("name".equals(property))
		{
			this.name=(String) value;
			return TableConstants.CELL_UPDATE;
		}
		if ("path".equals(property))
		{
			String path=(String) value;
			String source=FileUtils.getRelativePath(MediaConfiguration.getFanFicPath(), path);
			source=StringUtils.replaceStrings(source, "\\", "/");
			this.path=path;
			this.source=source;
			return TableConstants.CELL_UPDATE;
		}
		return TableConstants.NO_UPDATE;
	}

	@Override
	public Object getDisplayValue(int column, String property)
	{
		if ("name".equals(property)) return name;
		if ("path".equals(property)) return path;
		return "";
	}

	@Override
	public Class getCellClass(int col, String property)
	{
		return String.class;
	}

	@Override
	public boolean isEditable(int column, String property)
	{
		return true;
	}

	public String getName()
	{
		return name;
	}

	public String getSource()
	{
		return source;
	}

	public String getPath()
	{
		return path;
	}
}
