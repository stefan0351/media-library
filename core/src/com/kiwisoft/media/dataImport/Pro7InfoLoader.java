package com.kiwisoft.media.dataImport;

import static com.kiwisoft.utils.StringUtils.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.gui.progress.ObservableRunnable;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;

public class Pro7InfoLoader implements ObservableRunnable
{
	private String path;
	private Map<String, String> details;
	private int offset=14;
	private final static String BASE_URL="http://www.prosieben.de/service/tvprogramm/";
	private Calendar startDate;
	private ProgressSupport progressSupport=new ProgressSupport(null);
	private Set<Show> shows;

	public Pro7InfoLoader(String path, Date date, int days, Set<Show> shows)
	{
		this.shows=shows;
		startDate=Calendar.getInstance();
		startDate.setTime(date);
		offset=days;
		this.path=path;
		details=new HashMap<String, String>();
	}

	public void setProgress(ProgressListener progressListener)
	{
		progressSupport=new ProgressSupport(progressListener);
	}

	public String getName()
	{
		return "Lade Pro7 Termine";
	}

	public void run()
	{
		try
		{
			loadListings();
			loadDetails();
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

	private void loadListings()
	{
		progressSupport.step("Lade Listen...");
		progressSupport.initialize(offset);
		for (int i=0; i<offset; i++)
		{
			loadListing(startDate.getTime());
			progressSupport.progress(1, true);
			startDate.add(Calendar.DATE, 1);
		}
	}

	private void loadListing(Date date)
	{
		try
		{
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			String listing=null;
			int attempt=0;
			while (attempt<10 && listing==null)
			{
				attempt++;
				try
				{
					//http://www.prosieben.de/service/tvprogramm/index.php?action=onProgramm&datum=2004-09-24
					listing=WebUtils.loadURL(BASE_URL+"index.php?action=onProgramm&datum="+format.format(date));
				}
				catch (Exception e)
				{
					listing=null;
				}
			}
			if (listing!=null)
			{
				savePage(listing, format.format(date));
				parseListing(listing);
				progressSupport.message("Liste für "+DateFormat.getDateInstance().format(startDate.getTime())+" geladen");
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		progressSupport.error("Liste für "+DateFormat.getDateInstance().format(startDate.getTime())+" nicht geladen");
	}

	private void savePage(String listing, String name) throws IOException
	{
		File file=new File(path, name+".html");
		FileWriter fileWriter=new FileWriter(file);
		fileWriter.write(listing);
		fileWriter.flush();
		fileWriter.close();
	}

	private void parseListing(String listing)
	{
		//javascript:openPopup('/service/tvprogramm/popup.php?action=onDetail&id=7589767', 800, 500)
		int pos=listing.indexOf("<a href=\"Javascript:openPopup('/service/tvprogramm/popup.php?action=onDetail");
		while (pos>=0)
		{
			int end=listing.indexOf("</a>", pos);
			String s=listing.substring(pos, end);
			int nameIndex=s.indexOf("</strong>");
			if (nameIndex>0)
			{
				String name=s.substring(nameIndex);
				name=XMLUtils.resolveEntities(XMLUtils.removeTags(name)).trim();
				Show show=ShowManager.getInstance().getShowByName(name);
				if (show!=null && (shows==null || shows.contains(show)))
				{
					int idStart=s.indexOf("action=onDetail")+23;
					int idEnd=s.indexOf("'", idStart);
					String id=s.substring(idStart, idEnd);
					details.put(id, name);
				}
			}
			pos=listing.indexOf("<a href=\"Javascript:openPopup('/service/tvprogramm/popup.php?action=onDetail", end);
		}
	}

	private void loadDetails()
	{
		if (details.isEmpty())
		{
			progressSupport.warning("Keine Informationen gefunden.");
			return;
		}
		Set<String> keys=details.keySet();
		progressSupport.initialize(keys.size());
		Iterator<String> it=keys.iterator();
		progressSupport.step("Lade Informationen...");
		while (it.hasNext())
		{
			int attempt=0;
			boolean loaded=false;
			boolean parsed=false;
			String detailsId=it.next();
			String name=details.get(detailsId);
			while (attempt<10)
			{
				try
				{
					String detail=WebUtils.loadURL(BASE_URL+"popup.php?action=onDetail&id="+detailsId);
					loaded=true;
					parsed=parseDetail(name, detail);
					break;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					attempt++;
				}
			}
			if (!loaded)
				progressSupport.error("Information #"+detailsId+"("+name+") konnte nicht geladen");
			else if (!parsed)
				progressSupport.message("Information #"+detailsId+"("+name+") konnte geladen, aber nicht geparst werden");
			else
				progressSupport.message("Information #"+detailsId+"("+name+") geladen");
			progressSupport.progress(1, true);
		}
	}

	private boolean parseDetail(String name, String buffer)
	{
		SimpleDateFormat dateFormatP7=new SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.GERMAN);
		Calendar calendar=Calendar.getInstance();
		Calendar today=Calendar.getInstance();
		try
		{
			int number=2;

			int n2=2;
			File file2=new File(path+File.separator+name+".html");
			while (file2.exists()) file2=new File(path+File.separator+name+"."+(n2++)+".html");
			FileWriter fw2=new FileWriter(file2);
			fw2.write(buffer);
			fw2.flush();
			fw2.close();

			File file=new File(path+File.separator+name+".xml");
			while (file.exists()) file=new File(path+File.separator+name+"."+(number++)+".xml");
			FileWriter fw=new FileWriter(file);

			fw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n");
			fw.write("<Listing>\n");
			fw.write("<Details>\n");
			fw.write("<DataSource>");
			fw.write(DataSource.PRO7.getKey());
			fw.write("</DataSource>\n");

			int pos=buffer.indexOf("Uhr</strong>");
			pos=buffer.lastIndexOf("<strong", pos);
			int end=buffer.indexOf("Uhr</strong>", pos);
			String dateString=XMLUtils.resolveEntities(XMLUtils.removeTags(buffer.substring(pos, end))).trim();
			Date date=null;
			int attempt=0;
			DateFormat dateFormat=dateFormatP7;
			while (attempt<3)
			{
				attempt++;
				try
				{
					date=dateFormat.parse(dateString);
				}
				catch (ParseException e)
				{
					if (attempt==1)
					{
						System.out.print("Invalid date: "+dateString);
						dateFormat=new SimpleDateFormat("d. HH:mm", Locale.GERMAN);
						System.out.println(" retry with format: 'd. HH:mm'");
						continue;
					}
					if (attempt==2)
					{
						System.out.print("Invalid date: "+dateString);
						dateFormat=dateFormatP7;
						dateString=replaceStrings(dateString, "Aprill", "April");
						System.out.println(" retry with date: "+dateString);
						continue;
					}
					throw e;
				}
				break;
			}
			calendar.setTime(date);
			if (attempt==2)
			{
				if (calendar.get(Calendar.DAY_OF_MONTH)<startDate.get(Calendar.DAY_OF_MONTH))
					calendar.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
				else
					calendar.set(Calendar.MONTH, startDate.get(Calendar.MONTH)-1);
			}
			calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
			if (calendar.before(today)) calendar.add(Calendar.YEAR, 1);
			if (calendar.get(Calendar.HOUR_OF_DAY)<5) calendar.add(Calendar.DATE,1);
			if (attempt>1) System.out.println("Date: "+dateFormatP7.format(calendar.getTime()));
			fw.write("<Sendetermin><Sender>Pro Sieben</Sender><Datum>"+ImportConstants.DATE_FORMAT.format(calendar.getTime())+"</Datum></Sendetermin>\n");
			fw.flush();

			pos=buffer.indexOf("<div style=\"padding: 0px 0px 4px 15px;\"><strong>", end);
			end=buffer.indexOf("</strong>", pos);
			String showString=XMLUtils.resolveEntities(XMLUtils.removeTags(buffer.substring(pos, end))).trim();
			fw.write("<Show>"+XMLUtils.toXMLString(showString)+"</Show>\n");
			fw.flush();

			pos=buffer.indexOf("<div style=\"padding: 0px 10px 4px 15px;\"><strong>", end);
			end=buffer.indexOf("</strong>", pos);
			String episodeString=XMLUtils.resolveEntities(XMLUtils.removeTags(buffer.substring(pos, end))).trim();
			fw.write("<Episode>"+XMLUtils.toXMLString(episodeString)+"</Episode>\n");
			fw.flush();

			pos=buffer.indexOf("<div style=\"padding: 0px 20px 25px 15px; line-height:14px;\">", end);
			end=buffer.indexOf("</div>", pos);
			String contentString=XMLUtils.resolveEntities(XMLUtils.removeTags(buffer.substring(pos, end))).trim();
			fw.write("<Inhalt>"+XMLUtils.toXMLString(contentString)+"</Inhalt>\n");

			pos=buffer.indexOf("<table width=\"540\"", pos);
			String lastItemName="";
			while (pos>0)
			{
				pos=endIndexOf(buffer, "style=\"padding: 2px 0px 2px 15px; line-height:14px;\">", pos);
				end=buffer.indexOf("</td>", pos);
				if (pos<0 || end<0) break;
				String itemName=XMLUtils.resolveEntities(XMLUtils.removeTags(buffer.substring(pos, end))).trim();
				if (isEmpty(itemName) || "&nbsp".equals(itemName)) itemName=lastItemName;
				lastItemName=itemName;

				pos=endIndexOf(buffer, "style=\"line-height:14px; padding: 2px 0px 2px 0px;\">", pos);
				end=buffer.indexOf("</td>", pos);
				if (pos<0 || end<0) break;
				String itemValue=XMLUtils.resolveEntities(buffer.substring(pos, end)).trim();
				itemValue=replaceStrings(itemValue, "  ", " ");
				itemValue=replaceStrings(itemValue, "<br />", "|");

				if ("Darsteller:".equals(itemName))
				{
					StringTokenizer tokens=new StringTokenizer(itemValue, "|");
					MessageFormat castFormat=new MessageFormat("{0} ({1})");
					while (tokens.hasMoreTokens())
					{
						String token=tokens.nextToken();
						try
						{
							Object[] values=castFormat.parse(token);
							String actor=String.valueOf(values[0]);
							String character=String.valueOf(values[1]);
							fw.write("<Darsteller>");
							fw.write("<Schauspieler>"+XMLUtils.toXMLString(actor)+"</Schauspieler>");
							fw.write("<Charakter>"+XMLUtils.toXMLString(character)+"</Charakter>");
							fw.write("</Darsteller>\n");
							fw.flush();
						}
						catch (ParseException e)
						{
							System.out.println("ParseException: "+token);
						}
					}
				}
				else if ("Regie:".equals(itemName))
				{
					fw.write("<Regie>"+XMLUtils.toXMLString(itemValue)+"</Regie>\n");
					fw.flush();
				}
				else if ("Drehbuch:".equals(itemName))
				{
					fw.write("<Drehbuch>"+XMLUtils.toXMLString(itemValue)+"</Drehbuch>\n");
					fw.flush();
				}
				else if ("Kamera:".equals(itemName))
				{
					fw.write("<Kamera>"+XMLUtils.toXMLString(itemValue)+"</Kamera>\n");
					fw.flush();
				}
				else if ("Dialogregie:".equals(itemName))
				{
					fw.write("<Dialogregie>"+XMLUtils.toXMLString(itemValue)+"</Dialogregie>\n");
					fw.flush();
				}
				else if ("Dialogbuch:".equals(itemName))
				{
					fw.write("<Dialogbuch>"+XMLUtils.toXMLString(itemValue)+"</Dialogbuch>\n");
					fw.flush();
				}
				else if ("Komponist:".equals(itemName))
				{
					fw.write("<Komponist>"+XMLUtils.toXMLString(itemValue)+"</Komponist>\n");
					fw.flush();
				}
				else if ("Produzent:".equals(itemName))
				{
					fw.write("<Produzent>"+XMLUtils.toXMLString(itemValue)+"</Produzent>\n");
					fw.flush();
				}
				else if ("Kostüme:".equals(itemName) || "Kostueme:".equals(itemName))
				{
					fw.write("<Kostueme>"+XMLUtils.toXMLString(itemValue)+"</Kostueme>\n");
					fw.flush();
				}
				else if ("Autor:".equals(itemName))
				{
					fw.write("<Autor>"+XMLUtils.toXMLString(itemValue)+"</Autor>\n");
					fw.flush();
				}
				else if ("Gaststar:".equals(itemName))
				{
					fw.write("<Darsteller>");
					fw.write("<Schauspieler>"+XMLUtils.toXMLString(itemValue)+"</Schauspieler>");
					fw.write("<Charakter>sich selbst</Charakter>");
					fw.write("</Darsteller>\n");
					fw.flush();
				}
				else if ("Links:".equals(itemName) || "Schnitt:".equals(itemName))
				{
					// skip item
				}
				else
				{
					System.out.println("Unknown item: "+itemName);
				}
				pos=end;
			}

			fw.write("</Details>\n");
			fw.write("</Listing>\n");
			fw.flush();
			fw.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private static int endIndexOf(String string, String pattern, int startIndex)
	{
		int index=string.indexOf(pattern, startIndex);
		if (index>=0) index=index+pattern.length();
		return index;
	}

}
