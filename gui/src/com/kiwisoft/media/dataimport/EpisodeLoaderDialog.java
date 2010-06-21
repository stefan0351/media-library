package com.kiwisoft.media.dataimport;

import com.kiwisoft.collection.Chain;
import com.kiwisoft.media.show.*;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.swing.ButtonDialog;
import com.kiwisoft.swing.ComponentUtils;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.progress.JobFinishListener;
import com.kiwisoft.swing.progress.SmallProgressDialog;
import com.kiwisoft.swing.table.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 02.04.2010
 */
public class EpisodeLoaderDialog extends ButtonDialog
{
	private SortableTableModel<EpisodeData> episodeDataModel;
	private SortableTableModel<Episode> episodesModel;
	private TableComparisonPanel comparisonPanel;
	private EpisodeLoader loader;
	private Show show;
	private MatchAction matchAction;
	private UnmatchAction unmatchAction;
	private TableController<EpisodeData> episodeDataController;
	private TableController<Episode> episodesController;

	public EpisodeLoaderDialog(Window window, Show show, EpisodeLoader loader)
	{
		super(window, "Download Episodes", true);
		this.show=show;
		this.loader=loader;
		init();
	}

	@Override
	protected Action[] getActions()
	{
		return new Action[]{
				new StartAction(),
				new CancelAction("Close")
		};
	}

