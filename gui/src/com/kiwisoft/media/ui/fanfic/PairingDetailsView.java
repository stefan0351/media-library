package com.kiwisoft.media.ui.fanfic;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import com.kiwisoft.media.ShowCharacter;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.Pairing;
import com.kiwisoft.media.ui.MediaManagerFrame;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.utils.gui.table.ObjectTableModel;
import com.kiwisoft.utils.gui.DetailsView;
import com.kiwisoft.utils.gui.DetailsFrame;

public class PairingDetailsView extends DetailsView
{
	public static void create(Pairing pairing)
	{
		new DetailsFrame(new PairingDetailsView(pairing)).show();
	}

	private Pairing pairing;

	// Konfigurations Panel
	private JTextField tfName;
	private ObjectTableModel tmCharacters;
	private DynamicTable tblCharacters;

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
			Iterator it=pairing.getCharacters().iterator();
			while (it.hasNext())
			{
				ShowCharacter character=(ShowCharacter)it.next();
				tmCharacters.addObject(character);
			}
			tmCharacters.sort();
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
		Set characters=new HashSet(tmCharacters.getObjects());

		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			if (pairing==null) pairing=FanFicManager.getInstance().createPairing();
			pairing.setName(name);
			Iterator it=new HashSet(pairing.getCharacters()).iterator();
			while (it.hasNext())
			{
				ShowCharacter charcter=(ShowCharacter)it.next();
				if (!characters.contains(charcter)) pairing.dropCharacter(charcter);
				else characters.remove(charcter);
			}
			it=characters.iterator();
			while (it.hasNext()) pairing.addCharacter((ShowCharacter)it.next());
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
		tmCharacters=new ObjectTableModel("character", ShowCharacter.class, null);
		tblCharacters=new DynamicTable(tmCharacters);
		tblCharacters.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.pairing.characters"));

		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(400, 150));
		int row=0;
		add(new JLabel("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(tfName, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		row++;
		add(new JLabel("Charaktere:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
		        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JScrollPane(tblCharacters), new GridBagConstraints(1, row, 1, 1, 1.0, 1.0,
		        GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));

		tfName.getDocument().addDocumentListener(new FrameTitleUpdater());
	}

	public JComponent getDefaultFocusComponent()
	{
		return tfName;
	}

//	private class ReferenceListener implements LookupSelectionListener
//	{
//		public void selectionChanged(LookupEvent event)
//		{
//			if (event.getSource()==tfShow)
//			{
//				Show show=(Show)tfShow.getValue();
//				if (show!=null)
//				{
//					tfMovie.setValue(null);
//					if (StringUtils.isEmpty(tfName.getText())) tfName.setText(show.getName());
//				}
//			}
//			else if (event.getSource()==tfMovie)
//			{
//				Movie movie=(Movie)tfMovie.getValue();
//				if (movie!=null)
//				{
//					tfMovie.setValue(null);
//					if (StringUtils.isEmpty(tfName.getText())) tfName.setText(movie.getName());
//				}
//			}
//		}
//	}

	private class FrameTitleUpdater extends DocumentAdapter
	{
		public FrameTitleUpdater()
		{
			changedUpdate(null);
		}

		public void changedUpdate(DocumentEvent e)
		{
			String name=tfName.getText();
			if (StringUtils.isEmpty(name)) name="<unbekannt>";
			setTitle("Paarung: "+name);
		}
	}
}
