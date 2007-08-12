package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.ImageFileChooser;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.progress.SmallProgressDialog;

public class AddPhotoAction extends ContextAction<Object>
{
	private ApplicationFrame frame;
	private PhotoGallery photoGallery;

	public AddPhotoAction(ApplicationFrame frame, PhotoGallery photoGallery)
	{
		super("Add", Icons.getIcon("add"));
		this.frame=frame;
		this.photoGallery=photoGallery;
	}

	public void actionPerformed(ActionEvent e)
	{
		ImageFileChooser fileChooser=new ImageFileChooser();
		String path=MediaConfiguration.getRecentPhotoPath();
		if (StringUtils.isEmpty(path)) path=MediaConfiguration.getRootPath();
		if (!StringUtils.isEmpty(path)) fileChooser.setCurrentDirectory(new File(path));
		fileChooser.setMultiSelectionEnabled(true);
		if (JFileChooser.APPROVE_OPTION==fileChooser.showOpenDialog(frame))
		{
			File[] files=fileChooser.getSelectedFiles();
			MediaConfiguration.setRecentPhotoPath(files[0].getParent());
			new SmallProgressDialog(frame, new PhotoImportJob(photoGallery, files)).start();
		}
	}
}
