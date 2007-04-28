package com.kiwisoft.media.utils;

import com.kiwisoft.utils.gui.table.*;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Chain;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * @author Stefan Stiller
 */
public class TableController<T>
{
	// Components
	private SortableTable table;
	private SortableTableModel<T> model;
	private TableConfiguration configuration;
	private JScrollPane tablePane;
	private List<ContextAction<? super T>> toolBarActions;

	// Listeners
	private MyMouseListener mouseListener;
	private MySelectionListener selectionListener;

	public TableController(SortableTableModel<T> model, TableConfiguration configuration)
	{
		this.model=model;
		this.configuration=configuration;
	}

	public JComponent createComponent()
	{
		toolBarActions=getToolBarActions();

		table=new SortableTable(model);
		table.putClientProperty(TableConstants.ALTERNATE_ROW_BACKGROUND, new Color(240, 240, 255));
		if (configuration!=null) table.initializeColumns(configuration);
		tablePane=new JScrollPane(table);

		JPanel panel=new JPanel(new BorderLayout());
		panel.add(createToolBar(), BorderLayout.NORTH);
		panel.add(tablePane, BorderLayout.CENTER);
		return panel;
	}

	public void installListeners()
	{
		mouseListener=new MyMouseListener();
		table.addMouseListener(mouseListener);
		tablePane.addMouseListener(mouseListener);
		table.getTableHeader().addMouseListener(new TableHeaderMouseListener());
		selectionListener=new MySelectionListener();
		table.getSelectionModel().addListSelectionListener(selectionListener);
	}

	public void removeListeners()
	{
		table.removeMouseListener(mouseListener);
		tablePane.removeMouseListener(mouseListener);
		table.getSelectionModel().removeListSelectionListener(selectionListener);
	}

	protected JToolBar createToolBar()
	{
		return GuiUtils.createToolBar(toolBarActions);
	}

	public List<ContextAction<? super T>> getToolBarActions()
	{
		return Collections.emptyList();
	}

	public List<ContextAction<? super T>> getContextActions()
	{
		return Collections.emptyList();
	}

	public ContextAction<? super T> getDoubleClickAction()
	{
		return null;
	}

	public void dispose()
	{
		model.clear();
	}

	public SortableTableModel<T> getModel()
	{
		return model;
	}

	public SortableTable getTable()
	{
		return table;
	}

	public void updateToolBarActions()
	{
		List<T> objects=TableUtils.getSelectedObjects(table);
		for (ContextAction<? super T> action : toolBarActions) action.update(objects);
	}

	private class MyMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=table.rowAtPoint(e.getPoint());
				SortableTableRow<T> row=model.getRow(rowIndex);
				if (row!=null)
				{
					ContextAction<? super T> action=getDoubleClickAction();
					action.update(Collections.singletonList(row.getUserObject()));
					action.actionPerformed(null);
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				List<ContextAction<? super T>> actions=getContextActions();
				if (!actions.isEmpty())
				{
					List<T> objects=TableUtils.getSelectedObjects(table);
					for (ContextAction<? super T> action : actions)
					{
						if (action!=null) action.update(objects);
					}

					JPopupMenu menu=new JPopupMenu();
					for (ContextAction<? super T> action : actions) ContextAction.addActionToMenu(menu, action);
					menu.show((Component) e.getSource(), e.getX(), e.getY());
				}
				e.consume();
			}
			super.mouseClicked(e);
		}

	}

	private class MySelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				updateToolBarActions();
			}
		}
	}

	protected class MoveUpAction extends MultiContextAction<T>
	{
		private Chain chain;

		public MoveUpAction(Chain chain)
		{
			super("Move Up", Icons.getIcon("move.up"));
			this.chain=chain;
		}

		@Override
		public void update(List<? extends T> objects)
		{
			super.update(objects);
			if (!objects.isEmpty())
			{
				if (objects.contains(chain.getFirst())) setEnabled(false);
			}
		}

		@SuppressWarnings({"unchecked"})
		public void actionPerformed(ActionEvent e)
		{
			List<T> objects=getObjects();
			Collections.sort(objects, new Chain.ChainComparator());
			table.clearSelection();
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (T object : objects) chain.moveUp((Chain.ChainLink) object);
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
				JOptionPane.showMessageDialog(table, e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}

			for (T object : objects)
			{
				int rowIndex=model.indexOf(object);
				if (rowIndex>=0) table.getSelectionModel().addSelectionInterval(rowIndex, rowIndex);
			}
		}
	}

	protected class MoveDownAction extends MultiContextAction<T>
	{
		private Chain chain;

		public MoveDownAction(Chain chain)
		{
			super("Move Down", Icons.getIcon("move.down"));
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
			table.clearSelection();
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (T object : objects) chain.moveDown((Chain.ChainLink) object);
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
				JOptionPane.showMessageDialog(table, e1.getLocalizedMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
			}

			for (T object : objects)
			{
				int rowIndex=model.indexOf(object);
				if (rowIndex>=0) table.getSelectionModel().addSelectionInterval(rowIndex, rowIndex);
			}
		}
	}

	private static class TableHeaderMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getSource() instanceof JTableHeader)
			{
				if ((e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3))
				{
					JTableHeader tableHeader=(JTableHeader) e.getSource();
					SortableTable table=(SortableTable) tableHeader.getTable();

					JMenu columnsMenu=null;
					if (table.getModel() instanceof MutableSortableTableModel)
					{
						MutableSortableTableModel tableModel=(MutableSortableTableModel)table.getModel();

						columnsMenu=new JMenu("Spalten");
						String[] allColumnNames=tableModel.getAllColumnNames();
						for (String name : allColumnNames)
						{
							boolean columnVisible=tableModel.isColumnVisible(name);
							JCheckBoxMenuItem menuItem=new JCheckBoxMenuItem(new ToggleTableColumnAction(tableModel, name, !columnVisible));
							menuItem.setSelected(columnVisible);
							columnsMenu.add(menuItem);
						}
					}


					JPopupMenu menu=new JPopupMenu();
					menu.add(new OptimizeColumnsActions(table));
					if (columnsMenu!=null) menu.add(columnsMenu);
					menu.show(tableHeader, e.getX(), e.getY());
					e.consume();
				}
			}
		}
	}
}
