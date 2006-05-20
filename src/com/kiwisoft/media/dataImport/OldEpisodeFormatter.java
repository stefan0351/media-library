/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 3:46:13 PM
 */
package com.kiwisoft.media.dataImport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Language;
import com.kiwisoft.utils.CountingMap;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.progress.ObservableRunnable;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.utils.xml.XMLWriter;

public class OldEpisodeFormatter implements ObservableRunnable
{
	private ProgressSupport progressSupport=new ProgressSupport(null);

	private Show show;
	private Language language;
	private String source;
	private String target;

	public OldEpisodeFormatter(Show show, Language language, String source, String target)
	{
		this.show=show;
		this.language=language;
		this.source=source;
		this.target=target;
	}

	public String getName()
	{
		return "Konvertiere Episoden...";
	}

	public void setProgressListener(ProgressListener progressListener)
	{
		progressSupport=new ProgressSupport(progressListener);
	}

	public void run()
	{
		try
		{
			progressSupport.step("Episoden laden...");
			File sourceFile=new File(source);
			if (sourceFile.exists() && sourceFile.isFile())
			{
				Set episodes=loadFile(sourceFile);
				progressSupport.step("Episoden konvertieren...");
				createEpisodes(episodes);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			progressSupport.error(e.getMessage());
		}
		finally
		{
			progressSupport.stopped();
		}
	}

	private void createEpisodes(Set episodes) throws IOException
	{
		progressSupport.step("Episoden erzeugen...");
		progressSupport.initialize(episodes.size());
		CountingMap fileNumbers=new CountingMap();
		Iterator it=episodes.iterator();
		int counter=0;
		String variant=null;
		if (language!=null && "de".equals(language.getSymbol())) variant=language.getSymbol();
		while (it.hasNext())
		{
			EpisodeInfo info=(EpisodeInfo)it.next();
			boolean credits=!info.writers.isEmpty() || !info.directors.isEmpty() || !info.cast.isEmpty()
					|| info.firstAired!=null || !info.recurringCast.isEmpty() || !info.story.isEmpty();
			if (info.content!=null || info.otitle!=null || credits)
			{
				File file;
				String path=show.getUserKey()+File.separator+info.number+File.separator+"info";
				path=path.replace('.', File.separatorChar);
				file=new File(target, path+".xp");
				while (file.exists())
				{
					file=new File(target, path+fileNumbers.increase(info.number)+".xp");
				}
				file.getParentFile().mkdirs();
				XMLWriter xmlWriter=new XMLWriter(new FileOutputStream(file), null);
				xmlWriter.setDoubleEncoding(true);
				xmlWriter.start();
				xmlWriter.startElement("episode");
				if (variant!=null) xmlWriter.setAttribute("variant", "de");
				xmlWriter.addElement("show", show.getName());
				xmlWriter.addElement("nr", info.number);
				xmlWriter.addElement("episode", info.title);
				if (info.otitle!=null) xmlWriter.addComment("Original Title: "+info.otitle);
				if (info.content!=null)
				{
					xmlWriter.startElement("content");
					String[] paragraphs=info.content.split("\n");
					for (int i=0; i<paragraphs.length; i++)
					{
						if (!StringUtils.isEmpty(paragraphs[i]))
						{
							xmlWriter.startElement("p");
							xmlWriter.setText(paragraphs[i].trim());
							xmlWriter.closeElement("p");
						}
					}
					xmlWriter.closeElement("content");
				}
				if (credits)
				{
					xmlWriter.startElement("credits");
					if (info.firstAired!=null) xmlWriter.addElement("firstAired", info.firstAired);
					for (Iterator itWriters=info.writers.iterator(); itWriters.hasNext();)
						xmlWriter.addElement("writer", ((String)itWriters.next()).trim());
					for (Iterator itWriters=info.story.iterator(); itWriters.hasNext();)
						xmlWriter.addElement("story", ((String)itWriters.next()).trim());
					for (Iterator itDirectors=info.directors.iterator(); itDirectors.hasNext();)
						xmlWriter.addElement("director", ((String)itDirectors.next()).trim());
					for (Iterator itCast=info.recurringCast.iterator(); itCast.hasNext();)
						xmlWriter.addElement("recurringCast", ((String)itCast.next()).trim());
					if (!info.cast.isEmpty())
					{
						xmlWriter.startElement("guestCast");
						for (Iterator itCast=info.cast.iterator(); itCast.hasNext();)
						{
							CastInfo castInfo=(CastInfo)itCast.next();
							xmlWriter.startElement("cast");
							if (castInfo.actor!=null)
							{
								xmlWriter.startElement("actor");
								xmlWriter.setText(castInfo.actor);
								xmlWriter.closeElement("actor");
							}
							if (castInfo.character!=null)
							{
								xmlWriter.startElement("character");
								xmlWriter.setText(castInfo.character.trim());
								xmlWriter.closeElement("character");
							}
							xmlWriter.closeElement("cast");
						}
						xmlWriter.closeElement("guestCast");
					}
					xmlWriter.closeElement("credits");
				}
				xmlWriter.closeElement("episode");
				xmlWriter.close();
				counter++;
			}
			progressSupport.progress(1, true);
		}
		progressSupport.message(counter+" Episoden erzeugt");
	}

	private Set loadFile(File file) throws IOException
	{
		Set episodes=new HashSet();
		String content=FileUtils.loadFile(file);
		int episodeStart=content.indexOf("<tr><td class=title2>");
		while (episodeStart>0)
		{
			int nextEpisodeStart=content.indexOf("<tr><td class=title2>", episodeStart+1);

			int nrEnd=content.indexOf("</td>", episodeStart);
			String episodeNr=XMLUtils.resolveEntities(XMLUtils.removeTags(content.substring(episodeStart, nrEnd)));

			int titleStart=nrEnd+5;
			int titleEnd=content.indexOf("</tr>", titleStart);
			String title=XMLUtils.resolveEntities(XMLUtils.removeTags(content.substring(titleStart, titleEnd)));

			EpisodeInfo episode=new EpisodeInfo();
			episode.number=episodeNr;
			episode.title=title;
			episodes.add(episode);

			int infoStart=content.indexOf("<td class=item>", titleEnd);
			while (infoStart>0 && (nextEpisodeStart<0 || infoStart<nextEpisodeStart))
			{
				int keyEnd=content.indexOf("</td>", infoStart);
				int infoEnd=content.indexOf("</tr>", keyEnd);

				String key=XMLUtils.resolveEntities(XMLUtils.removeTags(content.substring(infoStart, keyEnd)));

				String value=XMLUtils.resolveEntities(content.substring(keyEnd, infoEnd));
				value=StringUtils.replaceStrings(value, "</td>", "");
				value=StringUtils.replaceStrings(value, "<td>", "");

				if ("Originaltitel:".equalsIgnoreCase(key))
					episode.otitle=XMLUtils.removeTags(value);
				else if ("Buch:".equalsIgnoreCase(key) || "Written by:".equalsIgnoreCase(key))
					episode.setDirectors(value);
				else if ("Regie:".equalsIgnoreCase(key) || "Directed by:".equalsIgnoreCase(key))
					episode.setWriters(value);
				else if ("Inhalt:".equalsIgnoreCase(key) || "Content:".equalsIgnoreCase(key))
					episode.setContent(value);
				else if ("Darsteller:".equalsIgnoreCase(key) || "Guest Cast:".equalsIgnoreCase(key))
					episode.setCast(value);
				else if ("Bemerkung:".equalsIgnoreCase(key) || "Notes:".equalsIgnoreCase(key) || "Note:".equalsIgnoreCase(key))
					episode.notes.add(value);
				else if ("First Aired:".equalsIgnoreCase(key))
					episode.firstAired=value;
				else if ("Recurring Cast:".equalsIgnoreCase(key))
					episode.setRecurringCast(value);
				else if ("Story by:".equalsIgnoreCase(key) || "Story:".equalsIgnoreCase(key))
					episode.setStory(value);
				else if ("RTL:".equalsIgnoreCase(key))
				{
					if (episode.content==null) episode.setContent(value);
				}
				else
					progressSupport.warning("Unbekanntes Tag '"+key+"'");

				infoStart=content.indexOf("<td class=item>", infoEnd);
			}
			System.out.println("episode = "+episode);

			episodeStart=nextEpisodeStart;
		}
		return episodes;
	}

	private static class EpisodeInfo
	{
		private String number;
		private String title;
		private String otitle;
		private String content;
		private String firstAired;
		private Set directors=new HashSet();
		private Set writers=new HashSet();
		private Set story=new HashSet();
		private Set cast=new HashSet();
		private Set recurringCast=new HashSet();
		private Set notes=new HashSet();

		public String toString()
		{
			StringBuffer buffer=new StringBuffer("EpisodeInfo("+number+": \""+title+"\"");
			if (otitle!=null) buffer.append("\n\totitle=").append(otitle);
			if (firstAired!=null) buffer.append("\n\tfirst aired=").append(firstAired);
			if (!directors.isEmpty()) buffer.append("\n\tdirectors=").append(directors);
			if (!writers.isEmpty()) buffer.append("\n\twriters=").append(writers);
			if (!story.isEmpty()) buffer.append("\n\tstory=").append(story);
			if (!cast.isEmpty()) buffer.append("\n\tguest cast=").append(cast);
			if (!recurringCast.isEmpty()) buffer.append("\n\trecurring cast=").append(recurringCast);
			if (!notes.isEmpty()) buffer.append("\n\tnotes=").append(notes);
			buffer.append(")");
			return buffer.toString();
		}

		public void setDirectors(String value)
		{
			for (StringTokenizer tokens=new StringTokenizer(value, ",&;"); tokens.hasMoreTokens();)
			{
				directors.add(tokens.nextToken().trim());
			}
		}

		public void setRecurringCast(String value)
		{
			for (StringTokenizer tokens=new StringTokenizer(value, ",&;"); tokens.hasMoreTokens();)
			{
				recurringCast.add(tokens.nextToken().trim());
			}
		}

		public void setWriters(String value)
		{
			for (StringTokenizer tokens=new StringTokenizer(value, ",&;"); tokens.hasMoreTokens();)
			{
				writers.add(tokens.nextToken().trim());
			}
		}

		public void setStory(String value)
		{
			for (StringTokenizer tokens=new StringTokenizer(value, ",&;"); tokens.hasMoreTokens();)
			{
				story.add(tokens.nextToken().trim());
			}
		}

		public void setCast(String value)
		{
			value=XMLUtils.removeTags(value);
			String[] items=value.split(",");
			for (int i=0; i<items.length; i++)
			{
				CastInfo cast=new CastInfo();
				String item=XMLUtils.removeTags(items[i].trim());
				int start=item.indexOf("(");
				try
				{
					if (start>=0)
					{
						cast.actor=item.substring(0, start).trim();
						int end=item.indexOf(")");
						if (end>start)
							cast.character=item.substring(start+1, end).trim();
						else
							cast.character=item.substring(start+1).trim();
					}
					else
						cast.actor=item.trim();
				}
				catch (Exception e)
				{
					System.out.println("item = "+item);
					cast.actor=item.trim();
					throw new RuntimeException(e);
				}
				this.cast.add(cast);
			}

		}

		public void setContent(String value)
		{
			content=StringUtils.replaceStrings(value, "\n", "");
			content=StringUtils.replaceStrings(content, "\r", "");
			content=StringUtils.replaceStrings(content, "<p>", "\n");
			content=StringUtils.replaceStrings(content, "</p>", "");
		}
	}

	private static class CastInfo
	{
		private String actor;
		private String character;

		public String toString()
		{
			return actor+">>"+character;
		}
	}
}
