package com.kiwisoft.media.dataImport;

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
import com.kiwisoft.swing.ComponentUtils;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;

public abstract class EpisodeLoaderDialog extends JDialog
{
	private JTextField showField;
	private JTextField urlField;
	private JFormattedTextField firstSeasonField;
	private JFormattedTextField lastSeasonField;
	private boolean returnValue;
	private Integer lastSeason;
	private Integer firstSeason;
	private JCheckBox autoCreateField;
	private boolean autoCreate;
	private Show show;
	private Link link;

	protected EpisodeLoaderDialog(Window frame, Show show, Link link)
	{
		super(frame, "Load Episode from TV.com", ModalityType.APPLICATION_MODAL);
		this.show=show;
		this.link=link;
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
		firstSeasonField=ComponentUtils.createNumberField(Integer.class, 5, 1, null);
		firstSeasonField.setValue(1);
		lastSeasonField=ComponentUtils.createNumberField(Integer.class, 5, 1, null);
		lastSeasonField.setValue(1);
		autoCreateField=new JCheckBox("Create Episodes automatically");

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Show:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(showField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		row++;
		pnlContent.add(new JLabel("URL:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(urlField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		row++;
		pnlContent.add(new JLabel("Season from:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(firstSeasonField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 5), 0, 0));
		pnlContent.add(new JLabel("Season to:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 15, 5, 0), 0, 0));
		pnlContent.add(lastSeasonField, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 5), 0, 0));

		row++;
		pnlContent.add(autoCreateField, new GridBagConstraints(1, row, 3, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new EpisodeLoaderDialog.OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new EpisodeLoaderDialog.CancelAction()));

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
		firstSeason=(Integer)firstSeasonField.getValue();
		if (firstSeason==null) throw new InvalidDataException("Missing season from!", firstSeasonField);
		lastSeason=(Integer)lastSeasonField.getValue();
		if (lastSeason==null) throw new InvalidDataException("Missing season to!", lastSeasonField);
		if (lastSeason<firstSeason) throw new InvalidDataException("Season from must be greater or equal than season to!", lastSeasonField);
		autoCreate=autoCreateField.isSelected();

		if (link==null || !url.equals(link.getUrl()))
		{
			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					if (link==null)
					{
						link=show.getLinkGroup(true).createLink();
						link.setName(getLinkName());
						link.setLanguage(getLinkLanguage());
					}
					link.setUrl(url);
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(EpisodeLoaderDialog.this, throwable);
				}
			});
		}
		return true;
	}

	protected abstract String getLinkName();

	protected abstract Language getLinkLanguage();

	public Link getLink()
	{
		return link;
	}

	public Integer getLastSeason()
	{
		return lastSeason;
	}

	public Integer getFirstSeason()
	{
		return firstSeason;
	}

	public boolean isAutoCreate()
	{
		return autoCreate;
	}

	private class OkAction extends AbstractAction
	{
		public OkAction()
		{
			super("Ok", Icons.getIcon("ok"));
		}

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
				GuiUtils.handleThrowable(EpisodeLoaderDialog.this, e1);
			}
		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel", Icons.getIcon("cancel"));
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}

}