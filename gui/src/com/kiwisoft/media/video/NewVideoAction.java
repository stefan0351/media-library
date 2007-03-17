package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.ContextAction;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 22:28:53
 * To change this template use File | Settings | File Templates.
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
