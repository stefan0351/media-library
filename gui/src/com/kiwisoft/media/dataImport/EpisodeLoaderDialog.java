package com.kiwisoft.media.dataImport;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.ComponentUtils;
import com.kiwisoft.utils.gui.WindowManager;

public class EpisodeLoaderDialog extends JDialog
{
	private JTextField showField;
	private JTextField urlField;
	private JFormattedTextField firstSeasonField;
	private JFormattedTextField lastSeasonField;
	private boolean returnValue;
	private String url;
	private Integer lastSeason;
	private Integer firstSeason;
	private JCheckBox autoCreateField;
	private boolean autoCreate;

	public EpisodeLoaderDialog(JFrame frame, Show show, String url)
	{
		super(frame, "Load Episode from TV.com", true);
		createContentPanel();
		initializeData(show, url);
		pack();
		WindowManager.arrange(frame, this);
	}

	private void initializeData(Show show, String url)
	{
		showField.setText(show.getTitle());
		urlField.setText(url);
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
		pnlContent.add(new JLabel("TV.com URL:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
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

	private boolean apply()
	{
		url=urlField.getText();
		firstSeason=(Integer)firstSeasonField.getValue();
		lastSeason=(Integer)lastSeasonField.getValue();
		autoCreate=autoCreateField.isSelected();
		return firstSeason!=null && lastSeason!=null && firstSeason<=lastSeason && !StringUtils.isEmpty(url);
	}

	public String getUrl()
	{
		return url;
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
			if (apply())
			{
				returnValue=true;
				dispose();
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
