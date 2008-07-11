package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.GuiUtils;
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
				DBLoader.getInstance().importChanges(file);
				JOptionPane.showMessageDialog(frame, "Import of changes completed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (Exception e)
			{
				GuiUtils.handleThrowable(frame, e);
			}
		}
	}

}
