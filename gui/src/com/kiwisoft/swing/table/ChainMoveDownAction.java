package com.kiwisoft.swing.table;

import java.util.List;
import java.util.Collections;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.collection.Chain;
import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;

/**
 * @author Stefan Stiller
*/
public class ChainMoveDownAction extends MultiContextAction
{
	private Chain chain;
	private TableController tableController;

	public ChainMoveDownAction(TableController tableController, Chain chain)
	{
		super(ChainLink.class, "Move Down", Icons.getIcon("move.down"));
		this.tableController=tableController;
		this.chain=chain;
	}

	@Override
	protected boolean isValid(Object object)
	{
		return super.isValid(object) && object!=chain.getLast();
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void actionPerformed(ActionEvent e)
	{
		List objects=getObjects();
		Collections.sort(objects, Collections.reverseOrder(new Chain.ChainComparator()));
		tableController.getTable().clearSelection();
		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			for (Object object : objects) chain.moveDown((ChainLink) object);
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
			JOptionPane.showMessageDialog(tableController.getTable(), e1.getLocalizedMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
		}

		for (Object object : objects)
		{
			int rowIndex=tableController.getModel().indexOf(object);
			if (rowIndex>=0) tableController.getTable().getSelectionModel().addSelectionInterval(rowIndex, rowIndex);
		}
	}
}
