package com.kiwisoft.media.dataimport;

import com.kiwisoft.swing.FormatBasedListRenderer;
import com.kiwisoft.swing.lookup.Lookup;
import com.kiwisoft.swing.lookup.LookupField;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 26.02.11
 */
public class MatchingPanel<T> extends JPanel
{
	private Lookup<T> lookup;

	private JRadioButton createNewButton;
	private JRadioButton manualMatchButton;
	private JRadioButton autoMatchButton;

	private LookupField<T> manualLookupField;
	private JComboBox autoMatchesField;

	public MatchingPanel(Lookup<T> lookup)
	{
		super(new GridBagLayout());
		setBorder(new CompoundBorder(new TitledBorder(new EtchedBorder(), "Match", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
											  UIManager.getFont("Label.font").deriveFont(Font.BOLD)),
									 new EmptyBorder(0, 5, 5, 5)));
		this.lookup=lookup;
		initComponents();
	}

	private void initComponents()
	{
		ButtonGroup buttonGroup=new ButtonGroup();
		buttonGroup.add(createNewButton=new JRadioButton("Create new"));
		buttonGroup.add(manualMatchButton=new JRadioButton("Match manually:"));
		buttonGroup.add(autoMatchButton=new JRadioButton("Automatic matches:"));

		manualLookupField=new LookupField<T>(lookup);
		manualLookupField.setEnabled(false);
		autoMatchesField=new JComboBox(new DefaultComboBoxModel());
		autoMatchesField.setRenderer(new FormatBasedListRenderer());
		autoMatchesField.setEnabled(false);

		int row=0;
		add(createNewButton, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		row++;
		add(manualMatchButton, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(manualLookupField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		row++;
		add(autoMatchButton, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(autoMatchesField, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		manualMatchButton.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				manualLookupField.setEnabled(manualMatchButton.isSelected());
			}
		});
		autoMatchButton.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				autoMatchesField.setEnabled(autoMatchButton.isSelected());
			}
		});
	}

	public void setMatches(Set<T> matches)
	{
		DefaultComboBoxModel model=(DefaultComboBoxModel) autoMatchesField.getModel();
		model.removeAllElements();
		for (T match : matches)
		{
			model.addElement(match);
		}
		if (matches.isEmpty())
		{
			autoMatchButton.setEnabled(false);
			createNewButton.setSelected(true);
		}
		else if (matches.size()==1)
		{
			autoMatchButton.setEnabled(true);
			autoMatchButton.setSelected(true);
			autoMatchesField.setSelectedIndex(0);
		}
		else
		{
			autoMatchButton.setEnabled(true);
			autoMatchButton.setSelected(false);
		}
	}

	public Set<T> getMatches()
	{
		if (createNewButton.isSelected()) return Collections.emptySet();
		if (autoMatchButton.isSelected())
		{
			T item=(T) autoMatchesField.getSelectedItem();
			return item!=null ? Collections.singleton(item) : null;
		}
		if (manualMatchButton.isSelected())
		{
			T item=manualLookupField.getValue();
			return item!=null ? Collections.singleton(item) : null;
		}
		return null;
	}
}
