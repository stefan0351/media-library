package com.kiwisoft.media.links;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.kiwisoft.app.DetailsView;
import com.kiwisoft.app.DetailsDialog;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.swing.lookup.LookupField;
import com.kiwisoft.swing.InvalidDataException;

/**
 * @author Stefan Stiller
 */
public class SelectLinkableView extends DetailsView
{
	public static Linkable createDialog(Window owner)
	{
		SelectLinkableView view=new SelectLinkableView();
		DetailsDialog dialog=new DetailsDialog(owner, view, DetailsDialog.OK_CANCEL_ACTIONS);
		dialog.show();
		if (dialog.getReturnValue()==DetailsDialog.OK)
		{
			return view.linkable;
		}
		return null;
	}

	private Linkable linkable;

	private LookupField<Linkable> linkableField;

	private SelectLinkableView()
	{
		setTitle("Select Show");
		initComponents();
	}

	@Override
	public JComponent getDefaultFocusComponent()
	{
		return linkableField;
	}

	private void initComponents()
	{
		linkableField=new LookupField<Linkable>(new LinkableLookup());
		linkableField.setPreferredSize(new Dimension(300, linkableField.getPreferredSize().height));

		setLayout(new GridBagLayout());
		add(new JLabel("Target Group:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(linkableField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
	}


	@Override
	public boolean apply() throws InvalidDataException
	{
		linkable=linkableField.getValue();
		return true;
	}
}
