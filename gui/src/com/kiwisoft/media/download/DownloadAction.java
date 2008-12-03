package com.kiwisoft.media.download;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;

import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.tree.GenericTreeNode;

public class DownloadAction extends MultiContextAction
{
	private GrabberFrame frame;

	public DownloadAction(GrabberFrame frame)
	{
		super((Class[])null, "Download", Icons.getIcon("download"));
		this.frame=frame;
	}

	@Override
	protected boolean isValid(Object object)
	{
		if (object instanceof GenericTreeNode) object=((GenericTreeNode)object).getUserObject();
		if (object instanceof URL) return true;
		if (object instanceof WebDocument) return ((WebDocument)object).isDownloable();
		return false;
	}

	public void actionPerformed(ActionEvent e)
	{
		GrabberProject project=frame.getProject();
		if (project!=null)
		{
			List objects=getObjects();
			for (Object object : objects)
			{
				WebDocument document=null;
				if (object instanceof GenericTreeNode) object=((GenericTreeNode)object).getUserObject();
				if (object instanceof URL)
				{
					URL url=(URL)object;
					try
					{
						url=GrabberUtils.getRealURL(url);
					}
					catch (Exception e1)
					{
						GuiUtils.handleThrowable(frame, e1);
						continue;
					}
					document=project.getDocumentForURL(url);
					if (document==null) document=project.createDocument(url);
				}
				if (object instanceof WebDocument) document=(WebDocument)object;
				if (document!=null) document.enqueueForDownload();
			}
			GrabberUtils.startAllQueues();
		}
	}
}
