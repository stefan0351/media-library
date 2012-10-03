package com.kiwisoft.media.fanfic;

import com.kiwisoft.collection.Chain;
import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.CompositeTag;
import org.w3c.tidy.Tidy;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 02.10.11
 */
public class FanFicEBookCreator
{
	private static final String CALIBRE_HOME="c:/Program Files (x86)/Calibre2";
	private FanFic fanFic;

	public FanFicEBookCreator(FanFic fanFic)
	{
		this.fanFic=fanFic;
	}

	public void convertToMobi() throws Exception
	{
		String baseName=FileUtils.toFileName(fanFic.getTitle());

		File workingDir=new File("tmp", "ebooks");
		workingDir.mkdirs();

		File htmlFile=new File(workingDir, baseName+".xhtml");
		saveToXhtml(htmlFile);

		File mobiFile=new File(workingDir, baseName+".mobi");
		convertXhtmlToMobi(htmlFile, mobiFile);
	}

	private void convertXhtmlToMobi(File htmlFile, File mobiFile) throws IOException, InterruptedException
	{
		Set<String> tags=new HashSet<String>();
		for (Pairing pairing : fanFic.getPairings()) tags.add(pairing.getName());
		for (FanDom fanDom : fanFic.getFanDoms()) tags.add(fanDom.getName());

		List<String> command=new ArrayList<String>();
		command.add(CALIBRE_HOME+"/ebook-convert");
		command.add(htmlFile.getAbsolutePath());
		command.add(mobiFile.getAbsolutePath());
		command.add("--publisher");
		command.add("FanFiction.net");
		command.add("--title");
		command.add(fanFic.getTitle());
		command.add("--authors");
		command.add(StringUtils.formatAsEnumeration(fanFic.getAuthors(), ", "));
		command.add("--chapter");
		command.add("//h:h2");
		command.add("--level1-toc");
		command.add("//h:h2");
		command.add("--tags");
		command.add(StringUtils.formatAsEnumeration(tags, ","));
		Utils.run(command, System.out, System.err);
	}

	private void saveToXhtml(File file) throws Exception
	{
		PrintWriter htmlWriter=new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
		try
		{
			htmlWriter.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			htmlWriter.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			htmlWriter.println("<body>");
			htmlWriter.println("<p style=\"font-size:200%; text-align: center;\">"+StringEscapeUtils.escapeHtml(fanFic.getTitle())+"</p>");
			String authors=StringUtils.formatAsEnumeration(fanFic.getAuthors(), ", ");
			htmlWriter.println("<p style=\"font-size:150%; text-align: center;font-style: italic;\">by "+StringEscapeUtils.escapeHtml(authors)+"</p>");
			Chain<FanFicPart> parts=fanFic.getParts();
			for (int i=0; i<parts.size(); i++)
			{
				FanFicPart part=parts.elements().get(i);
				String chapterTitle=part.getName();
				if (StringUtils.isEmpty(chapterTitle))
				{
					if (parts.size()==1 && fanFic.isFinished()) chapterTitle=fanFic.getTitle();
					else chapterTitle="Chapter "+(i+1);
				}
				htmlWriter.println("<h2 style=\"text-align: center;\">"+StringEscapeUtils.escapeHtml(part.getName())+"</h2>");
				String xhtml=toXHtml(part.getContent());
				System.out.println("FanFicEBookCreator.saveToXhtml: xhtml = "+xhtml);

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

	private String toXHtml(InputStream input) throws IOException
	{
		String content=IOUtils.toString(input, "UTF-8");
		content=content.replace("<g:plusone size='medium' count='false'></g:plusone>", "");

		StringWriter tidyWriter=new StringWriter();
		Tidy tidy=new Tidy();
		tidy.setErrout(new PrintWriter(new StringWriter()));
		tidy.setQuiet(true);
		tidy.setXHTML(true);
		tidy.parse(new StringReader(content), tidyWriter);
		return tidyWriter.toString();
	}
}
