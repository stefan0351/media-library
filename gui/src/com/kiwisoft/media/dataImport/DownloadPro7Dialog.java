/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 7, 2003
 * Time: 6:01:43 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kiwisoft.media.dataImport.Pro7InfoLoader;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.lookup.DateField;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

public class DownloadPro7Dialog extends JDialog
{
	private DialogLookupField tfPath;
	private DateField tfDate;
	private JTextField tfDays;
	private Set<Show> shows;

	public DownloadPro7Dialog(JFrame owner, Set<Show> shows) throws HeadlessException
	{
		super(owner, "Lade Pro7 Termine", true);
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
		panel.add(new JLabel("Pfad:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(tfPath, new GridBagConstraints(1, row, 3, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 10), 0, 0));

		row++;
		panel.add(new JLabel("Datum:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
		panel.add(tfDate, new GridBagConstraints(1, row, 1, 1, 0.5, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		panel.add(new JLabel("Tage:"), new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
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
		tfPath.setText(Configurator.getInstance().getString("path.dates.p7", ""));
		tfDate.setDate(Configurator.getInstance().getDate("download.p7.date"));
		tfDays.setText(String.valueOf(Configurator.getInstance().getInt("download.p7.offset", 7)));
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok", IconManager.getIcon("com/kiwisoft/utils/icons/ok.gif"));
		}

		public void actionPerformed(ActionEvent e)
		{
			Date date=tfDate.getDate();
			if (date==null)
			{
				JOptionPane.showMessageDialog(DownloadPro7Dialog.this,
				        "Kein Datum eingegeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (date.before(DateUtils.getToday()))
			{
				JOptionPane.showMessageDialog(DownloadPro7Dialog.this,
				        "Datum muss in der Zukunft liegen.", "Fehler", JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(DownloadPro7Dialog.this,
				        "Tage muss zwischen 0 und 50 liegen.", "Fehler", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String pathName=tfPath.getText();
			if (StringUtils.isEmpty(pathName))
			{
				JOptionPane.showMessageDialog(DownloadPro7Dialog.this,
				        "Kein Pfad eingegeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
				return;
			}
			File path=new File(pathName);
			if (!path.exists())
			{
				int option=JOptionPane.showConfirmDialog(DownloadPro7Dialog.this,
				        "Das Verzeichnis '"+path+"' existiert nicht.\nSoll es angelegt werden?",
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
				JOptionPane.showMessageDialog(DownloadPro7Dialog.this,
				        e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (shows==null)
			{
				Configurator.getInstance().setString("path.dates.p7", pathName);
				Configurator.getInstance().setInt("download.p7.offset", days);
				Configurator.getInstance().setDate("download.p7.date", date);
				Configurator.getInstance().saveUserValues();
			}
			dispose();
			new ProgressDialog((JFrame)getOwner(), new Pro7InfoLoader(pathName, date, days, shows)).show();
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
