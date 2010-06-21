package com.kiwisoft.media.books;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 * @since 31.10.2009
 */
public class Isbn
{
	private static Set<String> groupNumbers;
	private static Map<String, List<Range>> publisherNumberRanges=new HashMap<String, List<Range>>();

	private String prefix;
	private String groupNumber;
	private String publisherNumber;
	private String itemNumber;
	private String checkDigit;

	public Isbn(String prefix, String groupNumber, String publisherNumber, String itemNumber, String checkDigit)
	{
		this.prefix=prefix;
		this.groupNumber=groupNumber;
		this.publisherNumber=publisherNumber;
		this.itemNumber=itemNumber;
		this.checkDigit=checkDigit;
	}

	public Isbn(String groupNumber, String publisherNumber, String itemNumber, String checkDigit)
	{
		this.groupNumber=groupNumber;
		this.publisherNumber=publisherNumber;
		this.itemNumber=itemNumber;
		this.checkDigit=checkDigit;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public String getGroupNumber()
	{
		return groupNumber;
	}

	public String getPublisherNumber()
	{
		return publisherNumber;
	}

	public String getItemNumber()
	{
		return itemNumber;
	}

	public String getCheckDigit()
	{
		return checkDigit;
	}

	@Override
	public String toString()
	{
		StringBuilder text=new StringBuilder();
		if (prefix!=null) text.append(prefix).append("-");
		text.append(groupNumber);
		if (publisherNumber!=null && publisherNumber.length()>0) text.append("-").append(publisherNumber);
		text.append("-").append(itemNumber);
		text.append("-").append(checkDigit);
		return text.toString();
	}

	public String getRawNumber()
	{
		StringBuilder text=new StringBuilder();
		if (prefix!=null) text.append(prefix);
		text.append(groupNumber);
		if (publisherNumber!=null) text.append(publisherNumber);
		text.append(itemNumber);
		text.append(checkDigit);
		return text.toString();
	}

	private static Set<String> getGroupNumbers()
	{
		if (groupNumbers==null)
		{
			groupNumbers=new HashSet<String>();
			Properties properties=new Properties();
			try
			{
				properties.load(Isbn.class.getResourceAsStream("IsbnGroups.properties"));
				groupNumbers.addAll(Utils.<String>cast(properties.keySet()));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return groupNumbers;
	}

	private static List<Range> getPublisherNumberRanges(String group)
	{
		List<Range> ranges=publisherNumberRanges.get(group);
		if (ranges==null)
		{
			ranges=new ArrayList<Range>();
			publisherNumberRanges.put(group, ranges);
			Pattern pattern=Pattern.compile("(\\d+)-(\\d+)=(\\d+)");
			try
			{
				InputStream resourceStream=Isbn.class.getResourceAsStream("IsbnGroup"+group+".properties");
				if (resourceStream!=null)
				{
					BufferedReader reader=new BufferedReader(new InputStreamReader(resourceStream));
					String line;
					while ((line=reader.readLine())!=null)
					{
						Matcher matcher=pattern.matcher(line);
						if (matcher.matches())
						{
							Range range=new Range(matcher.group(1), matcher.group(2), Integer.parseInt(matcher.group(3)));
							ranges.add(range);
						}
						else System.err.println("Invalid range "+line);
					}
					reader.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return ranges;
	}

	public static Isbn valueOf(String text) throws IsbnFormatException
	{
		if (StringUtils.isEmpty(text)) return null;
		String prefix=null;
		text=BookManager.filterIsbn(text);
		int offset=0;
		if (text.length()==13)
		{
			offset=3;
			prefix=text.substring(0, 3);
			if (!"978".equals(prefix) && !"979".equals(prefix)) throw new IsbnFormatException("Invalid prefix");
		}
		if (text.length()-offset!=10) throw new IsbnFormatException("Invalid length");

		String groupNumber=null;
		for (int i=1; i<=5; i++)
		{
			String gn=text.substring(offset, offset+i);
			if (getGroupNumbers().contains(gn))
			{
				groupNumber=gn;
				break;
			}
		}
		if (groupNumber==null) throw new IsbnFormatException("Invalid group number");
		offset=offset+groupNumber.length();

		String publisherNumber=null;
		for (Range range : getPublisherNumberRanges(groupNumber))
		{
			String pn=text.substring(offset, offset+range.start.length());
			if (range.start.compareTo(pn)<=0 && pn.compareTo(range.end)<=0)
			{
				publisherNumber=pn.substring(0, range.length);
				break;
			}
		}
		if (publisherNumber==null) throw new IsbnFormatException("Invalid publisher number");
		offset=offset+publisherNumber.length();

		String itemNumber=text.substring(offset, text.length()-1);
		String checkDigit=text.substring(text.length()-1).toUpperCase();
		String calculatedCheckDigit=prefix!=null
									? calculateIsbn13CheckDigit(prefix, groupNumber, publisherNumber, itemNumber)
									: calculateIsbn10CheckDigit(groupNumber, publisherNumber, itemNumber);
		if (!calculatedCheckDigit.equals(checkDigit)) throw new IsbnFormatException("Invalid check digit. Expected: "+calculatedCheckDigit+" was: "+checkDigit);

		return new Isbn(prefix, groupNumber, publisherNumber, itemNumber, checkDigit);
	}

	public Isbn getIsbn13()
	{
		if (prefix!=null) return this;
		String checkDigit=calculateIsbn13CheckDigit("978", groupNumber, publisherNumber, itemNumber);
		return new Isbn("978", groupNumber, publisherNumber, itemNumber, checkDigit);
	}

	public static String calculateIsbn13CheckDigit(String prefix, String groupNumber, String publisherNumber, String itemNumber)
	{
		String number=prefix+groupNumber+publisherNumber+itemNumber;
		int sum=0;
		for (int i=0; i<number.length(); i++)
		{
			int digit=Character.digit(number.charAt(i), 10);
			if (i%2==0) sum+=digit;
			else sum+=3*digit;
		}
		int checkDigit=(10-(sum%10))%10;
		return String.valueOf(checkDigit);
	}

	public Isbn getIsbn10()
	{
		if (prefix==null) return this;
		String checkDigit=calculateIsbn10CheckDigit(groupNumber, publisherNumber, itemNumber);
		return new Isbn(groupNumber, publisherNumber, itemNumber, checkDigit);
	}

	public static String calculateIsbn10CheckDigit(String groupNumber, String publisherNumber, String itemNumber)
	{
		String number=groupNumber+publisherNumber+itemNumber;
		int sum=0;
		for (int i=0; i<number.length(); i++)
		{
			int digit=Character.digit(number.charAt(i), 10);
			sum+=(i+1)*digit;
		}
		int checkDigit=sum%11;
		if (checkDigit==10) return "X";
		return String.valueOf(checkDigit);
	}

	private static class Range
	{
		private String start;
		private String end;
		private int length;

		private Range(String start, String end, int length)
		{
			this.start=start;
			this.end=end;
			this.length=length;
		}
	}

	public static String format(String text)
	{
		if (text==null) return null;
		try
		{
			Isbn isbn=Isbn.valueOf(text);
			return isbn.toString();
		}
		catch (IsbnFormatException e)
		{
			return text;
		}
	}

	public static String toIsbn13(String text)
	{
		if (text==null) return null;
		try
		{
			return Isbn.valueOf(text).getIsbn13().toString();
		}
		catch (IsbnFormatException e)
		{
			return null;
		}
	}

	public static boolean isValid(String text)
	{
		try
		{
			Isbn.valueOf(text);
			return true;
		}
		catch (IsbnFormatException e)
		{
			return false;
		}
	}
}
