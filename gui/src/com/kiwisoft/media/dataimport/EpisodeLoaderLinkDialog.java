package com.kiwisoft.media.dataimport;

import static java.awt.GridBagConstraints.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Link;
import com.kiwisoft.media.Language;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

public class EpisodeLoaderLinkDialog extends JDialog
{
	private JTextField showField;
	private JTextField urlField;
	private boolean returnValue;
	private Show show;
	private Link link;
	private String linkName;
	private Language linkLanguage;

	protected EpisodeLoaderLinkDialog(Window frame, Show show, Link link, String linkName, Language linkLanguage)
	{
		super(frame, "Load Episode from TV.com", ModalityType.APPLICATION_MODAL);
		this.show=show;
		this.link=link;
		this.linkName=linkName;
		this.linkLanguage=linkLanguage;
		createContentPanel();
		initializeData();
		pack();
		GuiUtils.centerWindow(frame, this);
	}

	private void initializeData()
	{
		showField.setText(show.getTitle());
		if (link!=null) urlField.setText(link.getUrl());
	}

	private void createContentPanel()
	{
		urlField=new JTextField(40);
		showField=new JTextField(40);
		showField.setEditable(false);

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Show:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(showField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		row++;
		pnlContent.add(new JLabel("URL:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(urlField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new EpisodeLoaderLinkDialog.OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new EpisodeLoaderLinkDialog.CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(pnlContent, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
													 GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(pnlButtons, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0,
													 WEST, HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		setContentPane(panel);

		getRootPane().setDefaultButton(btnOk);
	}

	public boolean isOk()
	{
		return returnValue;
	}

	private boolean apply() throws InvalidDataException
	{
		final String url=urlField.getText();
		if (StringUtils.isEmpty(url)) throw new InvalidDataException("Missing URL!", urlField);
		try
		{
			new URL(url);
		}
		catch (MalformedURLException e)
		{
			throw new InvalidDataException(e.getMessage(), urlField);
		}
		if (link==null || !url.equals(link.getUrl()))
		{
			return DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					if (link==null)
					{
						link=show.getLinkGroup(true).createLink();
						link.setName(linkName);
						link.setLanguage(linkLanguage);
					}
					link.setUrl(url);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(EpisodeLoaderLinkDialog.this, throwable);
				}
			});
		}
		return true;
	}

	public Link getLink()
	{
		return link;
	}

	private class OkAction extends AbstractAction
	{
		public OkAction()
		{
			super("Ok", Icons.getIcon("ok"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				if (apply())
				{
					returnValue=true;
					dispose();
				}
			}
			catch (InvalidDataException e1)
			{
				e1.handle();
			}
			catch (Exception e1)
			{
				GuiUtils.handleThrowable(EpisodeLoaderLinkDialog.this, e1);
			}
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel", Icons.getIcon("cancel"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}

}
