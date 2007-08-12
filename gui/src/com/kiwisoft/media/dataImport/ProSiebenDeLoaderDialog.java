/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 7, 2003
 * Time: 6:01:43 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;
import java.util.List;
import javax.swing.*;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.lookup.DateField;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.progress.ProgressDialog;
import com.kiwisoft.cfg.Configuration;

public class ProSiebenDeLoaderDialog extends JDialog
{
	private DialogLookupField tfPath;
	private DateField tfDate;
	private JTextField tfDays;
	private List<Show> shows;

	public ProSiebenDeLoaderDialog(JFrame owner, List<Show> shows) throws HeadlessException
	{
		super(owner, "Load Pro7 Schedule", true);
		this.shows=shows;
		setContentPane(createContentPanel());
		initialize();
		pack();
		WindowManager.arrange(owner, this);
	}

	private JPanel createContentPanel()
	{
		tfPath=new DialogLookupField(new FileLookup(JFileChooser.DIRECTORIES_ONLY, false));
		tfDate=new DateField();
		tfDays=new JTextField();
		tfDays.setHorizontalAlignment(JTextField.TRAILING);

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlButtons.add(new JButton(new ApplyAction()));
		pnlButtons.add(new JButton(new CancelAction()));

		JPanel panel=new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(400, 150));
		int row=0;
		panel.add(new JLabel("Path:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(tfPath, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
												 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 10), 0, 0));

		row++;
		panel.add(new JLabel("Date:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
															  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(tfDate, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
												 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JLabel("Days:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
															  GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(tfDays, new GridBagConstraints(3, row, 1, 1, 0.5, 0.0,
												 GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 10), 0, 0));

		row++;
		panel.add(pnlButtons, new GridBagConstraints(0, row, 4, 1, 1.0, 0.0,
													 GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		return panel;
	}

	private void initialize()
	{
		tfPath.setText(MediaConfiguration.getRecentSchedulePath());
		tfDate.setDate(MediaConfiguration.getRecentPro7Date());
		tfDays.setText(String.valueOf(MediaConfiguration.getRecentPro7Offset()));
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok", Icons.getIcon("ok"));
		}

		public void actionPerformed(ActionEvent e)
		{
			Date date=tfDate.getDate();
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
				days=Integer.parseInt(tfDays.getText());
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
			String pathName=tfPath.getText();
			if (StringUtils.isEmpty(pathName))
			{
				JOptionPane.showMessageDialog(ProSiebenDeLoaderDialog.this,
											  "Path is missing.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			File path=new File(pathName);
			if (!path.exists())
			{
				int option=JOptionPane.showConfirmDialog(ProSiebenDeLoaderDialog.this,
														 "Directory '"+path+"' doesn't exists. Create?",
														 "Verzeichnis anlegen?",
														 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (option!=JOptionPane.YES_OPTION) return;
			}
			try
			{
				path.mkdirs();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				JOptionPane.showMessageDialog(ProSiebenDeLoaderDialog.this,
											  e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (shows==null)
			{
				MediaConfiguration.setRecentSchedulePath(pathName);
				MediaConfiguration.setRecentPro7Offset(days);
				MediaConfiguration.setRecentPro7Date(date);
				Configuration.getInstance().saveUserValues();
			}
			dispose();
			new ProgressDialog((JFrame)getOwner(), new ProSiebenDeLoader(pathName, date, days, shows)).show();
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
