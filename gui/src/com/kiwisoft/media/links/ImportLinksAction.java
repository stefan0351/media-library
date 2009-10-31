package com.kiwisoft.media.links;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.download.ParserFactory;
import com.kiwisoft.media.download.Parser;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

/**
 * @author Stefan Stiller
 */
public class ImportLinksAction extends ContextAction
{
	private ApplicationFrame frame;

	public ImportLinksAction(ApplicationFrame frame)
	{
		super("Import", Icons.getIcon("import"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION)
		{
			File file=fileChooser.getSelectedFile();
			String mimeType=FileUtils.getMimeType(file);
			Parser parser=ParserFactory.getParser(mimeType);
			if (parser!=null)
			{
				final List<URL> links=new ArrayList<URL>();
				try
				{
					parser.parse(file, null, new ArrayList<URL>(), links);
					if (!links.isEmpty())
					{
						final Linkable linkable=SelectLinkableView.createDialog(frame);
						if (linkable!=null)
						{
							DBSession.execute(new Transactional()
							{
								@Override
								public void run() throws Exception
								{
									for (URL url : links)
									{
										Link link=linkable.getLinkGroup(true).createLink();
										link.setName(url.toString());
										link.setUrl(url.toString());
									}
								}

								@Override
								public void handleError(Throwable throwable, boolean rollback)
								{
									GuiUtils.handleThrowable(frame, throwable);
								}
							});
						}
					}
				}
				catch (IOException e1)
				{
					GuiUtils.handleThrowable(frame, e1);
				}
			}
		}
	}
}
