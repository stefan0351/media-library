/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 26, 2003
 * Time: 12:16:53 PM
 */
package com.kiwisoft.media.ui;

import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.AbstractAction;

public class DownloadP7Action extends AbstractAction
{
	private MediaManagerFrame frame;
	private Set shows;

	public DownloadP7Action(MediaManagerFrame frame, Set objects)
	{
		this(frame);
		this.shows=objects;
		setEnabled(!objects.isEmpty());
	}

	public DownloadP7Action(MediaManagerFrame frame)
	{
		super("Pro Sieben");
		this.frame=frame;
	}

	public void actionPerformed(final ActionEvent anEvent)
	{
		new DownloadPro7Dialog(frame, shows).show();
	}
}
