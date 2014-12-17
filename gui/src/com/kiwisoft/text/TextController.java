package com.kiwisoft.text;

import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.ToolBar;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 11.12.2010
 */
public abstract class TextController
{
	protected JEditorPane textField;
	private UndoManager undoManager;
	private JPanel panel;
	private UndoAction undoAction;
	private RedoAction redoAction;

	public JPanel getComponent()
	{
		if (panel==null)
		{
			undoAction=new UndoAction();
			redoAction=new RedoAction();

			panel=new JPanel(new GridBagLayout());
			panel.setPreferredSize(new Dimension(300, 200));
			JEditorPane textField=getTextField();
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
			panel.add(createToolBar(textField), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			SearchBar searchBar=getSearchBar();
			if (searchBar!=null)
			{
				searchBar.setVisible(false);
				panel.add(searchBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			panel.add(scrollPane, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			updateUndoActions();
		}
		return panel;
	}

	private SearchBar searchBar;

	private SearchBar getSearchBar()
	{
		if (searchBar==null) searchBar=new SearchBar(getTextField());
		return searchBar;
	}

	protected abstract JEditorPane createTextField();

	public JEditorPane getTextField()
	{
		if (textField==null)
		{
			textField=createTextField();
			textField.setHighlighter(new ReverseDefaultHighlighter());
		}
		return textField;
	}

	public Document getDocument()
	{
		return getTextField().getDocument();
	}

	private void updateUndoActions()
	{
		if (undoManager!=null)
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
	}

	public void setText(String text)
	{
		JEditorPane textField=getTextField();
		int caretPosition=textField.getCaretPosition();
		textField.setText(text);
		textField.setCaretPosition(Math.min(caretPosition, textField.getDocument().getLength()));
		if (undoManager!=null) undoManager.discardAllEdits();
		updateUndoActions();
	}

	public String getText()
	{
		return getTextField().getText();
	}

	protected JToolBar createToolBar(JEditorPane textField)
	{
		List<ContextAction> actions=getToolBarActions();
		for (ContextAction action : actions)
		{
			if (action!=null)
			{
				KeyStroke keyStroke=(KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
				if (keyStroke!=null)
				{
					Object mapKey=new Object();
					textField.getInputMap().put(keyStroke, mapKey);
					textField.getActionMap().put(mapKey, action);
				}
			}
		}


		ToolBar bar=new ToolBar(false);
		bar.setFocusableButtons(false);
		bar.add(actions);
		return bar;
	}

	protected List<ContextAction> getToolBarActions()
	{
		List<ContextAction> actions=new ArrayList<ContextAction>();
		actions.add(new CopyAction());
		actions.add(new CutAction());
		actions.add(new PasteAction());
		actions.add(null);
		actions.add(new SearchAction());
		actions.add(null);
		actions.add(undoAction);
		actions.add(redoAction);
		return actions;
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

	private class SearchAction extends ContextAction
	{
		private SearchAction()
		{
			super("Search...", Icons.getIcon("search"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			getSearchBar().setVisible(true);
			String searchText="<P>";
			try
			{
				Document document=getDocument();
				String text=document.getText(0, document.getLength());
				int position=text.indexOf(searchText, getTextField().getCaretPosition());
				if (position>=0)
				{
					getTextField().select(position, position+searchText.length());
					getTextField().requestFocus();
				}
			}
			catch (BadLocationException e1)
			{
				e1.printStackTrace();
			}
		}
	}

}
