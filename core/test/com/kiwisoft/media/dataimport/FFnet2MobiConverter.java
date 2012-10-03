package com.kiwisoft.media.dataimport;

import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import org.apache.commons.lang.StringEscapeUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.ParserException;
import org.w3c.tidy.Tidy;

import java.io.*;
import java.util.*;

/**
 * @author Stefan Stiller
 * @since 02.10.11
 */
public class FFnet2MobiConverter
{
	private static final String CALIBRE_HOME="c:/Program Files (x86)/Calibre2";

	private FFnet2MobiConverter()
	{
	}

	public static void main(String[] args) throws Exception
	{
		ImportUtils.USE_CACHE=true;
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		configuration.loadDefaultsFromFile(new File("conf", "config-dev.xml"));

		FanFictionNetLoader loader=new FanFictionNetLoader(args[0]);
		FanFicData info=loader.getInfo();
		if (info!=null)
		{
			System.out.println("Title: "+info.getTitle());
			System.out.println("Author: "+info.getAuthor());
			System.out.println("FanDom: "+info.getDomains());
			System.out.println("Summary: "+info.getSummary());
			System.out.println("Rating: "+info.getRating());
			System.out.println("Published Date: "+info.getPublishedDate());
			System.out.println("Language: "+info.getLanguage());
			System.out.println("Characters: "+info.getCharacters());
			System.out.println("Genres: "+info.getGenres());
			System.out.println("Chapters: "+info.getChapterCount()+" "+info.getChapters());
			String baseName=FileUtils.toFileName(info.getTitle());

			File workingDir=new File("tmp", "ebooks");
			workingDir.mkdirs();

			File htmlFile=new File(workingDir, baseName+".xhtml");
			saveToXhtml(loader, info, htmlFile);

			File mobiFile=new File(workingDir, baseName+".mobi");
			convertXhtmlToMobi(info, htmlFile, mobiFile);
		}
	}

	private static void convertXhtmlToMobi(FanFicData info, File htmlFile, File mobiFile) throws IOException, InterruptedException
	{
		Set<String> tags=new HashSet<String>();
		if (info.getCharacters()!=null) tags.addAll(info.getCharacters());
		if (info.getDomains()!=null) tags.addAll(info.getDomains());
		if (info.getGenres()!=null) tags.addAll(info.getGenres());

		List<String> command=new ArrayList<String>();
		command.add(CALIBRE_HOME+"/ebook-convert");
		command.add(htmlFile.getAbsolutePath());
		command.add(mobiFile.getAbsolutePath());
		command.add("--publisher");
		command.add("FanFiction.net");
		command.add("--title");
		command.add(info.getTitle());
		if (info.getAuthor()!=null)
		{
			command.add("--authors");
			command.add(info.getAuthor());
		}
		command.add("--chapter");
		command.add("//h:h2");
		command.add("--level1-toc");
		command.add("//h:h2");
		if (!tags.isEmpty())
		{
			command.add("--tags");
			command.add(StringUtils.formatAsEnumeration(tags, ","));
		}
		Utils.run(command, System.out, System.err);
	}

	private static void saveToXhtml(FanFictionNetLoader loader, FanFicData info, File file) throws IOException, ParserException
	{
		PrintWriter htmlWriter=new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
		try
		{
			htmlWriter.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			htmlWriter.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			htmlWriter.println("<body>");
			htmlWriter.println("<p style=\"font-size:200%; text-align: center;\">"+StringEscapeUtils.escapeHtml(info.getTitle())+"</p>");
			htmlWriter.println("<p style=\"font-size:150%; text-align: center;font-style: italic;\">by "+StringEscapeUtils.escapeHtml(info.getAuthor())+"</p>");
			for (int i=1; i<=info.getChapterCount(); i++)
			{
				htmlWriter.println("<h2 style=\"text-align: center;\">"+StringEscapeUtils.escapeHtml(info.getChapters().get(i-1))+"</h2>");
				String xhtml=toXHtml(loader.getChapter(i));

				Parser parser=new Parser();
				parser.setInputHTML(xhtml);
				BodyTag bodyTag=(BodyTag) HtmlUtils.findFirst(parser, "body");
				Node shareTag=HtmlUtils.findFirst(bodyTag, "div.a2a_kit");
				if (shareTag!=null)
				{
					CompositeTag parent=(CompositeTag) shareTag.getParent();
					parent.removeChild(parent.findPositionOf(shareTag));
				}
				htmlWriter.println(HtmlUtils.getInnerHtml(bodyTag));
			}
			htmlWriter.println("</body>");
			htmlWriter.println("</html>");
			htmlWriter.flush();

			//Desktop.getDesktop().browse(htmlFile.toURI());
		}
		finally
		{
			htmlWriter.close();
		}
	}

	private static String toXHtml(String text)
	{
		text=text.replace("<g:plusone size='medium' count='false'></g:plusone>", "");

		StringWriter tidyWriter=new StringWriter();
		Tidy tidy=new Tidy();
		tidy.setErrout(new PrintWriter(new StringWriter()));
		tidy.setQuiet(true);
		tidy.setXHTML(true);
		tidy.parse(new StringReader(text), tidyWriter);
		return tidyWriter.toString();
	}
}
