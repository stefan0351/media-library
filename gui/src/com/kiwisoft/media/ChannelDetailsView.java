package com.kiwisoft.media;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.DetailsFrame;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.ImagePanel;
import com.kiwisoft.utils.gui.ImageUpdater;
import com.kiwisoft.utils.gui.lookup.DialogLookupField;
import com.kiwisoft.utils.gui.lookup.FileLookup;
import com.kiwisoft.utils.gui.table.SortableTable;

public class ChannelDetailsView extends DetailsView
{
	public static void create(Channel channel)
	{
		new DetailsFrame(new ChannelDetailsView(channel)).show();
	}

	private Channel channel;

	// Konfigurations Panel
	private JTextField tfName;
	private DialogLookupField tfLogo;
	private JComboBox cbxLanguage;
	private JCheckBox cbReceiving;
	private ImagePanel imgLogo;
	private SortableTable tblNames;
	private NamesTableModel tmNames;

	private ChannelDetailsView(Channel channel)
	{
		createContentPanel();
		setChannel(channel);
	}

	protected void createContentPanel()
	{
		tfName=new JTextField();
		cbxLanguage=new JComboBox(LanguageManager.getInstance().getLanguages().toArray());
		cbxLanguage.updateUI();
		cbxLanguage.setRenderer(new LanguageComboBoxRenderer());
		cbReceiving=new JCheckBox();
		tfLogo=new DialogLookupField(new FileLookup(JFileChooser.FILES_ONLY, true)
		{
			public String getCurrentDirectory()
			{
				return Configurator.getInstance().getString("path.logos.channels", null);
			}

			public void setCurrentDirectory(String path)
			{
				Configurator.getInstance().setString("path.logos.channels", path);
				Configurator.getInstance().saveUserValues();
			}
		});
		imgLogo=new ImagePanel(new Dimension(50, 30));
		tmNames=new NamesTableModel();
		tblNames=new SortableTable(tmNames);
		tblNames.initializeColumns(new MediaTableConfiguration("table.channel.names"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 250));
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Sprache:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(cbxLanguage, new GridBagConstraints(1, row, 2, 1, 1.0, 0.0,
												GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Empfang:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														   GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(cbReceiving, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
												GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Logo:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
														GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(tfLogo, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
										   GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		add(imgLogo, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0,
											GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Alternative Titel:"), new GridBagConstraints(0, row, 3, 1, 0.0, 0.0,
																	 GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));

		row++;
		add(new JScrollPane(tblNames), new GridBagConstraints(0, row, 3, 1, 1.0, 1.0,
															  GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));

		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
		new ImageUpdater(tfLogo.getTextField(), imgLogo);
	}

	private void setChannel(Channel channel)
	{
		this.channel=channel;
		if (channel!=null)
		{
			tfName.setText(channel.getName());
			cbxLanguage.setSelectedItem(channel.getLanguage());
			tfLogo.setText(channel.getLogo());
			cbReceiving.setSelected(channel.isReceivable());
			Iterator it=channel.getAltNames().iterator();
			while (it.hasNext())
			{
				Name name=(Name)it.next();
				tmNames.addName(name.getName(), name.getLanguage());
			}
			tmNames.sort();
		}
		else
		{
			cbxLanguage.setSelectedItem(LanguageManager.getInstance().getLanguageBySymbol("de"));
			cbReceiving.setSelected(true);
		}
	}

	public boolean apply()
	{
		String name=tfName.getText();
		if (StringUtils.isEmpty(name))
		{
			JOptionPane.showMessageDialog(this, "Name fehlt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			tfName.requestFocus();
			return false;
		}
		Language language=(Language)cbxLanguage.getSelectedItem();
		if (language==null)
		{
			JOptionPane.showMessageDialog(this, "Keine Sprache gewählt!", "Fehler", JOptionPane.ERROR_MESSAGE);
			cbxLanguage.requestFocus();
			return false;
		}
		boolean receiving=cbReceiving.isSelected();
		String logo=tfLogo.getText();
		if (StringUtils.isEmpty(logo)) logo=null;
		Map names=tmNames.getNames();

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (channel==null) channel=ChannelManager.getInstance().createChannel();
			channel.setName(name);
			channel.setLogo(logo);
			channel.setLanguage(language);
			channel.setReceivable(receiving);
			Iterator it=new HashSet(channel.getAltNames()).iterator();
			while (it.hasNext())
			{
				Name altName=(Name)it.next();
				if (names.containsKey(altName.getName()))
				{
					altName.setLanguage((Language)names.get(altName.getName()));
					names.remove(altName.getName());
				}
				else channel.dropAltName(altName);
			}
			it=names.keySet().iterator();
			while (it.hasNext())
			{
				String text=(String)it.next();
				Name altName=channel.createAltName();
				altName.setName(text);
				altName.setLanguage((Language)names.get(text));
			}
			transaction.close();
			return true;
		}
		catch (Exception e)
		{
			if (transaction!=null)
			{
				try
				{
					transaction.rollback();
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<unbekannt>";
			setTitle("Sender: "+name);
		}
	}
}
