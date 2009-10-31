package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.dataimport.SearchPattern;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class SearchPatternDetailsAction extends SimpleContextAction
{
	public SearchPatternDetailsAction()
	{
		super(SearchPattern.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		SearchPatternDetailsView.create((SearchPattern)getObject());
	}

}
