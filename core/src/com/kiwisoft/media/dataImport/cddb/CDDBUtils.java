package com.kiwisoft.media.dataImport.cddb;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.utils.WebUtils;

/**
 * @author Stefan Stiller
 */
public class CDDBUtils
{
	private CDDBUtils()
	{
	}

	public static Map<String, List<DiscInfo>> getDiscInfos() throws IOException, InterruptedException
	{
		File logFile=runDiscIdGenerator();
		Map<String, String> cddbQueries=parseLogFile(logFile);
		logFile.delete();
		Map<String, List<DiscInfo>> infos=new LinkedHashMap<String, List<DiscInfo>>();
		for (Map.Entry<String, String> entry : cddbQueries.entrySet())
		{
			List<DiscInfo> infoList=new ArrayList<DiscInfo>();
			String data=WebUtils.loadURL(
				"http://freedb.freedb.org/~cddb/cddb.cgi?cmd="+URLEncoder.encode(entry.getValue(), "UTF-8")+"&hello=stefan+localhost+MediaLib+2&proto=5");
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

	private static File runDiscIdGenerator() throws IOException, InterruptedException
	{
		File batchFile=new File("D:\\Downloads\\Software\\cddbidgen.bat");
		final File logFile=new File(batchFile.getParentFile(), "cddbidgen.log");
		logFile.delete();
		Runtime.getRuntime().exec("cmd /c start \"DISCID\" \""+batchFile.getAbsolutePath()+"\"", null, batchFile.getParentFile());
		Thread logFileWatcher=new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					while (true)
					{
						Thread.sleep(100);
						if (logFile.exists() && logFile.length()>0) break;
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

			}
		};
		logFileWatcher.start();
		logFileWatcher.join();
		return logFile;
	}

	private static Map<String, String> parseLogFile(File logFile) throws IOException
	{
		BufferedReader logReader=new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
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

	public static void getDiscDetails(DiscInfo discInfo) throws IOException
	{
		String data=WebUtils.loadURL(
			"http://freedb.freedb.org/~cddb/cddb.cgi?cmd=cddb+read+"+discInfo.getGenre()+"+"+discInfo.getDiscId()+"&hello=stefan+localhost+MediaLib+2&proto=5");
		System.out.println("data = "+data);
	}
}
