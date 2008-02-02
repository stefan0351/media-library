package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.JOptionPane;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Link;

/**
 * @author Stefan Stiller
 */
public class OpenLinkAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public OpenLinkAction(ApplicationFrame frame)
	{
		super(Link.class, "Open in Browser", Icons.getIcon("link.open"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (getObject() instanceof Link)
		{
			try
			{
				WebUtils.openURL(new URL(((Link)getObject()).getUrl()));
			}
			catch (Exception e1)
			{
				JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
