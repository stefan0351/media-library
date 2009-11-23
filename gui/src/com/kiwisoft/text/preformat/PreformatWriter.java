package com.kiwisoft.text.preformat;

import javax.swing.text.*;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

/**
 * @author Stefan Stiller
 * @since 15.11.2009
 */
public class PreformatWriter extends AbstractWriter
{
	private Stack<String> fontStack;

	public PreformatWriter(Writer writer, StyledDocument document, int pos, int len)
	{
		super(writer, document, pos, len);
	}

	@Override
	public void write() throws IOException, BadLocationException
	{
		if (getEndOffset()>getStartOffset())
		{

			fontStack=new Stack<String>();
			ElementIterator it=getElementIterator();
			Element element;
			while ((element=it.next())!=null)
			{
				if (!inRange(element)) continue;
				if (element instanceof AbstractDocument.LeafElement)
				{
					writeTags(element.getAttributes());
					String text=getText(element);
					if ((text.length()>0) && (text.charAt(text.length()-1)==NEWLINE))
					{
						text=text.substring(0, text.length()-1);
						write(text);
						write("[br/]");
					}
					else write(text);
				}
			}
			while (!fontStack.isEmpty()) write("[/"+fontStack.pop()+"]");
		}
	}

	private void writeTags(AttributeSet style) throws IOException
	{
		if (!fontStack.isEmpty())
		{
			// find outer most font tag to be closed
			int i=0;
			while (i<fontStack.size())
			{
				String tagName=fontStack.get(i);
				if ("b".equals(tagName) && !StyleConstants.isBold(style)) break;
				if ("i".equals(tagName) && !StyleConstants.isItalic(style)) break;
				if ("u".equals(tagName) && !StyleConstants.isUnderline(style)) break;
				if ("sub".equals(tagName) && !StyleConstants.isSubscript(style)) break;
				if ("sup".equals(tagName) && !StyleConstants.isSuperscript(style)) break;
				i++;
			}
			// close all font tags
			while (fontStack.size()>i) write("[/"+fontStack.pop()+"]");
		}
		writeStartTag("sup", StyleConstants.isSuperscript(style));
		writeStartTag("sub", StyleConstants.isSubscript(style));
		writeStartTag("u", StyleConstants.isUnderline(style));
		writeStartTag("i", StyleConstants.isItalic(style));
		writeStartTag("b", StyleConstants.isBold(style));
	}

	private void writeStartTag(String tag, boolean enable)  throws IOException
	{
		if (enable && !fontStack.contains(tag))
		{
			write("["+tag+"]");
			fontStack.push(tag);
		}
	}
}