	@Override
	protected JComponent createContentPane()
	{
		setPreferredSize(new Dimension(1200, 600));

		matchAction=new MatchAction();
		unmatchAction=new UnmatchAction();
		String profileSuffix="";
		if (loader.hasGermanData())
		{
			episodeDataModel=new DefaultSortableTableModel<EpisodeData>(EpisodeDataRow.SELECTED, EpisodeData.KEY, EpisodeData.GERMAN_TITLE, EpisodeData.TITLE, EpisodeData.PRODUCTION_CODE);
			profileSuffix=".de";
		}
		else
			episodeDataModel=new DefaultSortableTableModel<EpisodeData>(EpisodeDataRow.SELECTED, EpisodeData.KEY, EpisodeData.TITLE, EpisodeData.PRODUCTION_CODE);
		episodeDataModel.setResortable(false);
		BaseTableConfiguration tableConfiguration=new FixedOrderTableConfiguration("EpisodeSynchronizationDialog.episodeData"+profileSuffix,
																				   EpisodeLoaderDialog.class, "episodeData", EpisodeData.KEY);
		tableConfiguration.setResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		episodeDataController=new TableController<EpisodeData>(episodeDataModel, tableConfiguration)
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.addAll(super.getToolBarActions());
				actions.add(new EpisodeDataDetailsAction());
				actions.add(new ComplexAction("Match...", null,
											  matchAction,
											  unmatchAction,
											  null,
											  new RematchAllAction()));
				actions.add(new ComplexAction("Select...", null,
											  new SelectNewAction(),
											  new SelectAllAction(),
											  null,
											  new UnselectAllAction()));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new EpisodeDataDetailsAction();
			}
		};
		episodeDataController.setScrollOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		if (loader.hasGermanData())
			episodesModel=new DefaultSortableTableModel<Episode>(Episode.USER_KEY, Episode.GERMAN_TITLE, Episode.TITLE, Episode.PRODUCTION_CODE);
		else
			episodesModel=new DefaultSortableTableModel<Episode>(Episode.USER_KEY, Episode.TITLE, Episode.PRODUCTION_CODE);
		episodesModel.setResortable(false);
		tableConfiguration=new FixedOrderTableConfiguration("EpisodeSynchronizationDialog.episodes"+profileSuffix, EpisodeLoaderDialog.class, "episodes", Episode.USER_KEY);
		tableConfiguration.setResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		episodesController=new TableController<Episode>(episodesModel, tableConfiguration)
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.addAll(super.getToolBarActions());
				actions.add(new EpisodeDetailsAction(EpisodeLoaderDialog.this));
				actions.add(new ChainMoveUpAction(this, show.getEpisodes()));
				actions.add(new ChainMoveDownAction(this, show.getEpisodes()));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new EpisodeDetailsAction(EpisodeLoaderDialog.this);
			}

			@Override
			protected void selectionChanged(List<Episode> objects)
			{
				super.selectionChanged(objects);
				if (objects!=null && objects.size()==1) matchAction.setEpisode(objects.get(0));
				else matchAction.setEpisode(null);
			}
		};

		comparisonPanel=new TableComparisonPanel(episodeDataController.getTable(), episodesController.getTable());

		JPanel panel=new JPanel(new GridBagLayout());
		int row=0;
		panel.add(ComponentUtils.createBoldLabel("Episodes at "+loader.getName()), new GridBagConstraints(0, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(ComponentUtils.createBoldLabel("Episodes in Database"), new GridBagConstraints(2, row, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		row++;
		panel.add(episodeDataController.getComponent(), new GridBagConstraints(0, row, 1, 1, 0.5, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		panel.add(comparisonPanel, new GridBagConstraints(1, row, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		panel.add(episodesController.getComponent(), new GridBagConstraints(2, row, 1, 1, 0.5, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));

		episodeDataController.installListeners();
		episodesController.installListeners();

		return panel;
	}

	@Override
	protected void initData()
	{
		super.initData();
		ImportUtils.USE_CACHE=true;

		loadEpisodes();

		// Must not be started before window is visible
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final LoadListJob loadListJob=new LoadListJob();
				SmallProgressDialog progressDialog=new SmallProgressDialog(EpisodeLoaderDialog.this, loadListJob);
				progressDialog.start(new JobFinishListener()
				{
					@Override
					public void jobFinished(int result)
					{
						if (loadListJob.dataList!=null)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								@Override
								public void run()
								{
									List<EpisodeData> dataList=loadListJob.dataList;
									List<EpisodeDataRow> rows=new ArrayList<EpisodeDataRow>(dataList.size());
									for (int i=0; i<dataList.size(); i++)
									{
										EpisodeData episodeData=dataList.get(i);
										rows.add(new EpisodeDataRow(i, episodeData));
									}
									episodeDataModel.setRows(rows);
									matchEpisodes(dataList);
								}
							});
						}

					}
				});
			}
		});
	}

	private void loadEpisodes()
	{
		Chain<Episode> episodes=show.getEpisodes();
		List<EpisodeRow> episodeRows=new ArrayList<EpisodeRow>(episodes.size());
		for (Episode episode : episodes) episodeRows.add(new EpisodeRow(episode));
		episodesModel.setRows(episodeRows);
	}

	private void matchEpisodes(List<EpisodeData> dataList)
	{
		for (EpisodeData episodeData : dataList)
		{
			Episode episode=ShowManager.getInstance().getEpisodeByName(show, episodeData.getTitle());
			comparisonPanel.setMatch(episodeData, episode);
		}
		comparisonPanel.repaint();
	}

	@Override
	public void dispose()
	{
		show=null;
		comparisonPanel.dispose();
		episodeDataController.dispose();
		episodesController.dispose();
		super.dispose();
	}

	/**
	 * @author Stefan Stiller
	 */
	private class EpisodeDataDetailsAction extends SimpleContextAction
	{
		public EpisodeDataDetailsAction()
		{
			super(EpisodeData.class, "Details", Icons.getIcon("details"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			final EpisodeData episodeData=(EpisodeData) getObject();
			if (!episodeData.isDetailsLoaded())
			{
				new SmallProgressDialog(EpisodeLoaderDialog.this, new LoadDetailsJob(episodeData)).start(new JobFinishListener()
				{
					@Override
					public void jobFinished(int result)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								EpisodeDataDetailsView.create(EpisodeLoaderDialog.this, episodeData);
							}
						});
					}
				});
			}
			else EpisodeDataDetailsView.create(EpisodeLoaderDialog.this, episodeData);
		}
	}

	private class LoadDetailsJob implements Job
	{
		private EpisodeData episodeData;

		private LoadDetailsJob(EpisodeData episodeData)
		{
			this.episodeData=episodeData;
		}

		@Override
		public String getName()
		{
			return "Load Episode Details";
		}

		@Override
		public boolean run(ProgressListener progressListener) throws Exception
		{
			ProgressSupport progressSupport=new ProgressSupport(this, progressListener);
			loader.loadDetails(progressSupport, episodeData);
			return true;
		}

		@Override
		public void dispose() throws IOException
		{
		}
	}

	private class LoadListJob implements Job
	{
		private List<EpisodeData> dataList;

		@Override
		public String getName()
		{
			return "Load Episode List";
		}

		@Override
		public boolean run(ProgressListener progressListener) throws Exception
		{
			ProgressSupport progressSupport=new ProgressSupport(this, progressListener);
			dataList=loader.loadList(progressSupport);
			return true;
		}

		@Override
		public void dispose() throws IOException
		{
		}
	}

	private class StartAction extends AbstractAction
	{
		private StartAction()
		{
			super("Start", Icons.getIcon("play"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			final List<EpisodeData> episodeDataList=new ArrayList<EpisodeData>();
			Map<EpisodeData, Episode> episodes=new HashMap<EpisodeData, Episode>();
			for (int i=0; i<episodeDataModel.getRowCount(); i++)
			{
				EpisodeDataRow row=(EpisodeDataRow) episodeDataModel.getRow(i);
				if (row.isSelected())
				{
					EpisodeData episodeData=row.getUserObject();
					episodeDataList.add(episodeData);
					episodes.put(episodeData, (Episode) comparisonPanel.getMatch(episodeData));
				}
			}
			if (!episodeDataList.isEmpty())
			{
				SmallProgressDialog progressDialog=new SmallProgressDialog(EpisodeLoaderDialog.this, new EpisodeLoaderJob(loader, show, episodeDataList, episodes));
				progressDialog.start(new JobFinishListener()
				{
					@Override
					public void jobFinished(int result)
					{
						loadEpisodes();
						matchEpisodes(episodeDataList);
					}
				});
			}
			else
			{
				JOptionPane.showMessageDialog(EpisodeLoaderDialog.this, "No episodes selected!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class MatchAction extends SimpleContextAction
	{
		private Episode episode;

		private MatchAction()
		{
			super(EpisodeData.class, "Match");
		}

		public void setEpisode(Episode episode)
		{
			this.episode=episode;
			setEnabled(getObject()!=null);
		}

		@Override
		public void setEnabled(boolean newValue)
		{
			super.setEnabled(episode!=null && newValue);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			comparisonPanel.setMatch(getObject(), episode);
			comparisonPanel.repaint();
		}
	}

	private class UnmatchAction extends SimpleContextAction
	{
		private UnmatchAction()
		{
			super(EpisodeData.class, "Unmatch");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			comparisonPanel.setMatch(getObject(), null);
			comparisonPanel.repaint();
		}
	}

	private class RematchAllAction extends ContextAction
	{
		private RematchAllAction()
		{
			super("Rematch All");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			matchEpisodes(episodeDataController.getModel().getObjects());
		}
	}

	private class SelectNewAction extends ContextAction
	{
		private SelectNewAction()
		{
			super("New");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			for (int i=0; i<episodeDataModel.getRowCount(); i++)
			{
				EpisodeDataRow row=(EpisodeDataRow) episodeDataModel.getRow(i);
				if (comparisonPanel.getMatch(row.getUserObject())==null) row.setSelected(true);
			}
		}
	}

	private class SelectAllAction extends ContextAction
	{
		private SelectAllAction()
		{
			super("All");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			for (int i=0; i<episodeDataModel.getRowCount(); i++)
			{
				((EpisodeDataRow) episodeDataModel.getRow(i)).setSelected(true);
			}
		}
	}

	private class UnselectAllAction extends ContextAction
	{
		private UnselectAllAction()
		{
			super("Unselect All");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			for (int i=0; i<episodeDataModel.getRowCount(); i++)
			{
				((EpisodeDataRow) episodeDataModel.getRow(i)).setSelected(false);
			}
		}
	}

}


