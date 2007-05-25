package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import java.util.List;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class NewShowAction extends ContextAction<Show>
{
	public NewShowAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void update(List<? extends Show> objects)
	{
		setEnabled(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		ShowDetailsView.create(null);
	}
}
