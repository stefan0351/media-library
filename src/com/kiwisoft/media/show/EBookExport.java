package com.kiwisoft.media.show;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.kiwisoft.media.Language;
import com.kiwisoft.utils.PMLWriter;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.xp.XPLoader;
import com.kiwisoft.xp.XPBean;
import com.peanutpress.BuildBook;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class EBookExport
{
	private File file;
	private Show show;
	private File pmlFile;

	public EBookExport(Show show, File file)
	{
		this.show=show;
		if (!file.getName().toLowerCase().endsWith(".pdb")) file=new File(file.getAbsolutePath()+".pdb");
		this.file=file;
	}

	public void execute() throws IOException
	{
		try
		{
			createPMLFile();
			BuildBook.build(pmlFile.getAbsolutePath(), file.getAbsolutePath(), show.getName()+" - Guide");
		}
		finally
		{
			if (pmlFile!=null) pmlFile.delete();
		}
	}

	private void createPMLFile() throws IOException
	{
		pmlFile=File.createTempFile("eBook", ".pml", file.getParentFile());
		PMLWriter pmlWriter=new PMLWriter(pmlFile);
		pmlWriter.newPage();
		pmlWriter.setAlignCenter(true);
		pmlWriter.newLine();
		pmlWriter.emptyLines(3);
		pmlWriter.addHorizontalLine(90);
		pmlWriter.setBold(true);
		pmlWriter.println(show.getName());
		pmlWriter.emptyLines(1);
		pmlWriter.println("Guide");
		pmlWriter.setBold(false);
		pmlWriter.addHorizontalLine(90);
		pmlWriter.setAlignCenter(false);
		Language language=show.getLanguage();
		pmlWriter.newPage();
		pmlWriter.setBold(true);
		pmlWriter.newHeader("Staffeln", 0);
		pmlWriter.setBold(false);
		pmlWriter.emptyLines(1);
		TreeSet seasons=new TreeSet(show.getSeasons());
		for (Iterator it=seasons.iterator(); it.hasNext();)
		{
			Season season=(Season)it.next();
			pmlWriter.startLink("s"+season.getId());
			pmlWriter.print(season.toString());
			pmlWriter.closeLink();
			pmlWriter.newLine();
		}
		for (Iterator itSeasons=seasons.iterator(); itSeasons.hasNext();)
		{
			Season season=(Season)itSeasons.next();
			pmlWriter.newPage();
			pmlWriter.addAnchor("s"+season.getId());
			pmlWriter.setBold(true);
			pmlWriter.newHeader(season.toString(), 1);
			pmlWriter.setBold(false);
			pmlWriter.emptyLines(1);
			LinkedHashMap episodes=new LinkedHashMap();
			for (Iterator itEpisodes=new TreeSet(season.getEpisodes()).iterator(); itEpisodes.hasNext();)
			{
				Episode episode=(Episode)itEpisodes.next();
				boolean link=false;
				for (Iterator itInfos=episode.getInfos().iterator(); itInfos.hasNext();)
				{
					EpisodeInfo info=(EpisodeInfo)itInfos.next();
					if ("de".equals(info.getLanguage().getSymbol()) && info.getPath().endsWith("info_de.xp"))
					{
						String path=new File(Configurator.getInstance().getString("path.root"), info.getPath()).getAbsolutePath();
						XPBean data=(XPBean)XPLoader.loadXMLFile(path, info.getPath());
						XPBean content=(XPBean)data.getValue("content");
						if (content!=null)
						{
							episodes.put(episode, content.toString());
							link=true;
						}
					}
				}
				pmlWriter.setIndent(true);
				if (link) pmlWriter.startLink("e"+episode.getId());
				pmlWriter.setItalic(true);
				pmlWriter.print(episode.getUserKey());
				pmlWriter.setItalic(false);
				pmlWriter.println(" "+episode.getName());
				if (link) pmlWriter.closeLink();
				pmlWriter.setIndent(false);
				String originalTitle=null;
				if (language!=null && !"de".equals(language.getSymbol())) originalTitle=episode.getOriginalName();
				if (originalTitle!=null)
				{
					pmlWriter.setAlignRight(true);
					pmlWriter.println("("+toPMLString(originalTitle)+")");
					pmlWriter.setAlignRight(false);
				}
			}
			for (Iterator itEpisodes=episodes.keySet().iterator(); itEpisodes.hasNext();)
			{
				Episode episode=(Episode)itEpisodes.next();
				pmlWriter.newPage();
				pmlWriter.addAnchor("e"+episode.getId());
				pmlWriter.setBold(true);
				pmlWriter.newHeader(episode.getUserKey()+" "+episode.getName(), 2);
				pmlWriter.setBold(false);
				pmlWriter.emptyLines(1);
				String content=(String)episodes.get(episode);
				pmlWriter.printHTML(content);
				pmlWriter.newLine();
			}
		}
		pmlWriter.close();
	}

	/**
	 * Converts a text into a XML string by converting special characters
	 * into where corresponding entities.
	 *
	 * @param text The string to convert.
	 * @return The converted string.
	 */
	private static String toPMLString(String text)
	{
		StringBuffer buffer=new StringBuffer();
		int len=text.length();
		for (int i=0; i<len; i++)
		{
			char ch=text.charAt(i);
			if (ch=='\n' || ch=='\r')
				buffer.append(ch);
			else if ((ch<0x0020) || (ch>0x007e))
			{
				buffer.append("\\a");
				buffer.append((int)ch);
			}
			else
				buffer.append(ch);
		}
		return buffer.toString();
	}

}
