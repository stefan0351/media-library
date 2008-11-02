package com.kiwisoft.media.show;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.AirdateComparator;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;

/**
 * @author Stefan Stiller
 */
public class WebDatesExport implements Job
{
	private ProgressSupport progressSupport;

	public void setProgress(ProgressListener progressListener)
	{
	}

	public String getName()
	{
		return "Exportiere Internet-Sendetermine";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progressSupport=new ProgressSupport(this, progressListener);
		progressSupport.startStep("Lade Serien...");
		Set shows=ShowManager.getInstance().getInternetShows();
		progressSupport.startStep("Exportiere Termine...");
		progressSupport.initialize(true, shows.size(), null);
		Iterator it=shows.iterator();
		while (it.hasNext())
		{
			Show show=(Show)it.next();
			progressSupport.startStep("Exportiere Termine für "+show.getTitle()+"...");
			if (show.isInternet()) exportCurrentWebDates(show);
			progressSupport.progress(1, true);
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}

	// todo reruns
	private void exportCurrentWebDates(Show show)
	{
		File file=new File(MediaConfiguration.getWebSchedulePath());
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
		Date endDate=DateUtils.add(now, Calendar.DATE, 7);
		if (begin>=0 && end>=0)
		{
			FileWriter fw;
			try
			{
				List<Airdate> currentDates=new LinkedList<Airdate>();
				Iterator it=show.getAirdates().iterator();
				while (it.hasNext())
				{
					Airdate airdate=(Airdate)it.next();
					if (airdate.getDate().after(now)) currentDates.add(airdate);
				}
				Collections.sort(currentDates, new AirdateComparator(AirdateComparator.INV_TIME));
				String lb=System.getProperty("line.separator");
				fw=new FileWriter(file);
				fw.write(sBuffer.substring(0, begin));
				fw.write("<!--Begin Code "+showId+"-->"+lb);
				try
				{
					if (!currentDates.isEmpty())
					{
						it=currentDates.iterator();
						int count=0;
						while (it.hasNext())
						{
							Airdate airdate=(Airdate)it.next();
							if (count<7 || airdate.getDate().before(endDate))
							{
								fw.write(getCurrentWebHTML(airdate, file.getParent())+lb);
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
				fw.write(sBuffer.substring(end, sBuffer.length()));
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
		StringBuilder buffer=new StringBuilder();
		buffer.append("\t<tr><td><small>");
		Channel channel=date.getChannel();
		if (channel!=null)
		{
			Picture logo=channel.getLogo();
			String nameText=XMLUtils.toXMLString(channel.getName());
			if (logo!=null)
			{
				File logoFile=logo.getPhysicalFile();
				if (logoFile.exists())
				{
					FileUtils.syncFiles(logoFile, new File(MediaConfiguration.getWebChannelLogoPath()));
					buffer.append("<img src=\"/clipart/tv_logos/");
					buffer.append(logoFile.getName());
					buffer.append("\" alt=\"");
					buffer.append(nameText);
					buffer.append("\" title=\"");
					buffer.append(nameText);
					buffer.append("\" borderColor=black border=1 align=top>");
				}
				else buffer.append(nameText);
			}
			else buffer.append(nameText);
		}
		else buffer.append(XMLUtils.toXMLString(date.getChannelName()));
		buffer.append(" ");
		buffer.append(new SimpleDateFormat("EEE, d.M.yyyy H:mm", Locale.GERMANY).format(date.getDate()));
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
					href=FileUtils.getRelativePath(path, webScriptFile);
					href=StringUtils.replaceStrings(href, "\\", "/");
					style="script";
				}
				catch (Exception e)
				{
				}
			}
			String mouseInfo=episode.getJavaScript();
			title=episode.toString();
			if (href==null && mouseInfo!=null) href="javascript:void(0)";
			if (mouseInfo!=null) link="<a class=\""+style+"\" href=\""+href+"\" onMouseOver=\""+mouseInfo+"\" onMouseOut=\"nd();\">";
			else if (href!=null) link="<a class=\""+style+"\" href=\""+href+"\">";
		}
		else
		{
			if (date.getEvent()!=null) title="\""+date.getEvent()+"\"";
		}
		if (link!=null) buffer.append(" ").append(link).append(XMLUtils.toXMLString(title)).append("</a>");
		else buffer.append(" ").append(XMLUtils.toXMLString(title));
		buffer.append("</small></td></tr>");
		return buffer.toString();
	}

}
