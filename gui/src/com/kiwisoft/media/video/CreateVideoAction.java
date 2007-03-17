package com.kiwisoft.media.video;

import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.Icons;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 18:36:30
 * To change this template use File | Settings | File Templates.
 */
public class CreateVideoAction<T extends Recordable> extends MultiContextAction<T>
{
	public CreateVideoAction()
	{
		super("Create Video", Icons.getIcon("video.add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		VideoDetailsView.create(getObjects());
	}
}
