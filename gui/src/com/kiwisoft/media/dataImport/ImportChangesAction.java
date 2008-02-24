package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.utils.StringUtils;

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
			}
			catch (Exception e)
			{
				GuiUtils.handleThrowable(frame, e);
			}
		}
	}

}
