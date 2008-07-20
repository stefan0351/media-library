package com.kiwisoft.media.pics;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.progress.ProgressDialog;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class ImportPicturesAction extends ContextAction
{
	private ApplicationFrame frame;

	public ImportPicturesAction(ApplicationFrame frame)
	{
		super("Import Pictures");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		String path=MediaConfiguration.getRecentPicturePath();
		if (path==null) path=MediaConfiguration.getRootPath();
		if (path!=null) fileChooser.setCurrentDirectory(new File(path));
		if (fileChooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION)
		{
			final File directory=fileChooser.getSelectedFile();
			MediaConfiguration.setRecentPicturePath(directory.getParent());
			new ProgressDialog(frame, new PicturesImport(directory)).start();
		}
	}
}
