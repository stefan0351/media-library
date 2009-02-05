package com.kiwisoft.media;

import com.kiwisoft.format.DefaultObjectFormat;

public class LanguageFormat extends DefaultObjectFormat
{
    public String format(Object value)
    {
        if (value instanceof Language)
        {
            Language language=(Language) value;
            return language.getName();
        }
        return super.format(value);
    }

    public String getIconName(Object value)
    {
        if (value instanceof Language)
        {
            Language language=(Language) value;
            return "language."+language.getSymbol().toLowerCase();
        }
        return super.getIconName(value);
    }
}
