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

import com.kiwisoft.utils.gui.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.DefaultSortableTableModel;
import com.kiwisoft.utils.gui.table.DefaultTableConfiguration;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;

public class PairingsView extends ViewPanel implements Disposable
{
	private UpdateListener updateListener;
	private TableController<Pairing> tableController;

	public PairingsView()
	{
	}

	public String getTitle()
	{
		return "Fan Fiction - Pairings";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Pairing> tableModel=new DefaultSortableTableModel<Pairing>("name");
		for (Pairing pairing : FanFicManager.getInstance().getPairings()) tableModel.addRow(new Row(pairing));
		tableModel.sort();
		updateListener=new UpdateListener();
		FanFicManager.getInstance().addCollectionChangeListener(updateListener);

		tableController=new TableController<Pairing>(tableModel, new DefaultTableConfiguration(PairingsView.class, "pairings"))
		{
			@Override
			public List<ContextAction<? super Pairing>> getToolBarActions()
			{
				List<ContextAction<? super Pairing>> actions=new ArrayList<ContextAction<? super Pairing>>();
				actions.add(new PairingDetailsAction());
				actions.add(new NewPairingAction());
				actions.add(new DeletePairingAction(frame));
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super Pairing>> getContextActions()
			{
				List<ContextAction<? super Pairing>> actions=new ArrayList<ContextAction<? super Pairing>>();
				actions.add(new PairingDetailsAction());
				actions.add(null);
				actions.add(new NewPairingAction());
				actions.add(new DeletePairingAction(frame));
				actions.add(null);
				actions.add(new FanFicsAction(frame));
				return actions;
			}

			@Override
			public ContextAction<Pairing> getDoubleClickAction()
			{
				return new FanFicsAction<Pairing>(frame);
			}

		};
		return tableController.createComponent();
	}

	protected void installComponentListeners()
	{
		super.installComponentListeners();
		tableController.installListeners();
	}

	protected void removeComponentListeners()
	{
		super.removeComponentListeners();
		tableController.removeListeners();
	}

	public void dispose()
	{
		FanFicManager.getInstance().removeCollectionListener(updateListener);
		tableController.dispose();
	}

	private class UpdateListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFicManager.PAIRINGS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Pairing newPairing=(Pairing)event.getElement();
						tableController.getModel().addRow(new Row(newPairing));
						break;
					case CollectionChangeEvent.REMOVED:
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

		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

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

	public static class DeletePairingAction extends SimpleContextAction<Pairing>
	{
		private ApplicationFrame frame;

		public DeletePairingAction(ApplicationFrame frame)
		{
			super("Delete", Icons.getIcon("delete"));
			this.frame=frame;
		}

		public void actionPerformed(ActionEvent event)
		{
			Pairing pairing=getObject();
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

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		return new Bookmark(getTitle(), PairingsView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new PairingsView(), true);
	}
}
