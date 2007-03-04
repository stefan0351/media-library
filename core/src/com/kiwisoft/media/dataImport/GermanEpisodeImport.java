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
import java.util.*;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.StringUtils;
import static com.kiwisoft.utils.StringUtils.isEmpty;
import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.*;

public abstract class GermanEpisodeImport implements Job
{
	private ProgressSupport progressSupport;

	private String source;

	protected GermanEpisodeImport(String source)
	{
		this.source=source;
	}

	public String getName()
	{
		return "Importiere Episoden...";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progressSupport=new ProgressSupport(this, progressListener);
		progressSupport.startStep("Episoden laden...");
		File sourceFile=new File(source);
		if (sourceFile.exists())
		{
			Set episodes=new HashSet();
			if (sourceFile.isFile())
				episodes=loadFiles(new File[]{sourceFile}, episodes);
			else
				episodes=loadFiles(sourceFile.listFiles(new RegularFileFilter("*.xml")), episodes);
			progressSupport.startStep("Episoden konvertieren...");
			createEpisodes(episodes);
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}

	private void createEpisodes(Set episodes) throws IOException
	{
		progressSupport.startStep("Episoden erzeugen...");
		progressSupport.initialize(true, episodes.size(), null);
		String rootPath=Configurator.getInstance().getString("path.root");
		Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
		ShowManager showManager=ShowManager.getInstance();
		Iterator it=episodes.iterator();
		int counter=0;
		while (it.hasNext())
		{
			EpisodeXMLAdapter info=(EpisodeXMLAdapter)it.next();
			String originalTitle=info.getOriginalEpisodeTitle();
			if (!isEmpty(info.getEpisode()) && !isEmpty(info.getShow())
				&& (!isEmpty(info.getContent()) || !isEmpty(originalTitle) || info.isCreditsAvailable()))
			{
				Show show=showManager.getShowByName(info.getShow());
				if (show!=null)
				{
					Episode episode=showManager.getEpisodeByName(show, info.getEpisode());
					if (episode==null)
					{
						progressSupport.warning("Keine Episode mit dem Titel '"+info.getEpisode()+"' gefunden. (Datei: "+info.getFile()+")");
						episode=createEpisode(show, info);
					}
//					if (episode!=null)
//					{
//						EpisodeInfo episodeInfo=DBLoader.getInstance().load(EpisodeInfo.class, null,
//																	 "episode_id=? and name='Beschreibung (deutsch)'", episode.getId());
//						if (episodeInfo==null)
//						{
//							String path="shows/"+show.getUserKey()+"/episodes/"+episode.getUserKey().replace(".", "/")+"/info_de.xp";
//							File file=new File(rootPath, path);
//							if (!file.exists())
//							{
//								file.getParentFile().mkdirs();
//								createInfoFile(episode, info, file);
//								Transaction transaction=null;
//								try
//								{
//									transaction=DBSession.getInstance().createTransaction();
//									episodeInfo=episode.createInfo();
//									episodeInfo.setLanguage(german);
//									episodeInfo.setName("Beschreibung (deutsch)");
//									episodeInfo.setPath(path);
//									episode.setDefaultInfo(episodeInfo);
//									transaction.close();
//								}
//								catch (Throwable t)
//								{
//									t.printStackTrace();
//									progressSupport.error(t.getMessage());
//									try
//									{
//										if (transaction!=null) transaction.rollback();
//									}
//									catch (SQLException e)
//									{
//										e.printStackTrace();
//									}
//									return;
//								}
//
//								counter++;
//							}
//							else progressSupport.warning("File '"+file.getAbsolutePath()+"' isn't connected.");
//						}
//					}
				}
			}
			else progressSupport.warning("Keine Serie mit dem Titel '"+info.getShow()+"' gefunden. (Datei: "+info.getFile()+")");
			progressSupport.progress(1, true);
		}
		progressSupport.info(counter+" Episoden erzeugt");
	}

	protected abstract Episode createEpisode(Show show, ImportEpisode info);

	private void createInfoFile(Episode episode, EpisodeXMLAdapter info, File file
	)
		throws IOException
	{
		file.getParentFile().mkdirs();
		XMLWriter xmlWriter=new XMLWriter(new FileOutputStream(file), null);
		xmlWriter.setDoubleEncoding(true);
		xmlWriter.start();
		xmlWriter.startElement("episode");
		xmlWriter.setAttribute("variant", "de");
		xmlWriter.addComment("Show: "+info.getShow());
		if (episode!=null) xmlWriter.addComment("Nr.: "+episode.getUserKey());
		if (info.getEpisode()!=null) xmlWriter.addComment("Episode: "+info.getEpisode());
		if (info.getOriginalEpisodeTitle()!=null) xmlWriter.addComment("Original Title: "+info.getOriginalEpisodeTitle());
		if (info.getContent()!=null)
		{
			xmlWriter.startElement("content");
			String[] paragraphs=info.getContent().trim().split("\n");
			for (int i=0; i<paragraphs.length; i++)
			{
				xmlWriter.startElement("p");
				xmlWriter.setText(paragraphs[i].trim());
				xmlWriter.closeElement("p");
			}
			xmlWriter.closeElement("content");
		}
		if (info.isCreditsAvailable())
		{
			xmlWriter.startElement("credits");
			for (Iterator itWriters=info.getWriters().iterator(); itWriters.hasNext();)
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
			for (Iterator itDirectors=info.getDirectors().iterator(); itDirectors.hasNext();)
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
			if (!info.getCast().isEmpty())
			{
				xmlWriter.startElement("guestCast");
				for (Iterator itCast=info.getCast().iterator(); itCast.hasNext();)
				{
					CastXMLAdapter castInfo=(CastXMLAdapter)itCast.next();
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
	}

	private Set loadFiles(File[] files, Set episodes)
	{
		progressSupport.startStep("Lade Dateien...");
		progressSupport.initialize(true, files.length, null);
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
		xmlHandler.addTagMapping("Details", EpisodeXMLAdapter.class);
		xmlHandler.addTagMapping("Darsteller", CastXMLAdapter.class);
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
						if (xmlObject instanceof EpisodeXMLAdapter) episodes.add(xmlObject);
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

	public static class CastXMLAdapter extends XMLAdapter
	{
		private String character;
		private String actor;
		private String voice;
		private Set cast;

		public CastXMLAdapter(String character, String actor)
		{
			this.character=character;
			this.actor=actor;
		}

		public CastXMLAdapter(XMLContext context, String name)
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
			if (!isEmpty(value))
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
							cast.add(new CastXMLAdapter(character, actor));
						}
						else
						{
							cast.add(new CastXMLAdapter((String)null, item));
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
			if (!(o instanceof CastXMLAdapter)) return false;

			final CastXMLAdapter castInformation=(CastXMLAdapter)o;

			if (actor!=null ? !actor.equals(castInformation.actor) : castInformation.actor!=null) return false;
			if (character!=null ? !character.equals(castInformation.character) : castInformation.character!=null) return false;
			return !(voice!=null ? !voice.equals(castInformation.voice) : castInformation.voice!=null);
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

	public class EpisodeXMLAdapter extends XMLAdapter implements ImportEpisode
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

		public EpisodeXMLAdapter(XMLContext context, String name)
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

		public String getEpisodeKey()
		{
			return null;
		}

		public String getEpisodeTitle()
		{
			return episode;
		}

		public String getOriginalEpisodeTitle()
		{
			return originalTitle;
		}

		public Date getFirstAirdate()
		{
			return null;
		}

		public String getProductionCode()
		{
			return null;
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

		public boolean isCreditsAvailable()
		{
			return !writers.isEmpty() || !directors.isEmpty() || !cast.isEmpty();
		}

		public void addXMLElement(XMLContext context, XMLObject element)
		{
			if (element instanceof GermanEpisodeImport.CastXMLAdapter)
			{
				GermanEpisodeImport.CastXMLAdapter castInfo=(GermanEpisodeImport.CastXMLAdapter)element;
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
			if (!(o instanceof EpisodeXMLAdapter)) return false;

			final EpisodeXMLAdapter episodeInformation=(EpisodeXMLAdapter)o;

			if (cast!=null ? !cast.equals(episodeInformation.cast) : episodeInformation.cast!=null) return false;
			if (content!=null ? !content.equals(episodeInformation.content) : episodeInformation.content!=null) return false;
			if (directors!=null ? !directors.equals(episodeInformation.directors) : episodeInformation.directors!=null) return false;
			if (episode!=null ? !episode.equals(episodeInformation.episode) : episodeInformation.episode!=null) return false;
			if (show!=null ? !show.equals(episodeInformation.show) : episodeInformation.show!=null) return false;
			return !(writers!=null ? !writers.equals(episodeInformation.writers) : episodeInformation.writers!=null);
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
}
