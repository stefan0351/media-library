package com.kiwisoft.media.files;

import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JComponent;

import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.app.DetailsView;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowLookup;
import com.kiwisoft.swing.InvalidDataException;
import com.kiwisoft.swing.lookup.LookupField;

/**
 * @author Stefan Stiller
 */
public class SelectShowView extends DetailsView
{
	public static Show createDialog(Window owner)
	{
		SelectShowView view=new SelectShowView();
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK)
		{
			return view.show;
		}
		return null;
	}

	private Show show;

	private LookupField<Show> showField;

	private SelectShowView()
	{
		setTitle("Select Show");
		initComponents();
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return showField;
	}

	private void initComponents()
	{
		showField=new LookupField<Show>(new ShowLookup());
		showField.setPreferredSize(new Dimension(300, showField.getPreferredSize().height));

		setLayout(new GridBagLayout());
		add(new JLabel("Show:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(showField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
	}


	public boolean apply() throws InvalidDataException
	{
		show=showField.getValue();
		return true;
	}
}
