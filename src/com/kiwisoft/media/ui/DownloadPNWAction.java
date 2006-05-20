/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 26, 2003
 * Time: 12:16:05 PM
 */
package com.kiwisoft.media.ui;

import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.AbstractAction;

public class DownloadPNWAction extends AbstractAction
{
	private MediaManagerFrame frame;
	private Set shows;

	public DownloadPNWAction(MediaManagerFrame frame, Set shows)
	{
		this(frame);
		this.shows=shows;
		setEnabled(!shows.isEmpty());
	}

	public DownloadPNWAction(MediaManagerFrame frame)
	{
		super("Prisma Net World");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		new DownloadPrismaOnlineDialog(frame, shows).show();
	}
}
