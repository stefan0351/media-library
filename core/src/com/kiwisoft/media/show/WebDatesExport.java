package com.kiwisoft.media.show;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

import com.kiwisoft.utils.gui.progress.ObservableRunnable;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.AirdateComparator;
import com.kiwisoft.media.Channel;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 02.05.2004
 * Time: 16:43:07
 * To change this template use File | Settings | File Templates.
 */
public class WebDatesExport implements ObservableRunnable
{
	private ProgressSupport progressSupport=new ProgressSupport(null);

	public void setProgressListener(ProgressListener progressListener)
	{
		progressSupport=new ProgressSupport(progressListener);
	}

	public String getName()
	{
		return "Exportiere Internet-Sendetermine";
	}

	public void run()
	{
		try
		{
			progressSupport.step("Lade Serien...");
			Set shows=ShowManager.getInstance().getInternetShows();
			progressSupport.step("Exportiere Termine...");
			progressSupport.initialize(shows.size());
			Iterator it=shows.iterator();
			while (it.hasNext())
			{
				Show show=(Show)it.next();
				progressSupport.step("Exportiere Termine für "+show.getName()+"...");
				if (show.isInternet()) exportCurrentWebDates(show);
				progressSupport.progress(1, true);
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

	// todo reruns
	private void exportCurrentWebDates(Show show)
	{
		File file=new File(Configurator.getInstance().getString("file.web.dates"));
		FileReader fr;
		try
		{
			fr=new FileReader(file);
		}
		catch (FileNotFoundException fnfe)
		{
			JOptionPane.showMessageDialog(null, "Datei '"+file+"' nicht gefunden.", "Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}
		char[] buffer=new char[(int)file.length()];
		try
		{
			fr.read(buffer);
			fr.close();
		}
		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "I/O Fehler in Datei '"+file+"'.", "Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String sBuffer=new String(buffer);
		String showId=XMLUtils.toXMLString(show.getUserKey());
		int begin=sBuffer.indexOf("<!--Begin Code "+showId+"-->");
		int end=sBuffer.indexOf("<!--End Code "+showId+"-->");
		Date now=new Date();
		Date endDate=DateUtils.addDays(now, 7);
		if (begin>=0 && end>=0)
		{
			FileWriter fw;
			try
			{
				LinkedList currentDates=new LinkedList();
				Iterator it=show.getAirdates().iterator();
				while (it.hasNext())
				{
					Airdate airdate=(Airdate)it.next();
					if (airdate.getDate().after(now)) currentDates.add(airdate);
				}
				Collections.sort(currentDates,new AirdateComparator(AirdateComparator.INV_TIME));
				String lb=System.getProperty("line.separator");
				fw=new FileWriter(file);
				fw.write(sBuffer.substring(0,begin));
				fw.write("<!--Begin Code "+showId+"-->"+lb);
				try
				{
					if (currentDates.size()>0)
					{
						it=currentDates.iterator();
						int count=0;
						while (it.hasNext())
						{
							Airdate airdate=(Airdate)it.next();
							if (count<7 || airdate.getDate().before(endDate))
							{
								fw.write(getCurrentWebHTML(airdate,file.getParent())+lb);
								count++;
							}

						}
					}
					else
					{
						fw.write("\t<tr><td><font size=-1 color=#888888>Zur Zeit keine Sendertermine bekannt</font></td></tr>"+lb);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					fw.write("\t<tr><td><font size=-1 color=red>Ausgabefehler</font></td></tr>"+lb);
				}
				fw.write(sBuffer.substring(end,sBuffer.length()));
				fw.flush();
				fw.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				progressSupport.error("Ausnahmefehler: "+e.getMessage());
				return;
			}
		}
		else
		{
			progressSupport.error("Kein Codesegment für Schlüssel '"+show.getUserKey()+"' gefunden.");
		}
	}

	private String getCurrentWebHTML(Airdate date, String path)
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append("\t<tr><td><small>");
		Channel channel=date.getChannel();
		if (channel!=null)
		{
			String logo=channel.getLogo();
			String nameText=XMLUtils.toXMLString(channel.getName());
			if (logo!=null )
			{
				File logoFile=new File(logo);
				if (logoFile.exists())
				{
					FileUtils.syncFiles(logoFile, new File(Configurator.getInstance().getString("path.logos.channels.web")));
					buffer.append("<img src=\"/clipart/tv_logos/"+logoFile.getName()+"\" alt=\""+nameText+"\" title=\""+nameText+"\" borderColor=black border=1 align=top>");
				}
				else buffer.append(nameText);
			}
			else buffer.append(nameText);
		}
		else buffer.append(XMLUtils.toXMLString(date.getChannelName()));
		buffer.append(" "+new SimpleDateFormat("EEE, d.M.yyyy H:mm", Locale.GERMANY).format(date.getDate()));
		String href=null;
		String title="";
		String style="nav";
		String link=null;
		Episode episode=date.getEpisode();
		if (episode!=null)
		{
			String webScriptFile=episode.getWebScriptFile();
			if (webScriptFile!=null)
			{
				try
				{
					href=FileUtils.getRelativePath(path,webScriptFile);
					href=StringUtils.replaceStrings(href,"\\","/");
					style="script";
				}
				catch (Exception e) {}
			}
			String mouseInfo=episode.getJavaScript();
			title=episode.toString();
			if (href==null && mouseInfo!=null) href="javascript:void(0)";
			if (mouseInfo!=null) link="<a class=\""+style+"\" href=\""+href+"\" onMouseOver=\""+mouseInfo+"\" onMouseOut=\"nd();\">";
			else
				if (href!=null) link="<a class=\""+style+"\" href=\""+href+"\">";
		}
		else
		{
			if (date.getEvent()!=null) title="\""+date.getEvent()+"\"";
		}
		if (link!=null) buffer.append(" "+link+XMLUtils.toXMLString(title)+"</a>");
		else buffer.append(" "+XMLUtils.toXMLString(title));
		buffer.append("</small></td></tr>");
		return buffer.toString();
	}

}
