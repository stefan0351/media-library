package com.kiwisoft.media.dataImport;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NONE;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.UIUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.show.Show;

public class TVComDataLoaderDialog extends JDialog
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

	public TVComDataLoaderDialog(JFrame frame, Show show, String url)
	{
		super(frame, "Lade Daten von TV.com", true);
		createContentPanel();
		initializeData(show, url);
		pack();
		WindowManager.arrange(frame, this);
	}

	private void initializeData(Show show, String url)
	{
		showField.setText(show.getName());
		urlField.setText(url);
	}

	private void createContentPanel()
	{
		urlField=new JTextField(40);
		showField=new JTextField(40);
		showField.setEditable(false);
		firstSeasonField=UIUtils.createNumberField(Integer.class, 5, 1, null);
		firstSeasonField.setValue(1);
		lastSeasonField=UIUtils.createNumberField(Integer.class, 5, 1, null);
		lastSeasonField.setValue(1);
		autoCreateField=new JCheckBox("Erzeuge Episode automatisch");

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Serie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(showField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		row++;
		pnlContent.add(new JLabel("TV.com URL:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(urlField, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		row++;
		pnlContent.add(new JLabel("Staffel von:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 0), 0, 0));
		pnlContent.add(firstSeasonField, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 5), 0, 0));
		pnlContent.add(new JLabel("Staffel bis:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 15, 5, 0), 0, 0));
		pnlContent.add(lastSeasonField, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 5), 0, 0));

		row++;
		pnlContent.add(autoCreateField, new GridBagConstraints(1, row, 3, 1, 0.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new TVComDataLoaderDialog.OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new TVComDataLoaderDialog.CancelAction()));

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
			super("Ok", IconManager.getIcon("com/kiwisoft/utils/icons/ok.gif"));
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
			super("Abbrechen", IconManager.getIcon("com/kiwisoft/utils/icons/cancel.gif"));
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}

}
