package com.kiwisoft.swing.table;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

import com.kiwisoft.collection.Chain;
import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class ChainMoveUpAction extends MultiContextAction
{
	private Chain chain;
	private TableController tableController;

	public ChainMoveUpAction(TableController tableController, Chain chain)
	{
		super(ChainLink.class, "Move Up", Icons.getIcon("move.up"));
		this.tableController=tableController;
		this.chain=chain;
	}

	public Chain getChain()
	{
		return chain;
	}

	public void setChain(Chain chain)
	{
		this.chain=chain;
		update(getObjects());
	}

	@Override
	protected boolean isValid(Object object)
	{
		return super.isValid(object) && getChain()!=null && object!=getChain().getFirst();
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void actionPerformed(ActionEvent e)
	{
		List objects=getObjects();
		Collections.sort(objects, new Chain.ChainComparator());
		tableController.getTable().clearSelection();
		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			for (Object object : objects) chain.moveUp((ChainLink)object);
			transaction.close();
		}
		catch (Exception e1)
		{
			try
			{
				if (transaction!=null) transaction.rollback();
			}
			catch (SQLException e2)
			{
				e2.printStackTrace();
			}
			e1.printStackTrace();
			JOptionPane.showMessageDialog(tableController.getTable(), e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		tableController.getModel().sort();
		for (Object object : objects)
		{
			int rowIndex=tableController.getModel().indexOf(object);
			if (rowIndex>=0) tableController.getTable().getSelectionModel().addSelectionInterval(rowIndex, rowIndex);
		}
	}
}
