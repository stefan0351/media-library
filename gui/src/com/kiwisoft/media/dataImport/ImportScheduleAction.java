/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 3:41:18 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.swing.progress.ProgressDialog;

public class ImportScheduleAction extends AbstractAction
{
	private JFrame parent;

	public ImportScheduleAction(JFrame frame)
	{
		super("Import Schedule");
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		String[] values=ImportPathDialog.create(parent, MediaConfiguration.getRecentSchedulePath(), "*.xml");
		if (values!=null)
		{
			AirdateImport airdateImport=new AirdateImport(values[0], values[1]);
			ProgressDialog dialog=new ProgressDialog(parent, airdateImport);
			airdateImport.setDialog(dialog);
			dialog.show();
		}
	}
}
