package com.kiwisoft.media;

import com.kiwisoft.format.DefaultObjectFormat;

public class CountryFormat extends DefaultObjectFormat
{
    public String format(Object value)
    {
        if (value instanceof Country)
        {
            Country country=(Country) value;
            return country.getName();
        }
        return super.format(value);
    }

    public String getIconName(Object value)
    {
        if (value instanceof Country)
        {
            Country country=(Country) value;
            return "country."+country.getSymbol().toLowerCase();
        }
        return super.getIconName(value);
    }
}
