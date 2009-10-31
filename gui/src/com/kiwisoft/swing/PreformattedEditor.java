package com.kiwisoft.swing;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @todo copy'n'paste actions
 * @author Stefan Stiller
 * @since 28.10.2009
 */
public class PreformattedEditor extends JPanel
{
	private JEditorPane textField;

	public static void main(String[] args) throws IOException, BadLocationException
	{
		Icons.setResource("/com/kiwisoft/media/icons/Icons.xml");

		JFrame frame=new JFrame("Preformatted Editor Test");
		PreformattedEditor editor=new PreformattedEditor();
		editor.setText("Test[b]bold[/b][br/]sfasf[sub]123[/sub][sup]234[/sup]");
		frame.setContentPane(editor);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		GuiUtils.centerWindow(null, frame);
		frame.setVisible(true);
	}

	public PreformattedEditor()
	{
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(300, 200));
		textField=new JTextPane();
		textField.setEditorKit(new PreformattedEditorKit());
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK), "font-bold");
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK), "font-italic");
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK), "font-underline");
		JScrollPane scrollPane=new JScrollPane(textField);

		add(createToolBar(), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(scrollPane, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	public void setText(String text) throws IOException, BadLocationException
	{
		textField.setText(text);
	}

	protected JToolBar createToolBar()
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new SetStyleAction("Bold", Icons.getIcon("font.bold"), StyleConstants.Bold));
		actions.add(new SetStyleAction("Italic", Icons.getIcon("font.italic"), StyleConstants.Italic));
		actions.add(new SetStyleAction("Underline", Icons.getIcon("font.underline"), StyleConstants.Underline));
		actions.add(new SetStyleAction("Subscript", Icons.getIcon("font.subscript"), StyleConstants.Subscript));
		actions.add(new SetStyleAction("Superscript", Icons.getIcon("font.superscript"), StyleConstants.Superscript));
		ToolBar bar=new ToolBar(false);
		bar.setFocusableButtons(false);
		bar.initialize(actions);
		return bar;
	}

	private class SetStyleAction extends ContextAction
	{
		private Object attribute;

		protected SetStyleAction(String name, Icon icon, Object attribute)
		{
			super(name, icon);
			this.attribute=attribute;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			StyledEditorKit kit=(StyledEditorKit) textField.getEditorKit();
			MutableAttributeSet attr=kit.getInputAttributes();
			boolean isSet=Boolean.TRUE.equals(attr.getAttribute(attribute));
			SimpleAttributeSet style=new SimpleAttributeSet();
			style.addAttribute(attribute, Boolean.valueOf(!isSet));
			setCharacterAttributes(style);
		}

		/**
		 * Applies the given attributes to character
		 * content.  If there is a selection, the attributes
		 * are applied to the selection range.  If there
		 * is no selection, the attributes are applied to
		 * the input attribute set which defines the attributes
		 * for any new text that gets inserted.
		 *
		 * @param attr	the attributes
		 */
		protected final void setCharacterAttributes(AttributeSet attr)
		{
			int p0=textField.getSelectionStart();
			int p1=textField.getSelectionEnd();
			if (p0!=p1)
			{
				StyledDocument doc=(StyledDocument) textField.getDocument();
				doc.setCharacterAttributes(p0, p1-p0, attr, false);
			}
			StyledEditorKit k=(StyledEditorKit) textField.getEditorKit();
			MutableAttributeSet inputAttributes=k.getInputAttributes();
			inputAttributes.addAttributes(attr);
		}

	}

	private static class PreformattedEditorKit extends StyledEditorKit
	{
		@Override
		public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException
		{
			boolean tag=false;
			String tagName="";
			int ch;
			StyledDocument styledDocument=(StyledDocument) doc;
			Style style=styledDocument.getLogicalStyle(pos);
			while ((ch=in.read())!=-1)
			{
				if (ch=='[')
				{
					tag=true;
					tagName="";
				}
				else if (ch==']')
				{
					tag=false;
					if ("b".equalsIgnoreCase(tagName)) style.addAttribute(StyleConstants.Bold, Boolean.TRUE);
					else if ("/b".equalsIgnoreCase(tagName)) style.removeAttribute(StyleConstants.Bold);
					if ("i".equalsIgnoreCase(tagName)) style.addAttribute(StyleConstants.Italic, Boolean.TRUE);
					else if ("/i".equalsIgnoreCase(tagName)) style.removeAttribute(StyleConstants.Italic);
					if ("u".equalsIgnoreCase(tagName)) style.addAttribute(StyleConstants.Underline, Boolean.TRUE);
					else if ("/u".equalsIgnoreCase(tagName)) style.removeAttribute(StyleConstants.Underline);
					if ("sub".equalsIgnoreCase(tagName)) style.addAttribute(StyleConstants.Subscript, Boolean.TRUE);
					else if ("/sub".equalsIgnoreCase(tagName)) style.removeAttribute(StyleConstants.Subscript);
					if ("sup".equalsIgnoreCase(tagName)) style.addAttribute(StyleConstants.Superscript, Boolean.TRUE);
					else if ("/sup".equalsIgnoreCase(tagName)) style.removeAttribute(StyleConstants.Superscript);
					else if ("br/".equalsIgnoreCase(tagName))
					{
						doc.insertString(pos, "\n", style);
						pos++;
					}
				}
				else if (tag) tagName=tagName+Character.toString((char) ch);
				else
				{
					if (ch=='\n') continue;
					doc.insertString(pos, Character.toString((char) ch), style);
					pos++;
				}
			}
		}
	}
}
