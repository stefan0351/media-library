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
import java.util.LinkedHashSet;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.utils.CountingMap;
import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.progress.ObservableRunnable;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.*;

public class EpisodeFormatter implements ObservableRunnable
{
	private ProgressSupport progressSupport=new ProgressSupport(null);

	private String source;
	private String target;

	public EpisodeFormatter(String source, String target)
	{
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
			if (sourceFile.exists())
			{
				Set episodes=new HashSet();
				if (sourceFile.isFile())
					episodes=loadFiles(new File[]{sourceFile}, episodes);
				else
					episodes=loadFiles(sourceFile.listFiles(new RegularFileFilter("*.xml")), episodes);
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
		ShowManager showManager=ShowManager.getInstance();
		CountingMap fileNumbers=new CountingMap();
		Iterator it=episodes.iterator();
		int counter=0;
		while (it.hasNext())
		{
			EpisodeInformation info=(EpisodeInformation)it.next();
			Show show=showManager.getShowByName(info.getShow());
			if (show!=null)
			{
				Episode episode=null;
				if (!StringUtils.isEmpty(info.getEpisode())) episode=showManager.getEpisodeByName(show, info.getEpisode());
				String content=info.getContent();
				Set writers=info.getWriters();
				Set directors=info.getDirectors();
				Set cast=info.getCast();
				boolean credits=!writers.isEmpty() || !directors.isEmpty() || !cast.isEmpty();
				String originalTitle=info.getOriginalTitle();

				if (content!=null || originalTitle!=null || credits)
				{
					File file;
					do
					{
						file=new File(target, show.getUserKey()+File.separator+fileNumbers.increase(show)+".xp");
					}
					while (file.exists());
					file.getParentFile().mkdirs();
					XMLWriter xmlWriter=new XMLWriter(new FileOutputStream(file), null);
					xmlWriter.setDoubleEncoding(true);
					xmlWriter.start();
					xmlWriter.startElement("episode");
					xmlWriter.setAttribute("variant", "de");
					xmlWriter.addComment("Show: "+info.getShow());
					if (episode!=null) xmlWriter.addComment("Nr.: "+episode.getUserKey());
					if (info.getEpisode()!=null) xmlWriter.addComment("Episode: "+info.getEpisode());
					if (originalTitle!=null) xmlWriter.addComment("Original Title: "+originalTitle);
					if (content!=null)
					{
						xmlWriter.startElement("content");
						String[] paragraphs=content.trim().split("\n");
						for (int i=0; i<paragraphs.length; i++)
						{
							xmlWriter.startElement("p");
							xmlWriter.setText(paragraphs[i].trim());
							xmlWriter.closeElement("p");
						}
						xmlWriter.closeElement("content");
					}
					if (credits)
					{
						xmlWriter.startElement("credits");
						for (Iterator itWriters=writers.iterator(); itWriters.hasNext();)
						{
							String[] items=((String)itWriters.next()).split(",");
							for (int i=0; i<items.length; i++)
							{
								String item=items[i];
								xmlWriter.startElement("writer");
								xmlWriter.setText(item.trim());
								xmlWriter.closeElement("writer");
							}
						}
						for (Iterator itDirectors=directors.iterator(); itDirectors.hasNext();)
						{
							String[] items=((String)itDirectors.next()).split(",");
							for (int i=0; i<items.length; i++)
							{
								String item=items[i];
								xmlWriter.startElement("director");
								xmlWriter.setText(item.trim());
								xmlWriter.closeElement("director");
							}
						}
						if (!cast.isEmpty())
						{
							xmlWriter.startElement("guestCast");
							for (Iterator itCast=cast.iterator(); itCast.hasNext();)
							{
								CastInformation castInfo=(CastInformation)itCast.next();
								xmlWriter.startElement("cast");
								if (castInfo.getActor()!=null)
								{
									xmlWriter.startElement("actor");
									xmlWriter.setText(castInfo.getActor().trim());
									xmlWriter.closeElement("actor");
								}
								if (castInfo.getCharacter()!=null)
								{
									xmlWriter.startElement("character");
									xmlWriter.setText(castInfo.getCharacter().trim());
									xmlWriter.closeElement("character");
								}
								if (castInfo.getVoice()!=null)
								{
									xmlWriter.startElement("synchronVoice");
									xmlWriter.setText(castInfo.getVoice().trim());
									xmlWriter.closeElement("synchronVoice");
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
			}
			else
				progressSupport.warning("Keine Serie mit dem Titel '"+info.getShow()+"' gefunden. (Datei: "+info.getFile()+")");
			progressSupport.progress(1, true);
		}
		progressSupport.message(counter+" Episoden erzeugt");
	}

	private Set loadFiles(File[] files, Set episodes)
	{
		progressSupport.step("Lade Dateien...");
		progressSupport.initialize(files.length);
		for (int i=0; i<files.length; i++)
		{
			File file=files[i];
			if (file.isFile())
				loadFile(file, episodes);
			else loadFiles(file.listFiles(new RegularFileFilter("*.xml")), episodes);
			progressSupport.progress(1, true);
		}
		return episodes;
	}

	private void loadFile(File file, Set episodes)
	{
		XMLHandler xmlHandler=new XMLHandler();
		xmlHandler.addTagMapping("Details", EpisodeInformation.class);
		xmlHandler.addTagMapping("Darsteller", CastInformation.class);
		try
		{
			XMLObject root=xmlHandler.loadFile(file);
			if (root instanceof DefaultXMLObject)
			{
				DefaultXMLObject listing=(DefaultXMLObject)root;
				if ("Listing".equalsIgnoreCase(listing.getName()))
				{
					Iterator it=listing.getElements().iterator();
					while (it.hasNext())
					{
						XMLObject xmlObject=(XMLObject)it.next();
						if (xmlObject instanceof EpisodeInformation) episodes.add(xmlObject);
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("file="+file);
			throw new RuntimeException(e);
		}
	}

	public static class EpisodeInformation extends XMLAdapter
	{
		private String show;
		private String episode;
		private String content;
		private Set cast=new HashSet();
		private Set directors=new HashSet();
		private Set writers=new HashSet();
		private String originalTitle;
		private String file;
		private Set subTitles=new LinkedHashSet();

		public EpisodeInformation(XMLContext context, String name)
		{
			super(context, name);
			file=context.getFileName();
		}

		public String getFile()
		{
			return file;
		}

		public String getShow()
		{
			return show;
		}

		public String getEpisode()
		{
			if (subTitles.isEmpty()) return episode;
			else return episode+" ("+StringUtils.formatAsEnumeration(subTitles)+")";
		}

		public String getOriginalTitle()
		{
			return originalTitle;
		}

		public String getContent()
		{
			return content;
		}

		public Set getCast()
		{
			return cast;
		}

		public Set getDirectors()
		{
			return directors;
		}

		public Set getWriters()
		{
			return writers;
		}

		public void addXMLElement(XMLContext context, XMLObject element)
		{
			if (element instanceof CastInformation)
			{
				CastInformation castInfo=(CastInformation)element;
				if (castInfo.getCast()!=null)
					cast.addAll(castInfo.getCast());
				else
					cast.add(element);
			}
			else if (element instanceof DefaultXMLObject)
			{
				DefaultXMLObject xmlObject=(DefaultXMLObject)element;
				String name=xmlObject.getName();
				if ("Show".equalsIgnoreCase(name))
					show=xmlObject.getContent();
				else if ("Episode".equalsIgnoreCase(name))
					episode=xmlObject.getContent();
				else if ("Originaltitel".equalsIgnoreCase(name))
					originalTitle=xmlObject.getContent();
				else if ("Inhalt".equalsIgnoreCase(name))
					content=xmlObject.getContent();
				else if ("Drehbuch".equalsIgnoreCase(name))
					writers.add(xmlObject.getContent());
				else if ("Regie".equalsIgnoreCase(name))
					directors.add(xmlObject.getContent());
				else if ("Untertitel".equalsIgnoreCase(name))
					subTitles.add(xmlObject.getContent());
			}
		}

		public boolean equals(Object o)
		{
			if (this==o) return true;
			if (!(o instanceof EpisodeInformation)) return false;

			final EpisodeInformation episodeInformation=(EpisodeInformation)o;

			if (cast!=null ? !cast.equals(episodeInformation.cast) : episodeInformation.cast!=null) return false;
			if (content!=null ? !content.equals(episodeInformation.content) : episodeInformation.content!=null) return false;
			if (directors!=null ? !directors.equals(episodeInformation.directors) : episodeInformation.directors!=null) return false;
			if (episode!=null ? !episode.equals(episodeInformation.episode) : episodeInformation.episode!=null) return false;
			if (show!=null ? !show.equals(episodeInformation.show) : episodeInformation.show!=null) return false;
			if (writers!=null ? !writers.equals(episodeInformation.writers) : episodeInformation.writers!=null) return false;

			return true;
		}

		public int hashCode()
		{
			int result;
			result=(show!=null ? show.hashCode() : 0);
			result=29*result+(episode!=null ? episode.hashCode() : 0);
			result=29*result+(content!=null ? content.hashCode() : 0);
			result=29*result+(cast!=null ? cast.hashCode() : 0);
			result=29*result+(directors!=null ? directors.hashCode() : 0);
			result=29*result+(writers!=null ? writers.hashCode() : 0);
			return result;
		}
	}

	public static class CastInformation extends XMLAdapter
	{
		private String character;
		private String actor;
		private String voice;
		private Set cast;

		public CastInformation(String character, String actor)
		{
			this.character=character;
			this.actor=actor;
		}

		public CastInformation(XMLContext context, String name)
		{
			super(context, name);
		}

		public Set getCast()
		{
			return cast;
		}

		public String getCharacter()
		{
			return character;
		}

		public String getActor()
		{
			return actor;
		}

		public String getVoice()
		{
			return voice;
		}

		public void setXMLContent(XMLContext context, String value)
		{
			if (!StringUtils.isEmpty(value))
			{
				if (cast==null) cast=new HashSet();
				String[] items=value.split(",");
				for (int i=0; i<items.length; i++)
				{
					String item=items[i].trim();
					int start=item.indexOf("(");
					try
					{
						if (start>=0)
						{
							String actor=item.substring(0, start);
							int end=item.indexOf(")");
							String character;
							if (end>start)
								character=item.substring(start+1, end);
							else
								character=item.substring(start+1);
							cast.add(new CastInformation(character, actor));
						}
						else
						{
							cast.add(new CastInformation((String)null, item));
						}
					}
					catch (Exception e)
					{
						System.out.println("item = "+item);
						throw new RuntimeException(e);
					}
				}
			}
		}

		public void addXMLElement(XMLContext context, XMLObject element)
		{
			if (element instanceof DefaultXMLObject)
			{
				DefaultXMLObject xmlObject=(DefaultXMLObject)element;
				if ("Schauspieler".equalsIgnoreCase(xmlObject.getName()))
					actor=xmlObject.getContent();
				else if ("Charakter".equalsIgnoreCase(xmlObject.getName()))
					character=xmlObject.getContent();
				else if ("Stimme".equalsIgnoreCase(xmlObject.getName()))
					voice=xmlObject.getContent();
			}
		}

		public boolean equals(Object o)
		{
			if (this==o) return true;
			if (!(o instanceof CastInformation)) return false;

			final CastInformation castInformation=(CastInformation)o;

			if (actor!=null ? !actor.equals(castInformation.actor) : castInformation.actor!=null) return false;
			if (character!=null ? !character.equals(castInformation.character) : castInformation.character!=null) return false;
			if (voice!=null ? !voice.equals(castInformation.voice) : castInformation.voice!=null) return false;

			return true;
		}

		public int hashCode()
		{
			int result;
			result=(character!=null ? character.hashCode() : 0);
			result=29*result+(actor!=null ? actor.hashCode() : 0);
			result=29*result+(voice!=null ? voice.hashCode() : 0);
			return result;
		}
	}
}
