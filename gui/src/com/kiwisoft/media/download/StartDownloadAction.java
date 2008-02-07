package com.kiwisoft.media.download;

import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.AbstractAction;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.actions.MultiContextAction;

public class StartDownloadAction extends MultiContextAction
{
	private DownloadProject project;

	public StartDownloadAction(DownloadProject project)
	{
		super(null, "Download", Icons.getIcon("play"));
		this.project=project;
	}

	@Override
	protected boolean isValid(Object object)
	{
		WebDocument document=getDocument(object);
		return document!=null && document.isDownloable();
	}

	private WebDocument getDocument(Object object)
	{
		WebDocument document=null;
		if (object instanceof WebDocument) document=(WebDocument)object;
		else if (object instanceof DocumentNode) document=((DocumentNode)object).getUserObject();
		return document;
	}

	public void actionPerformed(ActionEvent e)
	{
		List<WebDocument> documents=new ArrayList<WebDocument>();
		for (Object o : getObjects())
		{
			WebDocument document=getDocument(o);
			if (document!=null) documents.add(document);
		}
		for (WebDocument document : documents) document.enqueueForDownload();
		project.start();		
	}
}
