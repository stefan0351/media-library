/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: May 17, 2003
 * Time: 6:55:46 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.utils.gui.WindowManager;

class ConcurrentAirdateDialog extends JDialog
{
	private Set<Airdate> invalidDates;
	private boolean addAirdate=true;

	private Set<Airdate> airdates;
	private AirdateData airdateData;
	private AiringData airingData;
	private Map<JCheckBox,Airdate> checkBoxes=new HashMap<JCheckBox, Airdate>();

	public ConcurrentAirdateDialog(Dialog owner, Set<Airdate> airdates, AirdateData airdateData, AiringData airingData)
			throws HeadlessException
	{
		super(owner, "Konkurrierende Sendetermine", true);
		this.airdates=airdates;
		this.airdateData=airdateData;
		this.airingData=airingData;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		createContentPane();
		pack();
		WindowManager.arrange(owner, this);
	}

	private void createContentPane()
	{
		JPanel pnlButtons=new JPanel(new FlowLayout(FlowLayout.TRAILING));
		pnlButtons.add(new JButton(new ReturnAction()));

		JPanel pnlContent=new JPanel(new GridBagLayout());
		int row=0;
		pnlContent.add(new JLabel("Alte(r) Termin(e):"),
				new GridBagConstraints(0, row++, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Iterator<Airdate> it=airdates.iterator();
		while (it.hasNext())
		{
			Airdate airdate=it.next();
			JCheckBox checkBox=new JCheckBox();
			checkBoxes.put(checkBox, airdate);
			pnlContent.add(checkBox,
					new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));
			pnlContent.add(new JLabel(airdate.getChannelName()+"; "+new SimpleDateFormat("EEE, d.MM.yyyy H:mm").format(airdate.getDate())),
					new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));
			pnlContent.add(new JLabel(airdate.getName()),
					new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));
			DataSource dataSource=airdate.getDataSource();
			if (dataSource!=null)
				pnlContent.add(new JLabel(dataSource.getName()),
						new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));
		}
		pnlContent.add(new JLabel("Neuer Termin:"),
				new GridBagConstraints(0, row++, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		JCheckBox checkBox=new JCheckBox();
		checkBox.setSelected(true);
		checkBoxes.put(checkBox, null);
		pnlContent.add(checkBox,
				new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));
		pnlContent.add(new JLabel(airingData.getChannel()+"; "+new SimpleDateFormat("EEE, d.MM.yyyy H:mm").format(airingData.getTime())),
				new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));
		pnlContent.add(new JLabel(Airdate.getName(airdateData.getShow(), airdateData.getEpisode(), airdateData.getEvent())),
				new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));
		if (airdateData.getDataSource()!=null)
			pnlContent.add(new JLabel(airdateData.getDataSource().getName()),
					new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 0, 10), 0, 0));

		pnlContent.add(pnlButtons,
				new GridBagConstraints(0, row, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));

		setContentPane(pnlContent);
	}

	public Set<Airdate> getInvalidAirdates()
	{
		return invalidDates;
	}

	public boolean isAddAirdate()
	{
		return addAirdate;
	}

	private class ReturnAction extends AbstractAction
	{
		public ReturnAction()
		{
			super("Ok");
		}

		public void actionPerformed(ActionEvent e)
		{
			invalidDates=new HashSet<Airdate>();
			Iterator<JCheckBox> it=checkBoxes.keySet().iterator();
			while (it.hasNext())
			{
				JCheckBox checkBox=it.next();
				if (!checkBox.isSelected())
				{
					Airdate airdate=checkBoxes.get(checkBox);
					if (airdate!=null) invalidDates.add(airdate);
					else addAirdate=false;
				}
			}
			dispose();
		}
	}
}
