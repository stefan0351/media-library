package com.kiwisoft.swing.table;

import com.kiwisoft.swing.GuiUtils;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

/**
 * @author Stefan Stiller
* @since 04.04.2010
*/
public class TableComparisonPanel extends JPanel
{
	private static final Color FILL_COLOR=new Color(180, 255, 180);
	private static final Color LINE_COLOR=new Color(110, 255, 110);

	private SortableTable sourceTable;
	private JViewport sourceView;
	private SortableTable targetTable;
	private JViewport targetView;
	private Map<Object, Pair> matches=new LinkedHashMap<Object, Pair>();
	private boolean invalidMatches;

	private Color selectionFillColor;
	private Color selectionLineColor;
	private Listener listener;

	public TableComparisonPanel(SortableTable sourceTable, SortableTable targetTable)
	{
		this.sourceTable=sourceTable;
		this.sourceView=(JViewport) sourceTable.getParent();
		this.targetTable=targetTable;
		this.targetView=(JViewport) targetTable.getParent();

		listener=new Listener();

		sourceTable.getModel().addTableModelListener(listener);
		targetTable.getModel().addTableModelListener(listener);

		sourceView.addChangeListener(listener);
		targetView.addChangeListener(listener);

		sourceTable.getSelectionModel().addListSelectionListener(listener);

		setPreferredSize(new Dimension(60, 100));
		setMinimumSize(new Dimension(60, 20));

		selectionFillColor=sourceTable.getSelectionBackground();
		selectionLineColor=GuiUtils.darker(selectionFillColor, 0.9);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (invalidMatches) validateMatches();

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int ySource=sourceView.getLocationOnScreen().y-getLocationOnScreen().y;
		int sourceScrollPos=sourceView.getViewRect().y-ySource;
		int yTarget=targetView.getLocationOnScreen().y-getLocationOnScreen().y;
		int targetScrollPos=targetView.getViewRect().y-yTarget;
		int yMax=getSize().height;

		g.setClip(0, ySource, getSize().width, yMax-ySource);

		synchronized (matches)
		{
			for (Pair match : matches.values())
			{
				if (match.sourceRow>=0 && match.targetRow>=0)
				{
					Rectangle sourceRect=sourceTable.getCellRect(match.sourceRow, 0, true);
					Rectangle targetRect=targetTable.getCellRect(match.targetRow, 0, true);

					int ySource1=sourceRect.y-sourceScrollPos+2;
					int ySource2=sourceRect.y+sourceRect.height-3-sourceScrollPos;
					int yTarget1=targetRect.y-targetScrollPos+2;
					int yTarget2=targetRect.y+targetRect.height-3-targetScrollPos;

					if (!(ySource2<=ySource && yTarget2<=yTarget) && !(ySource1>yMax && yTarget1>yMax))
					{
						boolean selected=sourceTable.getSelectionModel().isSelectedIndex(match.sourceRow);

						g.setColor(selected ? selectionFillColor : FILL_COLOR);
						g.fillPolygon(new int[]{0, getSize().width, getSize().width, 0}, new int[]{ySource1, yTarget1, yTarget2, ySource2}, 4);
						g.setColor(selected ? selectionLineColor : LINE_COLOR);
						g.drawLine(0, ySource1, getSize().width, yTarget1);
						g.drawLine(0, ySource2, getSize().width, yTarget2);
					}
				}
			}
		}
	}

	private void validateMatches()
	{
		SortableTableModel sourceModel=(SortableTableModel) sourceTable.getModel();
		SortableTableModel targetModel=(SortableTableModel) targetTable.getModel();
		synchronized (matches)
		{
			for (Pair match : matches.values())
			{
				match.sourceRow=sourceModel.indexOf(match.sourceObject);
				match.targetRow=targetModel.indexOf(match.targetObject);
			}
		}
		invalidMatches=false;
	}

	public void setMatch(Object sourceObject, Object targetObject)
	{
		synchronized (matches)
		{
			if (targetObject!=null)
			{
				invalidMatches=true;
				matches.put(sourceObject, new Pair(sourceObject, targetObject));
			}
			else matches.remove(sourceObject);
		}
	}

	public Object getMatch(Object sourceObject)
	{
		Pair pair=matches.get(sourceObject);
		return pair!=null ? pair.targetObject : null;
	}

	public void clearMatches()
	{
		synchronized (matches)
		{
			matches.clear();
		}
	}

	public void dispose()
	{
		sourceView.removeChangeListener(listener);
		targetView.removeChangeListener(listener);
		sourceTable.getSelectionModel().removeListSelectionListener(listener);
		matches.clear();
	}

	private static class Pair
	{
		private int targetRow;
		private int sourceRow;
		private Object sourceObject;
		private Object targetObject;

		private Pair(Object sourceObject, Object targetObject)
		{
			this.sourceObject=sourceObject;
			this.targetObject=targetObject;
		}
	}

	private class Listener implements ChangeListener, ListSelectionListener, TableModelListener
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{
			repaint();
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				repaint();
			}
		}

		@Override
		public void tableChanged(TableModelEvent e)
		{
			invalidMatches=true;
			repaint();
		}
	}
}
