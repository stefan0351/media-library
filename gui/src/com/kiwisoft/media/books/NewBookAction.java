package com.kiwisoft.media.books;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.video.MediumType;
import com.kiwisoft.media.video.VideoDetailsView;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class NewBookAction extends ContextAction<Object>
{
	public NewBookAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		BookDetailsView.create(null);
	}
}
