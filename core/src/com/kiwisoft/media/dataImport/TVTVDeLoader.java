package com.kiwisoft.media.dataImport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;

public class TVTVDeLoader implements Job
{
	public static final String BASE_URL="http://www.tvtv.de";

	public final SimpleDateFormat dateFormat;
	private String path;
	private List objects;
	private ProgressSupport progressSupport;
	private Set parsed;
	private Set loaded;

	public TVTVDeLoader(String path, List objects)
	{
		this.path=path;
		this.objects=objects;
		parsed=new HashSet();
		loaded=new HashSet();
		dateFormat=new SimpleDateFormat("dd. MMM HH.mm", Locale.GERMAN);
		dateFormat.setTimeZone(DateUtils.GMT);
	}

	public String getName()
	{
		return "Load Schedule from TVTV.de";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progressSupport=new ProgressSupport(this, progressListener);
		progressSupport.startStep("Load main page...");

		// Load index page
		String content=WebUtils.loadURL(BASE_URL);
		XMLUtils.Tag tag=XMLUtils.getNextTag(content, 0, "FRAMESET");
		tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
		tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
		String url=BASE_URL+XMLUtils.getAttribute(tag.text, "src");

		// Load main frame
		content=WebUtils.loadURL(url);
		tag=XMLUtils.getNextTag(content, 0, "FRAMESET");
		tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
		url=BASE_URL+XMLUtils.getAttribute(tag.text, "src");

		// Load nav frame
		content=WebUtils.loadURL(url);
		tag=XMLUtils.getNextTag(content, 0, "FRAMESET");
		tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
		tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
		tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
		url=BASE_URL+XMLUtils.getAttribute(tag.text, "src");

		// Load nav bottom
		content=WebUtils.loadURL(url);
		tag=XMLUtils.getNextTag(content, 0, "FORM");
		String searchUrl=BASE_URL+XMLUtils.getAttribute(tag.text, "action")+"?2.1=";

		// Load dates main frame
		progressSupport.startStep("Load search patterns...");
		if (objects==null)
		{
			Collection patterns=SearchManager.getInstance().getSearchPatterns(SearchPattern.TVTV, Show.class);

			Iterator it=patterns.iterator();
			progressSupport.startStep("Load schedule...");
			progressSupport.initialize(true, patterns.size(), null);
			while (it.hasNext() && !progressSupport.isStoppedByUser())
			{
				SearchPattern pattern=(SearchPattern)it.next();
				Show show=pattern.getShow();
				if (show!=null) loadShowDates(show, searchUrl, pattern.getPattern());
				progressSupport.progress(1, true);
			}
		}
		else
		{
			Iterator it=objects.iterator();
			progressSupport.initialize(true, objects.size(), null);
			progressSupport.startStep("Load schedule...");
			while (it.hasNext() && !progressSupport.isStoppedByUser())
			{
				Object object=it.next();
				if (object instanceof Show)
				{
					Show show=(Show)object;
					String pattern=show.getSearchPattern(SearchPattern.TVTV);
					loadShowDates(show, searchUrl, pattern);
				}
				else if (object instanceof Person)
				{
					Person person=(Person)object;
					String pattern=person.getSearchPattern(SearchPattern.TVTV);
					loadPersonDates(person, searchUrl, pattern);
				}
				else
					progressSupport.warning("Unhandled object class "+object.getClass());
				progressSupport.progress(1, true);
			}
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}

	private void loadShowDates(Show show, String searchUrl, String patternString)
	{
		progressSupport.startStep("Load schedule for "+show.getTitle()+"...");
		String content=null;
		try
		{
			content=WebUtils.loadURL(searchUrl+patternString);
			XMLUtils.Tag tag=XMLUtils.getNextTag(content, 0, "FRAMESET");
			tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
			tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
			String url=BASE_URL+XMLUtils.getAttribute(tag.text, "src");

			// Load dates list frame
			content=WebUtils.loadURL(url);
//			FileUtils.saveToFile(content, new File(path, show.getUserKey()+".html"));
			parseListing(content);
			progressSupport.info("Loaded schedule for "+show.getTitle()+".");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("content = "+content);
			progressSupport.error("Loading of schedule for "+show.getTitle()+" failed.");
		}
	}

	private void loadPersonDates(Person person, String searchUrl, String patternString) throws IOException
	{
		if (!StringUtils.isEmpty(patternString))
		{
			progressSupport.startStep("Load schedule for "+person.getName()+"...");
			String content=WebUtils.loadURL(searchUrl+patternString);
			XMLUtils.Tag tag=XMLUtils.getNextTag(content, 0, "FRAMESET");
			tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
			tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
			String url=BASE_URL+XMLUtils.getAttribute(tag.text, "src");

			// Load dates list frame
			content=WebUtils.loadURL(url);

			String fileName=URLEncoder.encode(person.getName(), "UTF-8");
			int number=2;
			File file=new File(path+File.separator+fileName+".html");
			while (file.exists()) file=new File(path+File.separator+fileName+"."+(number++)+".html");

			FileWriter fw=new FileWriter(file);
			fw.write(content);
			fw.close();

			parsePersonListing(content);

			progressSupport.info("Loaded schedule for "+person.getName()+".");
		}
	}

	private void parseListing(String listing) throws IOException
	{
		XMLUtils.Tag tag=XMLUtils.getNextTag(listing, 0, "FORM");
		String infoUrl=BASE_URL+XMLUtils.getAttribute(tag.text, "action")+"?sendung=";

		int index=tag.end;

		while (index>=0)
		{
			index=listing.indexOf("<SPAN class=\"pititle-anker1\"", index);
			if (index<0) break;
			tag=XMLUtils.getNextTag(listing, index, "a");
			index=tag.end;
			String ref=XMLUtils.getAttribute(tag.text, "href");
			String id=StringUtils.getTextBetween(ref, "(", ")");

			// Load dates list frame
			if (!parsed.contains(id))
			{
				// Load details
				String content=WebUtils.loadURL(infoUrl+id);
//				FileUtils.saveToFile(content, new File(path, "frame"+id+".html"));
				tag=XMLUtils.getNextTag(content, 0, "FRAMESET");
				tag=XMLUtils.getNextTag(content, tag.end, "FRAMESET");
				tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
				tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
				tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
				String url=BASE_URL+XMLUtils.getAttribute(tag.text, "src");

				content=WebUtils.loadURL(url);
				FileUtils.saveToFile(content, new File(path+File.separator+(id+".html")));
				parsed.add(id);
				try
				{
					parseDetails(content, id);
				}
				catch (Exception e)
				{
					progressSupport.error("Error parsing details: "+id+" ("+e.getMessage()+")");
				}
			}
		}
	}

	private void parseDetails(String content, String id) throws IOException
	{
		int index=content.indexOf("id=\"detail-box-station\"");
		index=content.indexOf(">", index);
		int index2=content.indexOf("</td>", index);
		String channel=content.substring(index+1, index2);
		channel=XMLUtils.unescapeHtml(XMLUtils.removeTags(channel)).trim();

		index=content.indexOf("id=\"detail-box-time\"", index);
		index=content.indexOf(">", index);
		index2=content.indexOf("</td>", index);
		String timeString=content.substring(index+1, index2);
		timeString=XMLUtils.unescapeHtml(XMLUtils.removeTags(timeString)).trim();

		index=content.indexOf("id=\"fb-b10\"", index);
		index=content.indexOf(">", index);
		index2=content.indexOf("</td>", index);
		String dateString=content.substring(index+1, index2);
		dateString=XMLUtils.unescapeHtml(XMLUtils.removeTags(dateString)).trim();

		Date date=null;
		try
		{
			Calendar now=Calendar.getInstance();
			now.setTimeZone(DateUtils.GMT);

			Calendar calendar=Calendar.getInstance();
			calendar.setTimeZone(DateUtils.GMT);
			calendar.setTime(dateFormat.parse(dateString+" "+timeString));
			calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
			if (now.after(calendar)) calendar.add(Calendar.YEAR, 1);
			date=calendar.getTime();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		index=content.indexOf("id=\"fb-b15\"", index);
		index=content.indexOf(">", index);
		index2=content.indexOf("</span>", index);
		String showName=content.substring(index+1, index2);
		showName=XMLUtils.unescapeHtml(XMLUtils.removeTags(showName)).trim();
		int lastIndex=index;

		index=content.indexOf("id=\"fb-b9\"", lastIndex);
		String title=null;
		String otitle=null;
		if (index>=lastIndex)
		{
			index=content.indexOf(">", index);
			index2=content.indexOf("</span>", index);
			title=content.substring(index+1, index2);
			title=XMLUtils.unescapeHtml(XMLUtils.removeTags(title)).trim();
			if (title.endsWith("\""))
			{
				title=StringUtils.getTextBetween(title, "\"", "\"");
				if (title.endsWith(")"))
				{
					otitle=StringUtils.getTextBetween(title, "(", ")");
					title=title.substring(0, title.indexOf("(")).trim();
				}
			}
			lastIndex=index;
		}

		index=content.indexOf("id=\"fn-b10\"", lastIndex);
		String description=null;
		if (index>=lastIndex)
		{
			index=content.indexOf(">", index);
			index2=content.indexOf("</span>", index);
			description=content.substring(index+1, index2);
			description=XMLUtils.unescapeHtml(description).trim();
			description=StringUtils.replaceStrings(description, "<br>\n<br>\n", "\n");
			description=StringUtils.replaceStrings(description, "<br>\n", " ");
			lastIndex=index;
		}

		index=content.indexOf(">Darsteller:<", lastIndex);
		String cast=null;
		if (index>=lastIndex)
		{
			index=content.indexOf("id=\"fn-b8\"", index);
			index=content.indexOf(">", index);
			index2=content.indexOf("</span>", index);
			cast=content.substring(index+1, index2);
			cast=XMLUtils.unescapeHtml(cast).trim();
			lastIndex=index;
		}

		index=content.indexOf(">Buch:<", lastIndex);
		String writer=null;
		if (index>=lastIndex)
		{
			index=content.indexOf("id=\"fn-b8\"", index);
			index=content.indexOf(">", index);
			index2=content.indexOf("</span>", index);
			writer=content.substring(index+1, index2);
			writer=XMLUtils.unescapeHtml(writer).trim();
			lastIndex=index;
		}

		index=content.indexOf(">Regie:<", lastIndex);
		String director=null;
		if (index>=lastIndex)
		{
			index=content.indexOf("id=\"fn-b8\"", index);
			index=content.indexOf(">", index);
			index2=content.indexOf("</span>", index);
			director=content.substring(index+1, index2);
			director=XMLUtils.unescapeHtml(director).trim();
		}

		String fileName=path+File.separator+URLEncoder.encode(showName, "UTF-8");
		File file=new File(fileName+".xml");
		int i=1;
		while (file.exists()) file=new File(fileName+"_"+(i++)+".xml");

		FileWriter fw=new FileWriter(file);
		fw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n");
		fw.write("<!-- "+id+"-->\n");
		fw.write("<Listing>\n");
		fw.write("<Details>\n");
		fw.write("<DataSource>");
		fw.write(DataSource.TVTV.getKey());
		fw.write("</DataSource>\n");
		fw.write("<Sendetermin><Sender>"+XMLUtils.toXMLString(channel)+"</Sender><Datum>"+ImportUtils.DATE_FORMAT.format(date)+"</Datum></Sendetermin>\n");
		fw.write("<Show>"+XMLUtils.toXMLString(showName)+"</Show>\n");
		if (!StringUtils.isEmpty(title)) fw.write("<Episode>"+XMLUtils.toXMLString(title)+"</Episode>\n");
		if (!StringUtils.isEmpty(otitle)) fw.write("<Originaltitel>"+XMLUtils.toXMLString(otitle)+"</Originaltitel>\n");
		if (!StringUtils.isEmpty(director)) fw.write("<Regie>"+XMLUtils.toXMLString(director)+"</Regie>\n");
		if (!StringUtils.isEmpty(writer)) fw.write("<Drehbuch>"+XMLUtils.toXMLString(writer)+"</Drehbuch>\n");
		if (!StringUtils.isEmpty(cast)) fw.write("<Darsteller>"+XMLUtils.toXMLString(cast)+"</Darsteller>\n");
		if (!StringUtils.isEmpty(description)) fw.write("<Inhalt>"+XMLUtils.toXMLString(description)+"</Inhalt>\n");
		fw.write("</Details>\n");
		fw.write("</Listing>\n");
		fw.close();
	}

	private void parsePersonListing(String listing) throws IOException
	{
		XMLUtils.Tag tag=XMLUtils.getNextTag(listing, 0, "FORM");
		String infoUrl=BASE_URL+XMLUtils.getAttribute(tag.text, "action")+"?sendung=";

		int index=tag.end;

		while (index>=0)
		{
			index=listing.indexOf("<SPAN class=\"pititle-anker1\"", index);
			if (index<0) break;
			tag=XMLUtils.getNextTag(listing, index, "a");
			index=tag.end;
			String ref=XMLUtils.getAttribute(tag.text, "href");
			String id=StringUtils.getTextBetween(ref, "(", ")");

			// Load dates list frame
			if (!loaded.contains(id))
			{
				// Load details
				String content=WebUtils.loadURL(infoUrl+id);
				tag=XMLUtils.getNextTag(content, 0, "FRAMESET");
				tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
				tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
				tag=XMLUtils.getNextTag(content, tag.end, "FRAME");
				String url=BASE_URL+XMLUtils.getAttribute(tag.text, "src");

				content=WebUtils.loadURL(url);
				FileUtils.saveToFile(content, new File(path+File.separator+(id+".html")));
				loaded.add(id);
			}
		}
	}
}
