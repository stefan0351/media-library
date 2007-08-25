package com.kiwisoft.media.fanfic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.swing.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsFrame;

public class PairingDetailsView extends DetailsView
{
	public static void create(Pairing pairing)
	{
		new DetailsFrame(new PairingDetailsView(pairing)).show();
	}

	private Pairing pairing;

	// Konfigurations Panel
	private JTextField tfName;

	private PairingDetailsView(Pairing pairing)
	{
		this.pairing=pairing;
		createContentPanel();
		initializeData();
	}

	private void initializeData()
	{
		if (pairing!=null)
		{
			tfName.setText(pairing.getName());
		}
	}

	public boolean apply()
	{
		String name=tfName.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name is missing!", "Error", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (pairing==null) pairing=FanFicManager.getInstance().createPairing();
			pairing.setName(name);
			transaction.close();
			return true;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			try
			{
				if (transaction!=null) transaction.rollback();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	protected void createContentPanel()
	{
		tfName=new JTextField();

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 80));
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
	}

	public JComponent getDefaultFocusComponent()
	{
		return tfName;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public FrameTitleUpdater()
		{
			changedUpdate(null);
		}

		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<unkown>";
			setTitle("Pairing: "+name);
		}
	}
}
