package com.kiwisoft.media;

import sun.text.Normalizer;

/**
 * @author Stefan Stiller
 */
public class IndexByUtils
{
	private IndexByUtils()
	{
	}

	public static String createIndexBy(String name)
	{
		if (name==null) return name;
		String indexBy=normalizeCharacters(name);
		indexBy=indexBy.replace(" & ", " AND ");
		if (indexBy.startsWith("A ")) indexBy=indexBy.substring(2)+", A";
		if (indexBy.startsWith("THE ")) indexBy=indexBy.substring(4)+", THE";
		return indexBy;
	}

	private static String normalizeCharacters(String name)
	{
		StringBuilder buffer=new StringBuilder(name.length());
		boolean space=false;
		for (int i=0; i<name.length(); i++)
		{
			char ch=name.charAt(i);
			if (Character.isWhitespace(ch))
			{
				space=true;
			}
			else
			{
				ch=Character.toUpperCase(ch);
				ch=Normalizer.normalize(Character.valueOf(ch).toString(), Normalizer.DECOMP, 0).charAt(0);
				Object replacement=null;
				switch (ch)
				{
					case 'ß':
						replacement="SS";
						break;
					case '(':
					case ')':
					case '[':
					case ']':
					case '{':
					case '}':
					case ':':
					case '!':
					case '.':
					case '-':
					case '\'':
					case '"':
						break;
					default :
						replacement=Character.valueOf(ch);
						break;
				}
				if (replacement!=null)
				{
					if (space && buffer.length()>0)
					{
						buffer.append(" ");
						space=false;
					}
					buffer.append(replacement);
				}
			}
		}
		return buffer.toString();
	}

}
