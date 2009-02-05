package com.kiwisoft.media.dataimport;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DatabaseChangesImportJob;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.progress.ProgressDialog;
import com.kiwisoft.swing.actions.ContextAction;

/**
 * @author Stefan Stiller
 * @since 09.02.2008 13:41:04
 */
public class ImportChangesAction extends ContextAction
{
	private ApplicationFrame frame;

	public ImportChangesAction(ApplicationFrame frame)
	{
		super("Import Changes");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent event)
	{
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		if (JFileChooser.APPROVE_OPTION==fileChooser.showOpenDialog(frame))
		{
			File file=fileChooser.getSelectedFile();
			try
			{
				new ProgressDialog(frame, new DatabaseChangesImportJob(file)).start();
			}
			catch (Exception e)
			{
				GuiUtils.handleThrowable(frame, e);
			}
		}
	}

}
