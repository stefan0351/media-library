/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 12, 2003
 * Time: 6:51:02 PM
 */
package com.kiwisoft.media.dataImport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.kiwisoft.utils.DateUtils;

public class ImportConstants
{
	public static final DateFormat DATE_FORMAT=new SimpleDateFormat("d.M.yyyy H:mm");

	static
	{
		DATE_FORMAT.setTimeZone(DateUtils.GMT);
	}

	private ImportConstants()
	{
	}
}
