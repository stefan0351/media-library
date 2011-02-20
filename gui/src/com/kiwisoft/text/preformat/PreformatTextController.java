package com.kiwisoft.text.preformat;

import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.ToolBar;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.text.TextController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.undo.UndoManager;
import javax.swing.text.*;
import java.awt.*;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Stiller
 * @todo move to utils
 * @todo enable/disable actions
 * @since 28.10.2009
 */
public class PreformatTextController extends TextController
{
	public static void main(String[] args) throws IOException, BadLocationException
	{
		Icons.setResource("/com/kiwisoft/media/icons/Icons.xml");

		JFrame frame=new JFrame("Preformatted Editor Test");
		PreformatTextController editor=new PreformatTextController();
		editor.setText("Line1 [b]bold[/b][br/]Line2\nStill Line 2[br/][br/]Line [em]3[/em]");
		frame.setContentPane(editor.getComponent());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		GuiUtils.centerWindow(null, frame);
		frame.setVisible(true);
	}

	public PreformatTextController()
	{
	}

	@Override
	protected JTextPane createTextField()
	{
		final JTextPane textField=new JTextPane();
		textField.setEditorKit(new PreformatEditorKit());
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK), "font-bold");
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK), "font-italic");
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK), "font-underline");
		textField.addCaretListener(new CaretListener()
		{
			@Override
			public void caretUpdate(CaretEvent e)
			{
				boolean selection=e.getDot()!=e.getMark();
				for (SetStyleAction styleAction : styleActions)
				{
					styleAction.setEnabled(selection);
				}
			}
		});
		return textField;
	}

	private List<SetStyleAction> styleActions;

	private List<SetStyleAction> getStyleActions()
	{
		if (styleActions==null)
		{
			styleActions=new ArrayList<SetStyleAction>();
			styleActions.add(new SetStyleAction("Bold", Icons.getIcon("font.bold"), StyleConstants.Bold));
			styleActions.add(new SetStyleAction("Italic", Icons.getIcon("font.italic"), StyleConstants.Italic));
			styleActions.add(new SetStyleAction("Underline", Icons.getIcon("font.underline"), StyleConstants.Underline));
			styleActions.add(new SetStyleAction("Subscript", Icons.getIcon("font.subscript"), StyleConstants.Subscript));
			styleActions.add(new SetStyleAction("Superscript", Icons.getIcon("font.superscript"), StyleConstants.Superscript));
		}
		return styleActions;
	}

	@Override
	protected List<ContextAction> getToolBarActions()
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.addAll(getStyleActions());
		actions.add(null);
		actions.addAll(super.getToolBarActions());
		return actions;
	}

	private class SetStyleAction extends ContextAction
	{
		private Object attribute;

		protected SetStyleAction(String name, Icon icon, Object attribute)
		{
			super(name, icon);
			this.attribute=attribute;
			setEnabled(false);
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
		 * @param attr the attributes
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
}
