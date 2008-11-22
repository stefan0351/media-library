package com.kiwisoft.media.dataImport.cddb;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
 */
public class CDDBUtils
{
	private static final String HELLO_STRING="hello=stefan+localhost+MediaLib+2";

	private CDDBUtils()
	{
	}

	public static Map<String, List<DiscInfo>> getDiscInfos() throws IOException, InterruptedException
	{
		Map<String, String> cddbQueries=runDiscIdGenerator();
		Map<String, List<DiscInfo>> infos=new LinkedHashMap<String, List<DiscInfo>>();
		for (Map.Entry<String, String> entry : cddbQueries.entrySet())
		{
			List<DiscInfo> infoList=new ArrayList<DiscInfo>();
			String data=cddbCommand(entry.getValue());
			int returnCode=Integer.parseInt(data.substring(0, 3));
			data=data.substring(4);
			switch (returnCode)
			{
				case 200:
				{
					DiscInfo discInfo=parseSearchResult(data.trim());
					if (discInfo!=null) infoList.add(discInfo);
					break;
				}
				case 210:
					String[] lines=data.split("\n");
					for (int i=1; i<lines.length; i++)
					{
						String line=lines[i].trim();
						if (".".equals(line)) break;
						DiscInfo discInfo=parseSearchResult(line);
						if (discInfo!=null) infoList.add(discInfo);
					}
					break;
				default:
					System.err.println("Unknown returnCode: "+returnCode);
					System.err.println(data);
			}
			if (!infoList.isEmpty()) infos.put(entry.getKey(), infoList);
		}
		return infos;
	}

	private static String cddbCommand(String command) throws IOException
	{
		String url=MediaConfiguration.getCDDBUrl();
		if (StringUtils.isEmpty(url)) url="http://freedb.freedb.org/~cddb/cddb.cgi";
		return WebUtils.loadURL(url+"?cmd="+URLEncoder.encode(command, "UTF-8")+"&" +HELLO_STRING+"&proto=5");
	}

	private static DiscInfo parseSearchResult(String data)
	{
		Matcher matcher=Pattern.compile("([a-zA-Z]*) ([a-fA-F0-9]{8}) (.*)").matcher(data);
		if (matcher.matches())
		{
			return new DiscInfo(matcher.group(1), matcher.group(2), matcher.group(3));
		}
		else
		{
			System.err.println("Invalid data: "+data);
			return null;
		}
	}

	private static Map<String, String> runDiscIdGenerator() throws IOException, InterruptedException
	{
		String path=MediaConfiguration.getCDDBIdGeneratorPath();
		if (StringUtils.isEmpty(path)) path="bin"+File.separator+"cddbidgen.exe";
		StringBuilder output=new StringBuilder();
		Utils.run("cmd /c \""+path+"\"", output, null);
//		System.out.println(output);
		return parseDiscIdGeneratorOutput(new StringReader(output.toString()));
	}

	private static Map<String, String> parseDiscIdGeneratorOutput(Reader reader) throws IOException
	{
		BufferedReader logReader=new BufferedReader(reader);
		String line;
		String unit=null;
		Map<String, String> cddbQueries=new HashMap<String, String>();
		Pattern unitPattern=Pattern.compile("Unit \"([A-Z]):\\\\\".*");
		Pattern cddbQueryPattern=Pattern.compile("cddb query .*");
		while ((line=logReader.readLine())!=null)
		{
			line=line.trim();
			Matcher matcher=unitPattern.matcher(line);
			if (matcher.matches())
			{
				unit=matcher.group(1);
			}
			if (unit!=null && !cddbQueries.containsKey(unit))
			{
				matcher=cddbQueryPattern.matcher(line);
				if (matcher.matches())
				{
					cddbQueries.put(unit, matcher.group());
					unit=null;
				}
			}
		}
		logReader.close();
		return cddbQueries;
	}

	public static String getDiscDetails(DiscInfo discInfo) throws IOException
	{
		return cddbCommand("cddb read "+discInfo.getGenre()+" "+discInfo.getDiscId());
	}
}
