/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.fanfic;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBObject;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.media.MediaConfiguration;

public class FanFicsView extends ViewPanel
{
	private FanFicGroup group;

	private FanFicListener fanFicListener;
	private PartsListener partsListener;

	private TableController<FanFic> tableController;
	private PartsTableController partsTableController;
	private FanFic currentFanFic;

	public FanFicsView(FanFicGroup group)
	{
		this.group=group;
	}

	@Override
	public String getTitle()
	{
		return "Fan Fiction - "+group.getFanFicGroupName();
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{

		SortableTableModel<FanFic> tmFanFics=new DefaultSortableTableModel<FanFic>(FanFic.ID, FanFic.TITLE, FanFic.AUTHORS, FanFic.FANDOMS, FanFic.PAIRINGS);
		SortableTableModel<FanFicPart> tmParts=new DefaultSortableTableModel<FanFicPart>(FanFicPart.SEQUENCE, FanFicPart.NAME, FanFicPart.SOURCE)
		{
			@Override
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (columnIndex==0) return rowIndex+1;
				return super.getValueAt(rowIndex, columnIndex);
			}
		};

		tableController=new TableController<FanFic>(tmFanFics, new DefaultTableConfiguration("fanfics.list", FanFicsView.class, "fanfics"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new FanFicDetailsAction());
				actions.add(new NewFanFicAction(group));
				actions.add(new DeleteFanFicAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new FanFicDetailsAction());
				actions.add(null);
				actions.add(new NewFanFicAction(group));
				actions.add(new DeleteFanFicAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new FanFicDetailsAction();
			}

			@Override
			protected void selectionChanged(List<FanFic> objects)
			{
				super.selectionChanged(objects);
				setCurrentFanFic(objects!=null && objects.size()==1 ? objects.get(0) : null);
			}
		};
		tableController.getTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		partsTableController=new PartsTableController(tmParts, frame);

		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(tableController.getComponent());
		panel.add(partsTableController.getComponent());
		return panel;
	}

	@Override
	public void initializeData()
	{
		SortableTableModel<FanFic> tableModel=tableController.getModel();
		fanFicListener=new FanFicListener();
		if (group!=null)
		{
			for (FanFic fanFic : group.getFanFics())
			{
				tableModel.addRow(new FanFicTableRow(fanFic));
			}
			FanFicManager.getInstance().addCollectionChangeListener(fanFicListener);
		}
		tableModel.sort();
	}

	private CollectionChangeListener getPartsListener()
	{
		if (partsListener==null) partsListener=new PartsListener();
		return partsListener;
	}

	private void setCurrentFanFic(FanFic currentFanFic)
	{
		if (this.currentFanFic!=null) this.currentFanFic.removeCollectionListener(getPartsListener());
		this.currentFanFic=currentFanFic;
		partsTableController.setFanFic(currentFanFic);
		if (currentFanFic!=null) currentFanFic.addCollectionListener(getPartsListener());
	}

	@Override
	protected void installComponentListeners()
	{
		tableController.installListeners();
		partsTableController.installListeners();
		super.installComponentListeners();

	}

