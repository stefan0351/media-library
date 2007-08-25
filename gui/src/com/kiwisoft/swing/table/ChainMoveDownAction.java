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
import com.kiwisoft.swing.table.TableController;

/**
 * @author Stefan Stiller
*/
public class ChainMoveDownAction<T> extends MultiContextAction<T>
{
	private Chain chain;
	private TableController tableController;

	public ChainMoveDownAction(TableController tableController, Chain chain)
	{
		super("Move Down", Icons.getIcon("move.down"));
		this.tableController=tableController;
		this.chain=chain;
	}

	@Override
	public void update(List<? extends T> objects)
	{
		super.update(objects);
		if (!objects.isEmpty())
		{
			if (objects.contains(chain.getLast())) setEnabled(false);
		}
	}

	@SuppressWarnings({"unchecked"})
	public void actionPerformed(ActionEvent e)
	{
		List<T> objects=getObjects();
		Collections.sort(objects, Collections.reverseOrder(new Chain.ChainComparator()));
		tableController.getTable().clearSelection();
		Transaction transaction=null;
		try
		{
			transaction=DBSession.getInstance().createTransaction();
			for (T object : objects) chain.moveDown((ChainLink) object);
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

		for (T object : objects)
		{
			int rowIndex=tableController.getModel().indexOf(object);
			if (rowIndex>=0) tableController.getTable().getSelectionModel().addSelectionInterval(rowIndex, rowIndex);
		}
	}
}
