package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class NewVideoAction extends ContextAction<Video>
{
	private MediumType type;

	public NewVideoAction(MediumType type)
	{
		super("New", Icons.getIcon("add"));
		this.type=type;
	}

	public void actionPerformed(ActionEvent e)
	{
		VideoDetailsView.create(type);
	}
}
