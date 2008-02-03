package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Link;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.WebUtils;

/**
 * @author Stefan Stiller
 */
public class OpenLinkAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public OpenLinkAction(ApplicationFrame frame)
	{
		super(new Class[]{Link.class, LinkNode.class}, "Open in Browser", Icons.getIcon("link.open"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		Object object=getObject();
		if (object instanceof Link) openLink((Link)object);
		else if (object instanceof LinkNode) openLink(((LinkNode)object).getUserObject());
	}

	private void openLink(Link link)
	{
		try
		{
			WebUtils.openURL(new URL(link.getUrl()));
		}
		catch (Exception e1)
		{
			JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
