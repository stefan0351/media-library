package com.kiwisoft.media.download;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import com.kiwisoft.utils.WebUtils;

public class TextParser implements Parser
{
	@Override
	public void parse(File file, URL documentUrl, List<URL> contained, List<URL> linked) throws IOException
	{
		InputStream is=new FileInputStream(file);
		try
		{
			while (true)
			{
				String url=getNextURL(is);
				if (url!=null)
				{
					URL newURL=WebUtils.getValidURL(url);
					if (newURL!=null && !ExcludeFilter.getInstance().match(newURL.toString()))
						linked.add(new URL(url));
					else
						System.out.println("Malformed url = "+url);
				}
				else
					return;
			}
		}
		finally
		{
			is.close();
		}
	}

	public static String getNextURL(InputStream is)
	{
		String http="http://";
		int correct;
		StringBuilder url=new StringBuilder();
		try
		{
			int b;
			while (true)
			{
				correct=0;
				while ((b=is.read())!=-1)
				{
					if (http.charAt(correct)==(char)b)
					{
						url.append((char)b);
						correct++;
						if (correct==http.length()) break;
					}
					else
					{
						url.delete(0, url.length());
						correct=0;
					}
				}
				if (b==-1) return null;
				while ((b=is.read())!=-1)
				{
					if (b>32 && b<128 && "<>(){}[]".indexOf((char)b)==-1)
						url.append((char)b);
					else
						return url.toString();
				}
				return null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args)
	{
		try
		{
			File file=new File("d:\\temp\\Dokument.txt");
			List<URL> linked=new ArrayList<URL>();
			new TextParser().parse(file, null, null, linked);
			System.out.println("linked = "+linked);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
