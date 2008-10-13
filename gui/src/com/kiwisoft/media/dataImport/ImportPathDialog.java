package com.kiwisoft.media.dataImport;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.lookup.DialogLookupField;
import com.kiwisoft.swing.lookup.FileLookup;

public class ImportPathDialog extends JComponent
{
	private DialogLookupField tfPath;
	private JTextField tfFilter;
	private JDialog dialog;
	private String[] values;
	private String path;
	private String filter;

	private ImportPathDialog(String path, String filter)
	{
		this.path=path;
		this.filter=filter;
	}

	public static String[] create(Window aParent, String path, String filter)
	{
		ImportPathDialog adapter=new ImportPathDialog(path, filter);
		JDialog dialog=adapter.createDialog(aParent);
		GuiUtils.centerWindow(aParent, dialog);
		dialog.setVisible(true);
		return adapter.getValue();
	}

	private JDialog createDialog(Window aParent)
	{
		dialog=new JDialog(aParent, "Path & Filter", Dialog.ModalityType.APPLICATION_MODAL);

		tfPath=new DialogLookupField(new FileLookup(JFileChooser.DIRECTORIES_ONLY, true));
		tfPath.setPreferredSize(new Dimension(300, tfPath.getPreferredSize().height));
		tfPath.setText(path);

		tfFilter=new JTextField(10);
		tfFilter.setText(filter);

		JButton btnOK=new JButton(new ApplyAction());

		JPanel pnlContent=new JPanel();
		pnlContent.setLayout(new GridBagLayout());
		pnlContent.add(new JLabel("Directory:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
																		new Insets(3, 3, 3, 0), 0, 0));
		pnlContent.add(tfPath, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
													  new Insets(3, 3, 3, 3), 0, 0));
		pnlContent.add(new JLabel("Filter:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
																	 new Insets(3, 3, 3, 0), 0, 0));
		pnlContent.add(tfFilter, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
														new Insets(3, 3, 3, 3), 0, 0));
		pnlContent.add(btnOK, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
													 new Insets(3, 3, 3, 3), 0, 0));

		dialog.getRootPane().setDefaultButton(btnOK);
		dialog.getContentPane().add(pnlContent);
		dialog.pack();

		return dialog;
	}

	private class ApplyAction extends AbstractAction
	{
		public ApplyAction()
		{
			super("Ok", Icons.getIcon("ok"));
			putValue(Action.MNEMONIC_KEY, new Integer('o'));
		}

		public void actionPerformed(ActionEvent e)
		{
			values=new String[2];
			values[0]=tfPath.getText();
			values[1]=tfFilter.getText();
			dialog.dispose();
		}
	}

	private String[] getValue()
	{
		return values;
	}

}
