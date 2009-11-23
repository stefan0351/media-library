package com.kiwisoft.text.preformat;

import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.ToolBar;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
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
public class PreformatTextController
{
	private JEditorPane textField;
	private UndoManager undoManager;
	private JPanel panel;
	private PreformatTextController.UndoAction undoAction;
	private PreformatTextController.RedoAction redoAction;

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

	public JPanel getComponent()
	{
		if (panel==null)
		{
			undoAction=new UndoAction();
			redoAction=new RedoAction();

			panel=new JPanel(new GridBagLayout());
			panel.setPreferredSize(new Dimension(300, 200));
			textField=new JTextPane();
			textField.setEditorKit(new PreformatEditorKit());
			textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK), "font-bold");
			textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK), "font-italic");
			textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK), "font-underline");
			textField.getDocument().addDocumentListener(new DocumentAdapter()
			{
				@Override
				public void changedUpdate(DocumentEvent e)
				{
					super.changedUpdate(e);
					updateUndoActions();
				}
			});

			undoManager=GuiUtils.initializeUndo(textField);

			JScrollPane scrollPane=new JScrollPane(textField);
			panel.add(createToolBar(), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
			panel.add(scrollPane, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
			updateUndoActions();
		}
		return panel;
	}

	private void updateUndoActions()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				undoAction.setEnabled(undoManager.canUndo());
				redoAction.setEnabled(undoManager.canRedo());
			}
		});
	}

	public void setText(String text)
	{
		getComponent(); // Make sure component is initialized
		textField.setText(text);
		undoManager.discardAllEdits();
		updateUndoActions();
	}

	public String getText()
	{
		getComponent(); // Make sure component is initialized
		return textField.getText();
	}

	protected JToolBar createToolBar()
	{
		List<ContextAction> actions=getToolBarActions();
		ToolBar bar=new ToolBar(false);
		bar.setFocusableButtons(false);
		bar.initialize(actions);
		return bar;
	}

	protected List<ContextAction> getToolBarActions()
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new SetStyleAction("Bold", Icons.getIcon("font.bold"), StyleConstants.Bold));
		actions.add(new SetStyleAction("Italic", Icons.getIcon("font.italic"), StyleConstants.Italic));
		actions.add(new SetStyleAction("Underline", Icons.getIcon("font.underline"), StyleConstants.Underline));
		actions.add(new SetStyleAction("Subscript", Icons.getIcon("font.subscript"), StyleConstants.Subscript));
		actions.add(new SetStyleAction("Superscript", Icons.getIcon("font.superscript"), StyleConstants.Superscript));
		actions.add(null);
		actions.add(new CopyAction());
		actions.add(new CutAction());
		actions.add(new PasteAction());
		actions.add(null);
		actions.add(undoAction);
		actions.add(redoAction);
		return actions;
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

	private class CopyAction extends ContextAction
	{
		private CopyAction()
		{
			super("Copy", Icons.getIcon("copy"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			textField.copy();
		}
	}

	private class CutAction extends ContextAction
	{
		private CutAction()
		{
			super("Cut", Icons.getIcon("cut"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			textField.cut();
		}
	}

	private class PasteAction extends ContextAction
	{
		private PasteAction()
		{
			super("Paste", Icons.getIcon("paste"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			textField.paste();
		}
	}

	private class UndoAction extends ContextAction
	{
		private UndoAction()
		{
			super("Undo", Icons.getIcon("undo"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (undoManager.canUndo()) undoManager.undo();
		}
	}

	private class RedoAction extends ContextAction
	{
		private RedoAction()
		{
			super("Redo", Icons.getIcon("redo"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (undoManager.canRedo()) undoManager.redo();
		}
	}

}
