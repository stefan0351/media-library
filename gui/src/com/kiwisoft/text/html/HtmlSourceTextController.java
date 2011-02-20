package com.kiwisoft.text.html;

import com.kiwisoft.editor.SourceCodeDocument;
import com.kiwisoft.editor.lexer.HTMLLexer;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.text.TextController;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.IOException;

/**
 * @author Stefan Stiller
 * @since 11.12.2010
 */
public class HtmlSourceTextController extends TextController
{
	public static void main(String[] args) throws IOException, BadLocationException
	{
		Icons.setResource("/com/kiwisoft/media/icons/Icons.xml");

		JFrame frame=new JFrame("HTML Source Editor");
		HtmlSourceTextController editor=new HtmlSourceTextController();
		editor.setText("<p>Line1</p>\n\n<p>Line 2</p>");
		frame.setContentPane(editor.getComponent());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		GuiUtils.centerWindow(null, frame);
		frame.setVisible(true);
	}

	public HtmlSourceTextController()
	{
	}

	@Override
	protected JTextPane createTextField()
	{
		SourceCodeDocument document=new SourceCodeDocument();
		document.setHighlightStyle(HTMLLexer.class);
		return new JTextPane(document);
	}
}
