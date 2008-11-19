package com.kiwisoft.media.files;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.swing.ImageFileChooser;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.utils.FileUtils;

/**
 * @author Stefan Stiller
 */
public class NewMediaFileAction extends ContextAction
{
	private ApplicationFrame frame;

	public NewMediaFileAction(ApplicationFrame frame)
	{
		super("Add", Icons.getIcon("add"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		ImageFileChooser fileChooser=new ImageFileChooser();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Video Files", "avi", "mp4", "mpg", "mpeg", "mov", "flv", "mkv"));
		fileChooser.setAcceptAllFileFilterUsed(true); // This selects the all files filter and moves it to the end
		String path=MediaConfiguration.getRecentMediaPath();
		if (path==null) path=MediaConfiguration.getRootPath();
		if (path!=null) fileChooser.setCurrentDirectory(new File(path));
		if (fileChooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION)
		{
			File file=fileChooser.getSelectedFile();
			MediaConfiguration.setRecentMediaPath(file.getParent());
			MediaFileInfo fileInfo=MediaFileUtils.getMediaFileInfo(file);
			if (fileInfo.isImage()) ImageDetailsView.createDialog(frame, FileUtils.getNameFromFile(file), file);
			else if (fileInfo.isVideo()) VideoDetailsView.createDialog(frame, FileUtils.getNameFromFile(file), file);
		}
	}
}
