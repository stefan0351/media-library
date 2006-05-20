package com.kiwisoft.media.dataImport;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.net.URLEncoder;

import com.kiwisoft.media.SearchPattern;
import com.kiwisoft.media.SearchManager;
import com.kiwisoft.media.Person;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.gui.progress.ObservableRunnable;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;

public class PrismaNetWorldInfoLoader implements ObservableRunnable
{
	private static final String MAIN_URL="http://www.prisma-online.de";

	private String path;
	private Set objects;
	private ProgressSupport progressSupport=new ProgressSupport(null);

	public PrismaNetWorldInfoLoader(String path, Set objects)
	{
		this.path=path;
		this.objects=objects;
	}

	public String getName()
	{
		return "Lade Prisma-Online Termine";
	}

	public void setProgressListener(ProgressListener progressListener)
	{
		progressSupport=new ProgressSupport(progressListener);
	}

	public void run()
	{
		try
		{
			progressSupport.step("Lade Suchmuster...");
			if (objects==null)
			{
				Collection patterns=SearchManager.getInstance().getSearchPatterns(SearchPattern.PRISMA_ONLINE, Show.class);
				Iterator it=patterns.iterator();
				progressSupport.initialize(patterns.size());
				progressSupport.step("Lade Termine...");
				while (it.hasNext() && !progressSupport.isStopped())
				{
					SearchPattern pattern=(SearchPattern)it.next();
					Show show=pattern.getShow();
					if (show!=null) loadShowDates(show, pattern.getPattern());
					progressSupport.progress(1, true);
				}
			}
			else
			{
				Iterator it=objects.iterator();
				progressSupport.initialize(objects.size());
				progressSupport.step("Lade Termine...");
				while (it.hasNext() && !progressSupport.isStopped())
				{
					Object object=it.next();
					if (object instanceof Show)
					{
						Show show=(Show)object;
						String pattern=show.getSearchPattern(SearchPattern.PRISMA_ONLINE);
						loadShowDates(show, pattern);
					}
					else if (object instanceof Person)
					{
						Person person=(Person)object;
						String pattern=person.getSearchPattern(SearchPattern.PRISMA_ONLINE);
						loadPersonDates(person, pattern);
					}
					else
						progressSupport.warning("Unhandled object class "+object.getClass());
					progressSupport.progress(1, true);
				}
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			progressSupport.error(t.getMessage());
		}
		finally
		{
			progressSupport.stopped();
		}
	}

	private void loadPersonDates(Person person, String pattern)
	{
		if (!StringUtils.isEmpty(pattern))
		{
			int tries=0;
			boolean loaded=false;
			while (tries<10)
			{
				try
				{
					String listing=WebUtils.loadURL(MAIN_URL+"/tv/pzpsuche.html?"+pattern);

					String fileName=URLEncoder.encode(person.getName(), "UTF-8");
					int number=2;
					File file=new File(path+File.separator+fileName+".html");
					while (file.exists()) file=new File(path+File.separator+fileName+"."+(number++)+".html");

					FileWriter fw=new FileWriter(file);
					fw.write(listing);
					fw.close();

					parsePersonListing(listing);

					loaded=true;
					break;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					tries++;
				}
			}
			if (loaded)
				progressSupport.message("Termine für "+person.getName()+" geladen.");
			else
				progressSupport.error("Termine für "+person.getName()+" nicht geladen.");
		}
	}

	private void loadShowDates(Show show, String pattern)
	{
		if (!StringUtils.isEmpty(pattern))
		{
			int tries=0;
			boolean loaded=false;
			while (tries<10)
			{
				try
				{
					String listing=WebUtils.loadURL("http://www.prisma-online.de/tv/pzpsuche.html?"+pattern);
					parseListing(show.getName(), listing);
					loaded=true;
					break;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					tries++;
				}
			}
			if (loaded)
				progressSupport.message("Termine für "+show.getName()+" geladen.");
			else
				progressSupport.error("Termine für "+show.getName()+" nicht geladen.");
		}
		else
			progressSupport.warning("Serie für Muster '"+pattern+"' existiert nicht mehr.");
	}

	private void parseListing(String name, String buffer)
	{
		try
		{
			SimpleDateFormat format1=new SimpleDateFormat("EEE, d. MMM yyyy");
			SimpleDateFormat format3=new SimpleDateFormat("H.mm");

			int number=2;
			File file=new File(path+File.separator+name+".xml");
			while (file.exists()) file=new File(path+File.separator+name+"."+(number++)+".xml");
			FileWriter fw=new FileWriter(file);

			String lb=System.getProperty("line.separator");
			fw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>"+lb);
			fw.write("<Listing>"+lb);

			int pos=buffer.indexOf("<h4>");
			Calendar date=Calendar.getInstance();
			Calendar time=Calendar.getInstance();
			while (pos>=0)
			{
				int end=buffer.indexOf("</h4>", pos);
				if (end<0) break;
				String sdate=XMLUtils.resolveEntities(buffer.substring(pos+4, end));
				char auml='ä';
				for (int i=0; i<sdate.length(); i++)
				{
					if ((int)sdate.charAt(i)==65508) auml=sdate.charAt(i);
				}
				sdate=StringUtils.replaceStrings(sdate, "M"+auml+"rz", "März");
				date.setTime(format1.parse(sdate));
				int nextPos=buffer.indexOf("<h4>", pos+1);
				int dl=buffer.indexOf("<dl>", pos);
				while (dl>=0 && ((nextPos>=0 && dl<nextPos) || nextPos<0))
				{
					int item0=buffer.indexOf("<b class=\"hot\">", dl)+15;
					int item1=buffer.indexOf("-", item0);
					String stime=buffer.substring(item0, item1);
					stime=StringUtils.replaceStrings(stime, "M"+auml+"rz", "Mrz");
					time.setTime(format3.parse(stime));
					date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
					date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));

					item0=buffer.indexOf("</b>&nbsp;", item1)+10;
					item1=buffer.indexOf("</dt>", item0);
					String channel=XMLUtils.resolveEntities(XMLUtils.removeTags(buffer.substring(item0, item1))).trim();
					if (channel.endsWith(" (Pay TV)")) channel=channel.substring(0, channel.length()-9);
					if ("Comedy & Co.".equals(channel)) channel="Comedy";

					item0=buffer.indexOf("<dd>", item1)+4;
					item1=buffer.indexOf("<br>", item0);
					String sshow=XMLUtils.resolveEntities(XMLUtils.removeTags(buffer.substring(item0, item1))).trim();

					item0=item1+4;
					item1=buffer.indexOf("<br>", item0);
					end=buffer.indexOf("</dd>", item0);
					String episode=null;
					if (item1>=0 && item1<end) episode=XMLUtils.resolveEntities(XMLUtils.removeTags(buffer.substring(item0, item1))).trim();

					fw.write("<Details>");
					fw.write("<DataSource>");
					fw.write(DataSource.PRISMA_ONLINE.getKey());
					fw.write("</DataSource>"+lb);
					fw.write("<Sendetermin>");
					fw.write("<Sender>"+XMLUtils.toXMLString(channel)+"</Sender>");
					fw.write("<Datum>"+ImportConstants.DATE_FORMAT.format(date.getTime())+"</Datum>");
					fw.write("</Sendetermin>"+lb);
					fw.write("<Show>"+XMLUtils.toXMLString(sshow)+"</Show>"+lb);
					if (episode!=null) fw.write("<Episode>"+XMLUtils.toXMLString(episode)+"</Episode>"+lb);
					fw.write("</Details>"+lb);

					dl=buffer.indexOf("<dl>", dl+1);
				}
				pos=nextPos;
			}
			fw.write("</Listing>"+lb);
			fw.flush();
			fw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			progressSupport.error(e.getMessage());
		}
	}

	private void parsePersonListing(String buffer)
	{
		try
		{
			String pattern="<a href=\"/tv/sendung.html?cid";
			int pos=buffer.indexOf(pattern);
			while (pos>=0)
			{
				int end=buffer.indexOf("</a>", pos);
				String tag=buffer.substring(pos, end+1);
				String showName=XMLUtils.removeTags(tag);
				String href=XMLUtils.getAttribute(tag, "href");

				String details=WebUtils.loadURL(MAIN_URL+href);

				String fileName=URLEncoder.encode(showName, "UTF-8");
				File file=new File(path+File.separator+fileName+".html");
				FileWriter fw=new FileWriter(file);
				fw.write(details);
				fw.close();

				pos=buffer.indexOf(pattern, end);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			progressSupport.error(e.getMessage());
		}
	}
}
