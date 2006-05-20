package com.kiwisoft.media.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.WindowManager;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.lookup.LookupField;
import com.kiwisoft.media.ui.show.ShowLookup;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Language;

public class OldEpisodeFormatterDialog extends JDialog
{
	private DialogLookupField tfSource;
	private DialogLookupField tfTarget;
	private LookupField tfShow;
	private LookupField tfLanguage;
	private boolean value;

	public OldEpisodeFormatterDialog(JFrame frame, String source, String target)
	{
		super(frame, "Konvertiere alte Episoden", true);
		createContentPanel();
		initializeData(source, target);
		pack();
		WindowManager.arrange(frame, this);
	}

	private void initializeData(String source, String target)
	{
		tfSource.setText(source);
		tfTarget.setText(target);
	}

	private void createContentPanel()
	{
		tfShow=new LookupField(new ShowLookup());
		tfLanguage=new LookupField(new LanguageLookup());
		tfSource=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true));
		tfSource.setColumns(40);
		tfTarget=new DialogLookupField(new FileLookup(JFileChooser.DIRECTORIES_ONLY, false));
		tfTarget.setColumns(40);

		JPanel pnlContent=new JPanel(new GridBagLayout());

		int row=0;
		pnlContent.add(new JLabel("Serie:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		pnlContent.add(tfShow, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		pnlContent.add(new JLabel("Sprache:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
		pnlContent.add(tfLanguage, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		pnlContent.add(new JLabel("Quelle:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
		pnlContent.add(tfSource, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		row++;
		pnlContent.add(new JLabel("Ziel:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
		pnlContent.add(tfTarget, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnOk=new JButton(new OkAction());
		pnlButtons.add(btnOk);
		pnlButtons.add(new JButton(new CancelAction()));

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
		return tfShow.getValue()!=null;
	}

	public boolean getValue()
	{
		return value;
	}

	public String getSource()
	{
		return tfSource.getText();
	}

	public String getTarget()
	{
		return tfTarget.getText();
	}

	public Show getShow()
	{
		return (Show)tfShow.getValue();
	}

	public Language getLanguage()
	{
		return (Language)tfLanguage.getValue();
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
