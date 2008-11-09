package com.kiwisoft.media.person;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class CreditTypeFormat extends DefaultObjectFormat
{
	public String format(Object value)
	{
		if (value instanceof CreditType)
		{
			CreditType creditType=(CreditType)value;
			return creditType.getAsName();
		}
		return super.format(value);
	}
}
