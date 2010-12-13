package com.kiwisoft.text;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.SwingListenerSupport;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 12.12.2010
 */
public class SearchBar extends JToolBar
{
	private JTextField expressionField;
	private FindNextAction nextAction;
	private FindPreviousAction previousAction;
	private HideAction hideAction;
	private JCheckBox regExpField;
	private JCheckBox caseSensitiveField;
	private JTextComponent textComponent;
	private Color textComponentBackground;
	private ReverseDefaultHighlighter.DefaultHighlightPainter highlightPainter;
	private List<Highlighter.Highlight> matches;
	private int selectedMatch=-1;
	private JLabel messageLabel;
	private SwingListenerSupport listeners=new SwingListenerSupport();

	public SearchBar(JTextComponent textComponent)
	{
		this.textComponent=textComponent;
		this.textComponentBackground=textComponent.getBackground();

		setFloatable(false);
		matches=new ArrayList<Highlighter.Highlight>();

		initComponents();
	}

	private void initListeners()
	{
		listeners.installDocumentListener(expressionField, new DocumentAdapter()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				highlightMatches();
			}
		});
		ItemListener changeListener=new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				highlightMatches();
			}
		};
		listeners.installItemListener(regExpField, changeListener);
		listeners.installItemListener(caseSensitiveField, changeListener);
	}

	@Override
	public void setVisible(boolean aFlag)
	{
		if (aFlag)
		{
			initListeners();
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					expressionField.requestFocus();
				}
			});
		}
		else
		{
			listeners.dispose();
			reset();
			expressionField.setText(null);
			expressionField.setBackground(textComponentBackground);
		}
		super.setVisible(aFlag);
	}

	private void initComponents()
	{
		nextAction=new FindNextAction();
		previousAction=new FindPreviousAction();
		hideAction=new HideAction();

		highlightPainter=new ReverseDefaultHighlighter.DefaultHighlightPainter(new Color(200, 255, 200));

		expressionField=new JTextField(20);
		int height=expressionField.getPreferredSize().height;
		expressionField.setMinimumSize(new Dimension(100, height));
		expressionField.setPreferredSize(new Dimension(200, height));
		expressionField.setMaximumSize(new Dimension(200, height));
		expressionField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "next-match");
		expressionField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "prev-match");
		expressionField.getActionMap().put("next-match", nextAction);
		expressionField.getActionMap().put("prev-match", previousAction);

		regExpField=new JCheckBox("Regular expression");
		regExpField.setOpaque(false);
		regExpField.setFocusable(false);

		caseSensitiveField=new JCheckBox("Case sensitive");
		caseSensitiveField.setOpaque(false);
		caseSensitiveField.setFocusable(false);

		messageLabel=new JLabel();

		add(expressionField);
		addSeparator();
		add(nextAction);
		add(previousAction);
		addSeparator();
		add(caseSensitiveField);
		add(regExpField);
		add(Box.createHorizontalGlue());
		add(messageLabel);
		add(Box.createHorizontalStrut(4));
		add(hideAction);
	}

	private void highlightMatches()
	{
		reset();

		String expression=expressionField.getText();
		expressionField.setBackground(textComponentBackground);
		if (expression!=null && expression.length()>0)
		{
			Pattern pattern;
			try
			{
				int flags=0;
				if (!caseSensitiveField.isSelected()) flags|=Pattern.CASE_INSENSITIVE;
				if (!regExpField.isSelected()) flags|=Pattern.LITERAL;
				pattern=Pattern.compile(expression, flags);
			}
			catch (Exception e)
			{
				expressionField.setBackground(new Color(255, 200, 200));
				messageLabel.setText("Invalid regular expression");
				return;
			}
			try
			{
				Document document=textComponent.getDocument();
				Matcher matcher=pattern.matcher(document.getText(0, document.getLength()));
				int position=0;
				while (matcher.find(position) && matches.size()<101)
				{
					if (matcher.end()>matcher.start())
						matches.add((Highlighter.Highlight) textComponent.getHighlighter().addHighlight(matcher.start(), matcher.end(), highlightPainter));
					position=matcher.end();
				}
				if (matches.size()>100) messageLabel.setText("More than 100 matches");
				else if (matches.size()>1) messageLabel.setText(matches.size()+" matches");
				else if (matches.size()==1) messageLabel.setText("1 match");
				else messageLabel.setText("No matches");
				selectNext();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	private void reset()
	{
		for (Highlighter.Highlight match : matches)
		{
			textComponent.getHighlighter().removeHighlight(match);
		}
		matches.clear();
		selectedMatch=-1;
		messageLabel.setText("");
	}

	private void selectNext()
	{
		selectedMatch=Math.min(selectedMatch+1, matches.size()-1);
		if (selectedMatch>=0)
		{
			Highlighter.Highlight highlight=matches.get(selectedMatch);
			textComponent.select(highlight.getStartOffset(), highlight.getEndOffset());
			textComponent.getCaret().setSelectionVisible(true);
		}
	}

	private void selectPrevious()
	{
		selectedMatch=Math.max(selectedMatch-1, 0);
		if (selectedMatch>=0)
		{
			Highlighter.Highlight highlight=matches.get(selectedMatch);
			textComponent.select(highlight.getStartOffset(), highlight.getEndOffset());
			textComponent.getCaret().setSelectionVisible(true);
		}
	}

	@Override
	protected JButton createActionComponent(Action a)
	{
		JButton button=super.createActionComponent(a);
		button.setHorizontalTextPosition(SwingConstants.TRAILING);
		button.setHideActionText(true);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFocusPainted(true);
		button.setFocusable(false);
		return button;
	}

	private class HideAction extends ContextAction
	{

		private HideAction()
		{
			super("Hide", Icons.getIcon("cancel"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}

	}

	private class FindNextAction extends ContextAction
	{
		private FindNextAction()
		{
			super("Next", Icons.getIcon("move.down"));
			//setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			selectNext();
		}
	}

	private class FindPreviousAction extends ContextAction
	{
		private FindPreviousAction()
		{
			super("Previous", Icons.getIcon("move.up"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			selectPrevious();
		}
	}
}
