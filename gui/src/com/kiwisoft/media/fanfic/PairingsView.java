/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.DefaultSortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;

public class PairingsView extends ViewPanel implements Disposable
{
	private TableController<Pairing> tableController;

	public PairingsView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Fan Fiction - Pairings";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Pairing> tableModel=new DefaultSortableTableModel<Pairing>("name");
		for (Pairing pairing : FanFicManager.getInstance().getPairings()) tableModel.addRow(new Row(pairing));
		tableModel.sort();
		getModelListenerList().installPropertyChangeListener(FanFicManager.getInstance(), new UpdateListener());

		tableController=new TableController<Pairing>(tableModel, new DefaultTableConfiguration("pairings.list", PairingsView.class, "pairings"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new PairingDetailsAction());
				actions.add(new NewPairingAction());
				actions.add(new DeletePairingAction(frame));
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new PairingDetailsAction());
				actions.add(null);
				actions.add(new NewPairingAction());
				actions.add(new DeletePairingAction(frame));
				actions.add(null);
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new FanFicsAction(frame);
			}

		};
		return tableController.getComponent();
	}

	@Override
	protected void installComponentListeners()
	{
		super.installComponentListeners();
		tableController.installListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		super.removeComponentListeners();
		tableController.removeListeners();
	}

	@Override
	public void dispose()
	{
		tableController.dispose();
		super.dispose();
	}

	private class UpdateListener extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (FanFicManager.PAIRINGS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionPropertyChangeEvent.ADDED:
						Pairing newPairing=(Pairing)event.getElement();
						tableController.getModel().addRow(new Row(newPairing));
						break;
					case CollectionPropertyChangeEvent.REMOVED:
						int index=tableController.getModel().indexOf(event.getElement());
						if (index>=0) tableController.getModel().removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class Row extends SortableTableRow<Pairing> implements PropertyChangeListener
	{
		public Row(Pairing pairing)
		{
			super(pairing);
		}

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			switch (column)
			{
				case 0:
					return getUserObject().getName();
			}
			return null;
		}
	}

	public static class DeletePairingAction extends SimpleContextAction
	{
		private ApplicationFrame frame;

		public DeletePairingAction(ApplicationFrame frame)
		{
			super(Pairing.class, "Delete", Icons.getIcon("delete"));
			this.frame=frame;
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			Pairing pairing=(Pairing)getObject();
			if (pairing.isUsed())
			{
				JOptionPane.showMessageDialog(frame, "The pairing '"+pairing.getName()+"' can't be deleted.", "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(frame, "Delete pairing '"+pairing.getName()+"'?", "Confirmation",
													 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					FanFicManager.getInstance().dropPairing(pairing);
					transaction.close();
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
							JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		return new Bookmark(getTitle(), PairingsView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new PairingsView());
	}
}
