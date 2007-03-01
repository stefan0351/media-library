/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 3:46:13 PM
 */
package com.kiwisoft.media.dataImport;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.utils.RegularFileFilter;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.db.*;
import com.kiwisoft.utils.gui.progress.ObservableRunnable;
import com.kiwisoft.utils.gui.progress.ProgressDialog;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.DefaultXMLObject;
import com.kiwisoft.utils.xml.XMLHandler;
import com.kiwisoft.utils.xml.XMLObject;

public class AirdateImport implements ObservableRunnable
{
	private String path;
	private String filter;
	private Set<String> ignoredChannels;
	private Set<String> ignoredShows;

	private ProgressSupport progressSupport=new ProgressSupport(null);
	private ProgressDialog progressDialog;
	private int created;

	public AirdateImport(String path, String filter)
	{
		this.path=path;
		this.filter=filter;
	}

	public String getName()
	{
		return "Importiere Sendetermine";
	}

	public void setProgress(ProgressListener progressListener)
	{
		progressSupport=new ProgressSupport(progressListener);
	}

	public void run()
	{
		try
		{
			progressSupport.step("Initialisieren...");
			File file=new File(path);
			String[] fileNames=file.list(new RegularFileFilter(filter));

			Set<AirdateData> preAirdates=loadFiles(fileNames);
			createAirdates(preAirdates);
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

	private void createAirdates(Set<AirdateData> preAirdates)
	{
		Set<String> newIgnoredChannels=new HashSet<String>();
		Set<String> newIgnoredShows=new HashSet<String>();
		created=0;
		progressSupport.step("Sendetermine erzeugen...");
		progressSupport.initialize(preAirdates.size());
		Iterator<AirdateData> it=preAirdates.iterator();
		while (it.hasNext())
		{
			AirdateData airdateData=it.next();
			if (airdateData.getShow()!=null)
			{
				try
				{
					airdateData.getEpisode();
				}
				catch (DBException e)
				{
					if (e.getErrorCode()==DBErrors.MULTIPLE_OBJECTS_FOUND)
						progressSupport.warning("Serie '"+airdateData.getShow().getName()+"' enthält mehrere Episoden mit Titel '"+airdateData.getEpisodeName()+"'");
					else
						throw e;
				}
				if (airdateData.getEpisode()==null) airdateData.setEvent(airdateData.getEpisodeName());

				Iterator<AiringData> itAirings=airdateData.getAirings().iterator();
				while (itAirings.hasNext())
				{
					AiringData airingData=itAirings.next();
					if (airingData.getTime()!=null)
					{
						if (airingData.getChannel()!=null)
						{
							Set<Airdate> concurrentDates=getConcurrentAirdates(airingData, 20);
							if (concurrentDates.isEmpty()) insertAirdate(airingData, airdateData, null);
							else
							{
								boolean found=false;
								Iterator<Airdate> itDates=concurrentDates.iterator();
								while (itDates.hasNext() && !found)
								{
									Airdate oldAirdate=itDates.next();
									if (oldAirdate.equals(airingData.getTime(), airdateData)) found=true;
								}
								if (!found)
								{
									ConcurrentAirdateDialog dialog=new ConcurrentAirdateDialog(progressDialog, concurrentDates, airdateData, airingData);
									dialog.setVisible(true);
									Set<Airdate> invalidDates=dialog.getInvalidAirdates();
									if (invalidDates!=null)
									{
										if (dialog.isAddAirdate()) insertAirdate(airingData, airdateData, invalidDates);
										else insertAirdate(null, null, invalidDates);
									}
								}
							}
						}
						else
						{
							if (!getIgnoredChannels().contains(airingData.getChannelName())) newIgnoredChannels.add(airingData.getChannelName());
						}
					}
					else
						progressSupport.warning("Termin für '"+airdateData.getShowName()+"; "+airdateData.getEpisodeName()+" ist fehlerhaft.");
				}
			}
			else
			{
				if (!getIgnoredShows().contains(airdateData.getShowName())) newIgnoredShows.add(airdateData.getShowName());
			}
			progressSupport.progress(1, true);
		}

		progressSupport.message(created+" Sendetermine erzeugt");
		if (!newIgnoredChannels.isEmpty())
		{
			progressSupport.warning(newIgnoredChannels.size()+" unbekannte Sender");
			for (String name : newIgnoredChannels)
			{
				System.out.println(name);
				progressSupport.message(name);
			}
		}
		if (!newIgnoredShows.isEmpty())
		{
			progressSupport.warning(newIgnoredShows.size()+" unbekannte Serien");
			for (String name : newIgnoredShows)
			{
				System.out.println(name);
				progressSupport.message(name);
			}
		}
	}

	private Set<AirdateData> loadFiles(String[] fileNames)
	{
		File file;
		Set<AirdateData> preAirdates=new HashSet<AirdateData>();
		progressSupport.step("Lade Dateien...");
		progressSupport.initialize(fileNames.length);
		for (int i=0; i<fileNames.length; i++)
		{
			String fileName=fileNames[i];
			file=new File(path, fileName);
			try
			{
				preAirdates.addAll(loadFile(file));
			}
			catch (Exception e)
			{
				progressSupport.error("Error in file '"+file+"': "+e.getMessage());
			}
			progressSupport.progress(1, true);
		}
		return preAirdates;
	}

	private void insertAirdate(AiringData airingData, AirdateData airdateData, Set<Airdate> oldAirdates)
	{
		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (oldAirdates!=null)
			{
				Iterator<Airdate> it=oldAirdates.iterator();
				while (it.hasNext())
				{
					Airdate oldAirdate=it.next();
					oldAirdate.delete();
				}
			}
			if (airingData!=null && airdateData!=null)
			{
				Airdate airdate=new Airdate();
				airdate.setChannel(airingData.getChannel());
				airdate.setDate(airingData.getTime());
				airdate.setShow(airdateData.getShow());
				airdate.setEpisode(airdateData.getEpisode());
				airdate.setEvent(airdateData.getEvent());
				airdate.setLanguage(airingData.getChannel().getLanguage());
				airdate.setDataSource(airdateData.getDataSource());
				created++;
			}
			transaction.close();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			try
			{
				if (transaction!=null) transaction.rollback();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			progressSupport.error(t.getMessage());
		}
	}

	private Set<AirdateData> loadFile(File file)
	{
		Set<AirdateData> airdates=new HashSet<AirdateData>();
		XMLHandler xmlHandler=new XMLHandler();
		xmlHandler.addTagMapping("Details", AirdateData.class);
		xmlHandler.addTagMapping("Sendetermin", AiringData.class);
		XMLObject root=xmlHandler.loadFile(file);
		if (root instanceof DefaultXMLObject)
		{
			DefaultXMLObject listing=(DefaultXMLObject)root;
			if ("Listing".equalsIgnoreCase(listing.getName()))
			{
				Iterator<XMLObject> it=listing.getElements().iterator();
				while (it.hasNext())
				{
					XMLObject xmlObject=it.next();
					if (xmlObject instanceof AirdateData) airdates.add((AirdateData)xmlObject);
				}
			}
		}
		return airdates;
	}

	private Set<String> getIgnoredChannels()
	{
		if (ignoredChannels==null)
		{
			ignoredChannels=new HashSet<String>();
			try
			{
				BufferedReader reader=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("channels.properties")));
				String line;
				while ((line=reader.readLine())!=null) ignoredChannels.add(line);
				reader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				progressSupport.error(e.getMessage());
			}
		}
		return ignoredChannels;
	}

	private Set<String> getIgnoredShows()
	{
		if (ignoredShows==null)
		{
			ignoredShows=new HashSet<String>();
			try
			{
				BufferedReader reader=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("shows.properties")));
				String line;
				while ((line=reader.readLine())!=null) ignoredShows.add(line);
				reader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				progressSupport.error(e.getMessage());
			}
		}
		return ignoredShows;
	}

	public void setDialog(ProgressDialog dialog)
	{
		this.progressDialog=dialog;
	}

	public Set<Airdate> getConcurrentAirdates(AiringData airing, int range)
	{
		Date startTime=DateUtils.addMinutes(airing.getTime(), -range);
		Date endTime=DateUtils.addMinutes(airing.getTime(), range);
		return DBLoader.getInstance().loadSet(Airdate.class,
				null,
				"channel_id=? and viewdate>? and viewdate<?",
				airing.getChannel().getId(), startTime, endTime);
	}

}