	@Override
	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		partsTableController.removeListeners();
		super.removeComponentListeners();
	}

	@Override
	public void dispose()
	{
		setCurrentFanFic(null);
		FanFicManager.getInstance().removeCollectionListener(fanFicListener);
		tableController.dispose();
		partsTableController.dispose();
		super.dispose();
	}

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), FanFicsView.class);
		bookmark.setParameter("class", group.getClass().getName());
		bookmark.setParameter("id", String.valueOf(group.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String className=bookmark.getParameter("class");
		Long id=new Long(bookmark.getParameter("id"));
		try
		{
			FanFicGroup group=(FanFicGroup)DBLoader.getInstance().load(Utils.<DBObject>cast(Class.forName(className)), id);
			frame.setCurrentView(new FanFicsView(group));
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	private class FanFicListener implements CollectionChangeListener
	{
		@Override
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFicManager.FANFICS.equals(event.getPropertyName()))
			{
				SortableTableModel<FanFic> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						FanFic newFanFic=(FanFic)event.getElement();
						if (group==null || group.contains(newFanFic))
							tableModel.addRow(new FanFicTableRow(newFanFic));
						break;
					case CollectionChangeEvent.REMOVED:
					{
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
					}
					break;
					case CollectionChangeEvent.CHANGED:
						if (group!=null)
						{
							FanFic fanFic=(FanFic)event.getElement();
							int index=tableModel.indexOf(fanFic);
							if (group.contains(fanFic))
							{
								if (index<0) tableModel.addRow(new FanFicTableRow(fanFic));
							}
							else
							{
								if (index>=0) tableModel.removeRowAt(index);
							}
						}
				}
			}
		}
	}

	private class PartsListener implements CollectionChangeListener
	{
		@Override
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (FanFic.PARTS.equals(event.getPropertyName()))
			{
				SortableTableModel<FanFicPart> tableModel=partsTableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						FanFicPart newPart=(FanFicPart)event.getElement();
						tableModel.addRow(new PartTableRow(newPart));
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
				}
			}
		}
	}

	private static class FanFicTableRow extends BeanTableRow<FanFic>
	{
		public FanFicTableRow(FanFic fanFic)
		{
			super(fanFic);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			if (FanFic.AUTHORS.equals(property)) return StringUtils.formatAsEnumeration(getUserObject().getAuthors());
			if (FanFic.FANDOMS.equals(property)) return StringUtils.formatAsEnumeration(getUserObject().getFanDoms());
			if (FanFic.PAIRINGS.equals(property)) return StringUtils.formatAsEnumeration(getUserObject().getPairings());
			return super.getDisplayValue(column, property);
		}
	}

	private static class PartTableRow extends BeanTableRow<FanFicPart>
	{
		public PartTableRow(FanFicPart part)
		{
			super(part);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			if (FanFicPart.SOURCE.equals(property))
			{
				String source=getUserObject().getSource();
				if (!StringUtils.isEmpty(source)) return FileUtils.getFile(MediaConfiguration.getFanFicPath(), source).getAbsolutePath();
				else return null;
			}
			return super.getDisplayValue(column, property);
		}
	}

	private static class PartsTableController extends TableController<FanFicPart>
	{
		private NewFanFicPartAction newPartAction;
		private DeleteFanFicPartAction deletePartAction;
		private ChainMoveUpAction partMoveUpAction;
		private ChainMoveDownAction partMoveDownAction;

		public PartsTableController(SortableTableModel<FanFicPart> tableModel, ApplicationFrame frame)
		{
			super(tableModel, new FixedOrderTableConfiguration("fanfics.parts", FanFicsView.class, "parts", FanFicPart.SEQUENCE));
			tableModel.setResortable(false);
			newPartAction=new NewFanFicPartAction(frame);
			deletePartAction=new DeleteFanFicPartAction(frame);
			partMoveUpAction=new ChainMoveUpAction(this, null);
			partMoveDownAction=new ChainMoveDownAction(this, null);
		}

		@Override
		public List<ContextAction> getToolBarActions()
		{
			List<ContextAction> actions=new ArrayList<ContextAction>();
			actions.add(new FanFicPartDetailsAction());
			actions.add(newPartAction);
			actions.add(deletePartAction);
			actions.add(partMoveUpAction);
			actions.add(partMoveDownAction);
			return actions;
		}

		@Override
			public List<ContextAction> getContextActions()
		{
			List<ContextAction> actions=new ArrayList<ContextAction>();
			actions.add(new FanFicPartDetailsAction());
			actions.add(null);
			actions.add(newPartAction);
			actions.add(deletePartAction);
			actions.add(null);
			actions.add(partMoveUpAction);
			actions.add(partMoveDownAction);
			return actions;
		}

		@Override
		public ContextAction getDoubleClickAction()
		{
			return new FanFicPartDetailsAction();
		}

		public void setFanFic(FanFic fanFic)
		{
			newPartAction.setFanFic(fanFic);
			deletePartAction.setFanFic(fanFic);
			SortableTableModel<FanFicPart> tableModel=getModel();
			tableModel.clear();
			if (fanFic!=null)
			{
				for (FanFicPart part : fanFic.getParts())
				{
					tableModel.addRow(new PartTableRow(part));
				}
				tableModel.sort();
				partMoveUpAction.setChain(fanFic.getParts());
				partMoveDownAction.setChain(fanFic.getParts());
			}
			else
			{
				partMoveUpAction.setChain(null);
				partMoveDownAction.setChain(null);
			}
		}
	}
}
