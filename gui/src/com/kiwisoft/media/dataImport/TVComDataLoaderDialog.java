package com.kiwisoft.media.dataImport;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.media.show.Show;

public class TVComDataLoaderDialog extends JDialog
{
	private JTextField tfShow;
	private JTextField tfUrl;
	private boolean value;

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
		tfShow.setText(show.getName());
		tfUrl.setText(url);
	}

	private void createContentPanel()
	{
		tfUrl=new JTextField(40);
		tfShow=new JTextField(40);

		JPanel pnlContent=new JPanel(new GridBagLayout());
		pnlContent.add(new JLabel("Show:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
																	 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pnlContent.add(tfShow, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
		pnlContent.add(new JLabel("TV.com URL:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
																	 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pnlContent.add(tfUrl, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
														GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new TVComDataLoaderDialog.OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new TVComDataLoaderDialog.CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.add(pnlContent, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
													 GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(pnlButtons, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0,
													 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		setContentPane(panel);

		getRootPane().setDefaultButton(btnOk);
	}

	private boolean apply()
	{
		return true;
	}

	public boolean getValue()
	{
		return value;
	}

	public String getUrl()
	{
		return tfUrl.getText();
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
				value=true;
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
