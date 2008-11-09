/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 7, 2003
 * Time: 6:01:43 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;
import javax.swing.*;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.lookup.DateField;
import com.kiwisoft.swing.progress.ProgressDialog;
import com.kiwisoft.cfg.Configuration;

public class ProSiebenDeLoaderDialog extends JDialog
{
	private DateField dateField;
	private JTextField daysField;
	private List<Show> shows;

	public ProSiebenDeLoaderDialog(Window owner, List<Show> shows) throws HeadlessException
	{
		super(owner, "Load Pro7 Schedule", ModalityType.APPLICATION_MODAL);
		this.shows=shows;
		setContentPane(createContentPanel());
		initialize();
		pack();
		GuiUtils.centerWindow(owner, this);
	}

	private JPanel createContentPanel()
	{
		dateField=new DateField();
		daysField=new JTextField();
		daysField.setHorizontalAlignment(JTextField.TRAILING);

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlButtons.add(new JButton(new ApplyAction()));
		pnlButtons.add(new JButton(new CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(400, 150));
		int row=0;
		panel.add(new JLabel("Date:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(dateField, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
												 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JLabel("Days:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
															  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(daysField, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
												 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 10), 0, 0));

		row++;
		panel.add(pnlButtons, new GridBagConstraints(0, row, 4, 1, 1.0, 0.0,
													 GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		return panel;
	}

	private void initialize()
	{
		dateField.setDate(MediaConfiguration.getRecentPro7Date());
		daysField.setText(String.valueOf(MediaConfiguration.getRecentPro7Offset()));
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok", Icons.getIcon("ok"));
		}

		public void actionPerformed(ActionEvent e)
		{
			Date date=dateField.getDate();
			if (date==null)
			{
				JOptionPane.showMessageDialog(ProSiebenDeLoaderDialog.this,
											  "Date is missing.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (date.before(DateUtils.getToday().getTime()))
			{
				JOptionPane.showMessageDialog(ProSiebenDeLoaderDialog.this,
											  "Date must lie in the future.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			int days=0;
			try
			{
				days=Integer.parseInt(daysField.getText());
			}
			catch (NumberFormatException e1)
			{
			}
			if (days<=0 || days>=50)
			{
				JOptionPane.showMessageDialog(ProSiebenDeLoaderDialog.this,
											  "Days must lie between 0 and 50.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (shows==null)
			{
				MediaConfiguration.setRecentPro7Offset(days);
				MediaConfiguration.setRecentPro7Date(date);
				Configuration.getInstance().saveUserValues();
			}
			dispose();
			new ProgressDialog(getOwner(), new ProSiebenDeLoader(null, date, days, shows)).start();
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
