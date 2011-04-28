package com.kiwisoft.utils.websearch;

import com.kiwisoft.swing.ActionField;
import com.kiwisoft.swing.ButtonDialog;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.TextFieldAction;
import com.kiwisoft.swing.actions.ActionConstants;
import com.kiwisoft.swing.icons.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
* @author Stefan Stiller
* @since 20.02.11
*/
public class WebSearchAction extends TextFieldAction
{
	private GoogleSearch search;
	private String searchExpression;

	public WebSearchAction(GoogleSearch search, String searchExpression)
	{
		super(Icons.getIcon("search"), "Search Google");
		this.search=search;
		this.searchExpression=searchExpression;
	}

	@Override
	public void actionPerformed(final ActionField actionField, ActionEvent event)
	{
		try
		{
			final List<WebSearchResult> results=search.search(searchExpression);
			ButtonDialog dialog=new ButtonDialog(SwingUtilities.getWindowAncestor(actionField), "Google Search", true)
			{
				@Override
				protected JComponent createContentPane()
				{
					WebSearchResultPanel resultView=new WebSearchResultPanel()
					{
						@Override
						public void linkSelected(WebSearchResult result)
						{
							actionField.setText(result.getUrl());
							dispose();
						}
					};
					resultView.setResults(results);
					JScrollPane scrollPane=new JScrollPane(resultView);
					scrollPane.setPreferredSize(new Dimension(600, 400));
					return scrollPane;
				}

				@Override
				protected Action[] getActions()
				{
					CancelAction action=new CancelAction("Close");
					action.putValue(ActionConstants.DEFAULT_ACTION, Boolean.TRUE);
					return new Action[]{action};
				}
			};
			dialog.init();
			dialog.pack();
			dialog.setVisible(true);
		}
		catch (Exception e)
		{
			GuiUtils.handleThrowable(actionField, e);
			e.printStackTrace();
		}
	}
}
