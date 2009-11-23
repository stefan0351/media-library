package com.kiwisoft.text.preformat;

import com.kiwisoft.utils.parser.Token;

import javax.swing.text.*;
import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Stefan Stiller
* @since 15.11.2009
*/
public class PreformatEditorKit extends StyledEditorKit
{
	@Override
	public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException
	{
		StyledDocument styledDocument=(StyledDocument) doc;
		Style style=styledDocument.getLogicalStyle(pos);
		String tagName;
		PreformatLexer lexer=new PreformatLexer(in);
		Token token;
		boolean whiteSpace=true;
		while ((token=lexer.getNextToken())!=null)
		{
			switch (token.getId())
			{
				case PreformatLexer.WHITE_SPACE:
					if (!whiteSpace)
					{
						doc.insertString(pos, " ", style);
						pos++;
					}
					whiteSpace=true;
					break;
				case PreformatLexer.TEXT:
					doc.insertString(pos, token.getContents(), style);
					pos+=token.getContents().length();
					whiteSpace=false;
					break;
				case PreformatLexer.OPENING_TAG:
					tagName=token.getContents().substring(1, token.getContents().length()-1).trim().toLowerCase();
					if ("b".equalsIgnoreCase(tagName)) StyleConstants.setBold(style, true);
					else if ("i".equalsIgnoreCase(tagName)) StyleConstants.setItalic(style, true);
					else if ("em".equalsIgnoreCase(tagName)) StyleConstants.setItalic(style, true);
					else if ("u".equalsIgnoreCase(tagName)) StyleConstants.setUnderline(style, true);
					else if ("sub".equalsIgnoreCase(tagName)) StyleConstants.setSubscript(style, true);
					else if ("sup".equalsIgnoreCase(tagName)) StyleConstants.setSuperscript(style, true);
					else if ("br".equalsIgnoreCase(tagName))
					{
						doc.insertString(pos, "\n", style);
						pos++;
						whiteSpace=true;
					}
					break;
				case PreformatLexer.CLOSING_TAG:
					tagName=token.getContents().substring(2, token.getContents().length()-1).trim().toLowerCase();
					if ("b".equalsIgnoreCase(tagName)) StyleConstants.setBold(style, false);
					else if ("i".equalsIgnoreCase(tagName)) StyleConstants.setItalic(style, false);
					else if ("em".equalsIgnoreCase(tagName)) StyleConstants.setItalic(style, false);
					else if ("u".equalsIgnoreCase(tagName)) StyleConstants.setUnderline(style, false);
					else if ("sub".equalsIgnoreCase(tagName)) StyleConstants.setSubscript(style, false);
					else if ("sup".equalsIgnoreCase(tagName)) StyleConstants.setSuperscript(style, false);
					break;
				case PreformatLexer.EMPTY_TAG:
					tagName=token.getContents().substring(1, token.getContents().length()-2).trim().toLowerCase();
					if ("br".equalsIgnoreCase(tagName))
					{
						doc.insertString(pos, "\n", style);
						pos++;
						whiteSpace=true;
					}
					break;
			}
		}
	}

	@Override
	public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException
	{
		if (doc instanceof StyledDocument)
		{
			PreformatWriter w = new PreformatWriter(out, (StyledDocument) doc, pos, len);
			w.write();
		}
		else
		{
			super.write(out, doc, pos, len);
		}
	}
}
