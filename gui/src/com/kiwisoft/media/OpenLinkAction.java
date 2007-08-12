package com.kiwisoft.media;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.JOptionPane;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class OpenLinkAction extends SimpleContextAction<Link>
{
	private ApplicationFrame frame;

	public OpenLinkAction(ApplicationFrame frame)
	{
		super("Open in Browser", Icons.getIcon("link.open"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (getObject()!=null)
		{
			try
			{
				WebUtils.openURL(new URL(getObject().getUrl()));
			}
			catch (Exception e1)
			{
				JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
