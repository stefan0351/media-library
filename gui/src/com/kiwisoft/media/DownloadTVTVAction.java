/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 26, 2003
 * Time: 12:16:39 PM
 */
package com.kiwisoft.media;

import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.AbstractAction;

public class DownloadTVTVAction extends AbstractAction
{
	private MediaManagerFrame frame;
	private Set shows;

	public DownloadTVTVAction(MediaManagerFrame frame, Set shows)
	{
		this(frame);
		this.shows=shows;
		setEnabled(!shows.isEmpty());
	}

	public DownloadTVTVAction(MediaManagerFrame frame)
	{
		super("TVTV");
		this.frame=frame;
	}

	public void actionPerformed(final ActionEvent anEvent)
	{
		new DownloadTVTVDialog(frame, shows).show();
	}
}
