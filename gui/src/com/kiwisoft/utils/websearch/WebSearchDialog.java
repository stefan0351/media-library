package com.kiwisoft.utils.websearch;

import com.kiwisoft.swing.ButtonDialog;
import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ActionConstants;
import com.kiwisoft.swing.lookup.HistoryLookupField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 20.02.11
 */
public class WebSearchDialog extends ButtonDialog
{
	private GoogleSearch search;
	private HistoryLookupField searchForField;
	private JButton searchForButton;
	private WebSearchResultPanel resultPanel;
	private WebSearchResult result;

	public WebSearchDialog(Window parent, String title, GoogleSearch search)
	{
		super(parent, title, true);
		this.search=search;
		init();
	}

	@Override
	protected JComponent createContentPane()
	{
		searchForField=new HistoryLookupField("WebSearchDialog.searchFor", 25);
		searchForButton=new JButton("Search");
		searchForButton.setEnabled(false);
		resultPanel=new WebSearchResultPanel()
		{
			@Override
			public void linkSelected(WebSearchResult result)
			{
				WebSearchDialog.this.result=result;
				dispose();
			}
		};

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(600, 400));
		panel.add(new JLabel("Search for:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(searchForField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		panel.add(searchForButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(new JScrollPane(resultPanel), new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 0));

		SearchActionListener actionListener=new SearchActionListener();
		searchForField.addActionListener(actionListener);
		searchForButton.addActionListener(actionListener);
		searchForField.getTextField().getDocument().addDocumentListener(new DocumentAdapter()
		{
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				String text=searchForField.getText();
				searchForButton.setEnabled(text!=null && text.trim().length()>2);
			}
		});
		return panel;
	}

	@Override
	public Action[] getActions()
	{
		CancelAction cancelAction=new CancelAction("Close");
		cancelAction.putValue(ActionConstants.DEFAULT_ACTION, Boolean.TRUE);
		return new Action[]{cancelAction};
	}

	public WebSearchResult getResult()
	{
		return result;
	}

	private class SearchActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			String text=searchForField.getText();
			if (text!=null && text.trim().length()>2)
			{
				try
				{
					List<WebSearchResult> results=search.search(text);
					resultPanel.setResults(results);
				}
				catch (Exception e)
				{
					GuiUtils.handleThrowable(searchForField, e);
				}
			}
		}
	}
}
