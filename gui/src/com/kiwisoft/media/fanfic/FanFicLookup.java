/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Collection;
import java.util.Collections;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.lookup.TableLookup;
import com.kiwisoft.utils.gui.table.TableConfiguration;

public class FanFicLookup extends TableLookup<FanFic>
{
	private final static String[] COLUMNS=new String[]{"title", "author"};

	public Collection<FanFic> getValues(String text, FanFic currentValue, boolean lookup)
	{
		if (StringUtils.isEmpty(text)) return Collections.emptySet();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			DBLoader dbLoader=DBLoader.getInstance();
			return dbLoader.loadSet(FanFic.class, null, "title like ?", text);
		}
	}

	protected TableConfiguration getTableConfiguration()
	{
		return new MediaTableConfiguration("table.lookup.fanfic");
	}

	public String[] getColumnNames()
	{
		return COLUMNS;
	}

	public Object getColumnValue(FanFic fanFic, int column, String property)
	{
		if (column==0) return fanFic.getTitle();
		else if (column==1) return StringUtils.formatAsEnumeration(((FanFic)fanFic).getAuthors());
		return null;
	}
}
