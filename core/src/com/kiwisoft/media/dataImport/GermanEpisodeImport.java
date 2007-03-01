/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 3:46:13 PM
 */
package com.kiwisoft.media.dataImport;

import static com.kiwisoft.utils.StringUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.sql.SQLException;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.EpisodeInfo;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.gui.progress.ObservableRunnable;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.*;

public abstract class GermanEpisodeImport implements ObservableRunnable
{
	private ProgressSupport progressSupport=new ProgressSupport(null);

	private String source;

	protected GermanEpisodeImport(String source)
	{
		this.source=source;
	}

	public String getName()
	{
		return "Importiere Episoden...";
	}

	public void setProgress(ProgressListener progressListener)
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
		String rootPath=Configurator.getInstance().getString("path.root");
		Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
		ShowManager showManager=ShowManager.getInstance();
		Iterator it=episodes.iterator();
		int counter=0;
		while (it.hasNext())
		{
			XMLEpisodeInfo info=(XMLEpisodeInfo)it.next();
			String originalTitle=info.getOriginalTitle();
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
					if (episode!=null)
					{
						EpisodeInfo episodeInfo=DBLoader.getInstance().load(EpisodeInfo.class, null,
																	 "episode_id=? and name='Beschreibung (deutsch)'", episode.getId());
						if (episodeInfo==null)
						{
							String path="shows/"+show.getUserKey()+"/episodes/"+episode.getUserKey().replace(".", "/")+"/info_de.xp";
							File file=new File(rootPath, path);
							if (!file.exists())
							{
								file.getParentFile().mkdirs();
								createInfoFile(episode, info, file);
								Transaction transaction=null;
								try
								{
									transaction=DBSession.getInstance().createTransaction();
									episodeInfo=episode.createInfo();
									episodeInfo.setLanguage(german);
									episodeInfo.setName("Beschreibung (deutsch)");
									episodeInfo.setPath(path);
									episode.setDefaultInfo(episodeInfo);
									transaction.close();
								}
								catch (Throwable t)
								{
									t.printStackTrace();
									progressSupport.error(t.getMessage());
									try
									{
										if (transaction!=null) transaction.rollback();
									}
									catch (SQLException e)
									{
										e.printStackTrace();
									}
									return;
								}

								counter++;
							}
							else progressSupport.warning("File '"+file.getAbsolutePath()+"' isn't connected.");
						}
					}
				}
			}
			else progressSupport.warning("Keine Serie mit dem Titel '"+info.getShow()+"' gefunden. (Datei: "+info.getFile()+")");
			progressSupport.progress(1, true);
		}
		progressSupport.message(counter+" Episoden erzeugt");
	}

	protected abstract Episode createEpisode(Show show, XMLEpisodeInfo info);

	private void createInfoFile(Episode episode, XMLEpisodeInfo info, File file
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
		if (info.getOriginalTitle()!=null) xmlWriter.addComment("Original Title: "+info.getOriginalTitle());
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
		xmlHandler.addTagMapping("Details", XMLEpisodeInfo.class);
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
						if (xmlObject instanceof XMLEpisodeInfo) episodes.add(xmlObject);
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
}
