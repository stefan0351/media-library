/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 3:01:23 PM
 */
package com.kiwisoft.media;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.kiwisoft.media.show.WebDatesExport;
import com.kiwisoft.swing.progress.ProgressDialog;

public class ExportWebDatesAction extends AbstractAction
{
	public ExportWebDatesAction()
	{
		super("Exportiere Internet-Termine");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		new ProgressDialog(null, new WebDatesExport()).start();
	}

}
