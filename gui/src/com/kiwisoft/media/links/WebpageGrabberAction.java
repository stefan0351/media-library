package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.download.GrabberFrame;

/**
 * @author Stefan Stiller
 */
public class WebpageGrabberAction extends ContextAction
{
	public WebpageGrabberAction()
	{
		super("Webpage Grabber", Icons.getIcon("download"));
	}

	public void actionPerformed(ActionEvent e)
	{
		new GrabberFrame(null).setVisible(true);
	}
}
