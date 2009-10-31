/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.Collection;
import java.util.Collections;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.lookup.TableLookup;
import com.kiwisoft.swing.table.TableConfiguration;
import com.kiwisoft.swing.table.DefaultTableConfiguration;

public class FanFicLookup extends TableLookup<FanFic>
{
	private final static String[] COLUMNS=new String[]{"title", "author"};

	@Override
	public Collection<FanFic> getValues(String text, FanFic currentValue, int lookup)
	{
		if (lookup>0) return Collections.emptySet();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			DBLoader dbLoader=DBLoader.getInstance();
			return dbLoader.loadSet(FanFic.class, null, "title like ?", text);
		}
	}

	@Override
	protected TableConfiguration getTableConfiguration()
	{
		return new DefaultTableConfiguration(FanFicLookup.class);
	}

	@Override
	public String[] getColumnNames()
	{
		return COLUMNS;
	}

	@Override
	public Object getColumnValue(FanFic fanFic, int column, String property)
	{
		if (column==0) return fanFic.getTitle();
		else if (column==1) return StringUtils.formatAsEnumeration(fanFic.getAuthors());
		return null;
	}
}
