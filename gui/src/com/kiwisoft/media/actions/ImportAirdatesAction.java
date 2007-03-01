/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 3:41:18 PM
 */
package com.kiwisoft.media.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.kiwisoft.media.dataImport.AirdateImport;
import com.kiwisoft.media.dataImport.ImportPathDialog;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

public class ImportAirdatesAction extends AbstractAction
{
	private JFrame parent;

	public ImportAirdatesAction(JFrame frame)
	{
		super("Importiere Termine");
		parent=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		String[] values=ImportPathDialog.create(parent, Configurator.getInstance().getString("path.import", ""), "*.xml");
		if (values!=null)
		{
			AirdateImport airdateImport=new AirdateImport(values[0], values[1]);
			ProgressDialog dialog=new ProgressDialog(parent, airdateImport);
			airdateImport.setDialog(dialog);
			dialog.show();
		}
	}
}
