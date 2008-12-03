package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.net.URL;
import java.io.IOException;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.media.download.GrabberFrame;
import com.kiwisoft.media.download.GrabberProject;
import com.kiwisoft.media.download.GrabberUtils;
import com.kiwisoft.media.Link;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class GrabPageAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public GrabPageAction(ApplicationFrame frame)
	{
		super(new Class[]{LinkNode.class, Link.class}, "Grab Page", Icons.getIcon("download"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		List objects=getObjects();
		Set<URL> urls=new HashSet<URL>();
		for (Object object : objects)
		{
			if (object instanceof LinkNode) object=((LinkNode)object).getUserObject();
			if (object instanceof Link)
			{
				try
				{
					URL url=GrabberUtils.getRealURL(new URL(((Link)object).getUrl()));
					urls.add(url);
				}
				catch (Exception e1)
				{
					GuiUtils.handleThrowable(frame, e1);
				}
			}
		}
		if (!urls.isEmpty())
		{
			GrabberProject project=new GrabberProject();
			for (URL url : urls)
			{
				project.createDocument(url);
			}
			new GrabberFrame(project).setVisible(true);
		}
	}
}
