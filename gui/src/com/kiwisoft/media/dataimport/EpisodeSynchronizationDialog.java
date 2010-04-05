package com.kiwisoft.media.dataimport;

import com.kiwisoft.collection.Chain;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.person.*;
import com.kiwisoft.media.show.*;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
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
import com.kiwisoft.utils.StringUtils;
import static com.kiwisoft.utils.StringUtils.isEmpty;

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
public class EpisodeSynchronizationDialog extends ButtonDialog
{
	private SortableTableModel<EpisodeData> episodeDataModel;
	private SortableTableModel<Episode> episodesModel;
	private TableComparisonPanel comparisonPanel;
	private EpisodeDataLoader2 loader;
	private Show show;
	private MatchAction matchAction;
	private UnmatchAction unmatchAction;
	private TableController<EpisodeData> episodeDataController;
	private TableController<Episode> episodesController;

	public EpisodeSynchronizationDialog(Window window, Show show, EpisodeDataLoader2 loader)
	{
		super(window, "Synchronize Episodes", true);
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
																				   EpisodeSynchronizationDialog.class, "episodeData", EpisodeData.KEY);
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
		tableConfiguration=new FixedOrderTableConfiguration("EpisodeSynchronizationDialog.episodes"+profileSuffix, EpisodeSynchronizationDialog.class, "episodes", Episode.USER_KEY);
		tableConfiguration.setResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		episodesController=new TableController<Episode>(episodesModel, tableConfiguration)
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.addAll(super.getToolBarActions());
				actions.add(new EpisodeDetailsAction(EpisodeSynchronizationDialog.this));
				actions.add(new ChainMoveUpAction(this, show.getEpisodes()));
				actions.add(new ChainMoveDownAction(this, show.getEpisodes()));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new EpisodeDetailsAction(EpisodeSynchronizationDialog.this);
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
				SmallProgressDialog progressDialog=new SmallProgressDialog(EpisodeSynchronizationDialog.this, loadListJob);
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
				new SmallProgressDialog(EpisodeSynchronizationDialog.this, new LoadDetailsJob(episodeData)).start(new JobFinishListener()
				{
					@Override
					public void jobFinished(int result)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								EpisodeDataDetailsView.create(EpisodeSynchronizationDialog.this, episodeData);
							}
						});
					}
				});
			}
			else EpisodeDataDetailsView.create(EpisodeSynchronizationDialog.this, episodeData);
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
				SmallProgressDialog progressDialog=new SmallProgressDialog(EpisodeSynchronizationDialog.this, new ProcessJob(episodeDataList, episodes));
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
				JOptionPane.showMessageDialog(EpisodeSynchronizationDialog.this, "No episodes selected!", "Error", JOptionPane.ERROR_MESSAGE);
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

	private class ProcessJob implements Job
	{
		private List<EpisodeData> episodeDataList;
		private Map<EpisodeData, Episode> episodes;
		private ProgressSupport progressSupport;
		private Language english;
		private Language german;
		private Map<String, Person> personCache;

		public ProcessJob(List<EpisodeData> episodeDataList, Map<EpisodeData, Episode> episodes)
		{
			this.episodeDataList=episodeDataList;
			this.episodes=episodes;
			this.english=LanguageManager.getInstance().getLanguageBySymbol("en");
			this.german=LanguageManager.getInstance().getLanguageBySymbol("de");
			personCache=new HashMap<String, Person>();
		}

		@Override
		public String getName()
		{
			return "Importing Episodes";
		}

		@Override
		public boolean run(ProgressListener progressListener) throws Exception
		{
			progressSupport=new ProgressSupport(this, progressListener);
			progressSupport.initialize(true, episodeDataList.size(), null);
			for (EpisodeData episodeData : episodeDataList)
			{
				if (progressSupport.isStoppedByUser()) return false;

				if (!episodeData.isDetailsLoaded() && !StringUtils.isEmpty(episodeData.getLink(EpisodeData.DETAILS_LINK)))
				{
					loader.loadDetails(progressSupport, episodeData);
					Thread.sleep(300);
					progressSupport.info("Loaded details for "+episodeData.getTitle());
				}

				Episode episode=episodes.get(episodeData);
				if (episode==null) episode=createEpisode(episodeData);
				if (episode!=null) saveEpisode(episode, episodeData);
				progressSupport.progress();
			}
			return true;
		}

		private Episode createEpisode(final EpisodeData data)
		{
			MyTransactional<Episode> transactional=new MyTransactional<Episode>()
			{
				@Override
				public void run() throws Exception
				{
					value=show.createEpisode();
					value.setUserKey(data.getKey());
					value.setGermanTitle(data.getGermanTitle());
					value.setTitle(data.getTitle());
					value.setAirdate(data.getFirstAirdate());
					value.setProductionCode(data.getProductionCode());
				}
			};
			if (DBSession.execute(transactional))
			{
				progressSupport.info("New episode "+transactional.value+" created.");
				return transactional.value;
			}
			else
			{
				progressSupport.error("Create of new episode "+data.getTitle()+" failed.");
				return null;
			}
		}

		private void saveEpisode(final Episode episode, final EpisodeData data)
		{
			DBSession.execute(new MyTransactional()
			{
				@Override
				public void run()
				{
					String oldOrigName=episode.getTitle();
					String newOrigName=data.getTitle();
					if (isEmpty(oldOrigName) && !isEmpty(newOrigName)) episode.setTitle(newOrigName);

					String oldName=episode.getGermanTitle();
					String newName=data.getGermanTitle();
					if (isEmpty(oldName) && !isEmpty(newName)) episode.setGermanTitle(newName);

					String oldCode=episode.getProductionCode();
					String newCode=data.getProductionCode();
					if (isEmpty(oldCode) && !isEmpty(newCode)) episode.setProductionCode(newCode);

					String newSummary=data.getEnglishSummary();
					if (!isEmpty(newSummary))
					{
						String oldSummary=episode.getSummaryText(english);
						if (isEmpty(oldSummary)) episode.setSummaryText(english, newSummary);
					}

					newSummary=data.getGermanSummary();
					if (!isEmpty(newSummary))
					{
						String oldSummary=episode.getSummaryText(german);
						if (isEmpty(oldSummary)) episode.setSummaryText(german, newSummary);
					}

					Date oldAirdate=episode.getAirdate();
					Date newAirdate=data.getFirstAirdate();
					if (oldAirdate==null && newAirdate!=null) episode.setAirdate(newAirdate);
				}
			});
			saveCrew(episode, CreditType.WRITER, data.getWrittenBy());
			saveCrew(episode, CreditType.DIRECTOR, data.getDirectedBy());

			saveCast(episode, CreditType.MAIN_CAST, data.getMainCast());
			saveCast(show, CreditType.MAIN_CAST, data.getMainCast());
			saveCast(episode, CreditType.RECURRING_CAST, data.getRecurringCast());
			saveCast(show, CreditType.RECURRING_CAST, data.getRecurringCast());
			saveCast(episode, CreditType.GUEST_CAST, data.getGuestCast());
			progressSupport.info(episode.getUserKey()+" "+episode.getTitle()+" updated.");
		}

		private void saveCast(final Production production, final CreditType type, final List<CastData> castList)
		{
			if (castList!=null)
			{
				final Set<String> castNames=new HashSet<String>();
				for (CastMember castMember : production.getCastMembers(type))
				{
					castNames.add(castMember.getActor().getName());
				}
				if (type==CreditType.RECURRING_CAST)
				{
					for (CastMember castMember : production.getCastMembers(CreditType.MAIN_CAST))
					{
						castNames.add(castMember.getActor().getName());
					}
				}
				DBSession.execute(new MyTransactional()
				{
					@Override
					public void run() throws Exception
					{
						for (CastData castData : castList)
						{
							String actorName=castData.getActor();
							if (!castNames.contains(actorName))
							{
								String character=StringUtils.trimAll(castData.getRole());
								Person person=getPerson(castData.getKey(), actorName);
								CastMember castMember=new CastMember();
								castMember.setCreditType(type);
								if (production instanceof Episode) castMember.setEpisode((Episode) production);
								else castMember.setShow((Show) production);
								castMember.setActor(person);
								castMember.setCharacterName(character);
							}
						}
					}
				});
			}
		}

		private Person getPerson(String key, String name)
		{
			Person person=null;
			if (key!=null)
			{
				person=personCache.get(key);
				if (person==null) person=PersonManager.getInstance().getPersonByTVcomKey(key);
			}
			if (person==null) person=personCache.get(name);
			if (person==null) person=PersonManager.getInstance().getPersonByName(name, true);
			//noinspection ConstantConditions
			if (person==null || (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(person.getTvcomKey()) && !key.equals(person.getTvcomKey())))
			{
				person=new Person();
				person.setName(name);
				personCache.put(name, person);
			}
			if (StringUtils.isEmpty(person.getTvcomKey())) person.setTvcomKey(key);
			if (!StringUtils.isEmpty(key)) personCache.put(key, person);
			return person;
		}

		private void saveCrew(final Episode episode, final CreditType type, final List<CrewData> crewList)
		{
			if (crewList!=null)
			{
				final Map<String, Credit> crewNames=new HashMap<String, Credit>();
				final Set<Credit> credits=episode.getCredits(type);
				for (Iterator it=credits.iterator(); it.hasNext();)
				{
					Credit crewMember=(Credit) it.next();
					crewNames.put(crewMember.getPerson().getName()+" / "+crewMember.getSubType(), crewMember);
				}
				DBSession.execute(new MyTransactional()
				{
					@Override
					public void run() throws Exception
					{
						for (CrewData crewData : crewList)
						{
							Credit credit=crewNames.get(crewData.getName()+" / "+crewData.getSubType());
							if (credit==null)
							{
								Person person=getPerson(crewData.getKey(), crewData.getName());
								Credit crewMember=new Credit();
								crewMember.setEpisode(episode);
								crewMember.setCreditType(type);
								crewMember.setSubType(crewData.getSubType());
								crewMember.setPerson(person);
							}
							else credits.remove(credit);
						}
						for (Credit credit : credits) episode.dropCredit(credit);
					}
				});
			}
		}

		@Override
		public void dispose() throws IOException
		{
			personCache.clear();
		}

		private abstract class MyTransactional<T> implements Transactional
		{
			public T value;

			@Override
			public void handleError(Throwable e, boolean rollback)
			{
				progressSupport.error(e.getClass().getSimpleName()+": "+e.getMessage());
			}
		}

	}
}


