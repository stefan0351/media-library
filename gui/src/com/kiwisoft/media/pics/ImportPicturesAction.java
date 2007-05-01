package com.kiwisoft.media.pics;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

/**
 * @author Stefan Stiller
 */
public class ImportPicturesAction extends ContextAction<Picture>
{
	private ApplicationFrame frame;

	public ImportPicturesAction(ApplicationFrame frame)
	{
		super("Import");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		String path=Configurator.getInstance().getString("path.pictures.recent", null);
		if (path==null) path=Configurator.getInstance().getString("path.root");
		if (path!=null) fileChooser.setCurrentDirectory(new File(path));
		if (fileChooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION)
		{
			final File directory=fileChooser.getSelectedFile();
			Configurator.getInstance().setString("path.pictures.recent", directory.getParent());
			new ProgressDialog(frame, new PicturesImport(directory)).setVisible(true);
		}
	}
}
