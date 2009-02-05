package com.kiwisoft.media.fanfic;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.Text;
import org.htmlparser.Tag;
import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.xml.XMLUtils;

/**
 * @author Stefan Stiller
 */
public class Html2Xp
{
	private static StringBuilder line=new StringBuilder();

	private Html2Xp()
	{
	}

	public static void main(String[] args) throws ParserException, IOException
	{
		File htmlFile=new File(args[0]);
		File xpFile=new File(htmlFile.getParentFile(), FileUtils.getNameWithoutExtension(htmlFile)+".xp");
		final PrintWriter xpWriter=new PrintWriter(new FileWriter(xpFile));
		org.htmlparser.Parser parser=new org.htmlparser.Parser();
		parser.setResource(htmlFile.toURI().toString());
		final Stack<String> tags=new Stack<String>();
		parser.visitAllNodesWith(new NodeVisitor()
		{
			@Override
			public void visitTag(org.htmlparser.Tag tag)
			{
				if ("br".equalsIgnoreCase(tag.getTagName()))
				{
					writeLine(xpWriter);
					xpWriter.println("<br/>");
				}
				else if ("hr".equalsIgnoreCase(tag.getTagName()))
				{
					writeLine(xpWriter);
					xpWriter.println("<hr/>");
				}
				else if ("p".equalsIgnoreCase(tag.getTagName()))
				{
					writeLine(xpWriter);
					openTag("p");
				}
				else if ("i".equalsIgnoreCase(tag.getTagName())) line.append("<i>");
				else if ("b".equalsIgnoreCase(tag.getTagName())) line.append("<b>");
				else if ("u".equalsIgnoreCase(tag.getTagName())) line.append("<u>");
				else System.out.println("Html2Xp.visitTag: "+tag.getTagName());
			}

			@Override
			public void visitEndTag(Tag tag)
			{
				if ("p".equalsIgnoreCase(tag.getTagName()))
				{
					writeLine(xpWriter);
					xpWriter.println("</p>");
				}
				if ("i".equalsIgnoreCase(tag.getTagName())) line.append("</i>");
				if ("b".equalsIgnoreCase(tag.getTagName())) line.append("</b>");
				if ("u".equalsIgnoreCase(tag.getTagName())) line.append("</u>");
				super.visitEndTag(tag);
			}

			@Override
			public void visitStringNode(Text string)
			{
				line.append(XMLUtils.toXMLString(StringEscapeUtils.unescapeHtml(string.getText())));
			}

			private void openTag(String tagName)
			{
				line.append("<").append(tagName).append(">");
				tags.push(tagName);
			}

			private void closeTag(String tagName)
			{
				String currentTag=tags.isEmpty() ? null : tags.pop();
				List<String> openTags=new ArrayList<String>();
				while (!tagName.equals(currentTag))
				{
					if (currentTag==null) throw new RuntimeException("Missing opening tag for closing tag </"+tagName+">.");
					line.append("</").append(currentTag).append(">");
					openTags.add(currentTag);
					currentTag=tags.isEmpty() ? null : tags.pop();
				}
				for (String tag : openTags)
				{
					openTag(tag);
				}
			}

		});
		writeLine(xpWriter);
		xpWriter.close();
	}

	private static void writeLine(PrintWriter writer)
	{
		if (line.length()>0)
		{
			String text=line.toString();
			while (text.startsWith("&#160;")) text=text.substring(6);
			while (text.startsWith(" ")) text=text.substring(1);
			while (text.startsWith("&nbsp;")) text=text.substring(6);
			while (text.endsWith("&#160;")) text=text.substring(0, text.length()-6);
			while (text.endsWith(" ")) text=text.substring(0, text.length()-1);
			while (text.endsWith("&nbsp;")) text=text.substring(0, text.length()-6);
			writer.print(text);
			line=new StringBuilder();
		}
	}
}
