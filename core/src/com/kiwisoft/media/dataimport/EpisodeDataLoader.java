package com.kiwisoft.media.dataimport;

import java.io.IOException;
import java.util.*;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.person.*;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.StringUtils;
import static com.kiwisoft.utils.StringUtils.isEmpty;
import static com.kiwisoft.utils.StringUtils.trimString;
import com.kiwisoft.utils.WebUtils;
import static com.kiwisoft.utils.xml.XMLUtils.removeTags;
import static com.kiwisoft.utils.xml.XMLUtils.unescapeHtml;

/**
 * @author Stefan Stiller
 */
public abstract class EpisodeDataLoader implements Job
{
	private ProgressSupport progress;

	private String baseUrl;
	private Show show;
	private int startSeason;
	private int endSeason;
	private boolean autoCreate;
	private Date today;
	private Map<String, Person> personCache;
	private Language english;
	private Language german;

	protected EpisodeDataLoader(Show show, String baseUrl, int startSeason, int endSeason, boolean autoCreate)
	{
		this.show=show;
		this.baseUrl=baseUrl;
		this.startSeason=startSeason;
		this.endSeason=endSeason;
		this.autoCreate=autoCreate;
		today=new Date();
		this.english=LanguageManager.getInstance().getLanguageBySymbol("en");
		this.german=LanguageManager.getInstance().getLanguageBySymbol("de");
		personCache=new HashMap<String, Person>();
	}

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl=baseUrl;
	}

	public ProgressSupport getProgress()
	{
		return progress;
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progress=new ProgressSupport(this, progressListener);
		int seasonCount=endSeason-startSeason+1;
		for (int season=startSeason; season<=endSeason && !progress.isStoppedByUser(); season++)
		{
			progress.initialize(false, seasonCount, null);
			progress.progress(season-startSeason, false);
			if (!loadSeason(season)) return false;
		}
		return true;
	}

	private boolean loadSeason(int season) throws IOException, InterruptedException
	{
		progress.startStep("Load episode list for season "+season+"...");

		List<EpisodeData> episodes=loadEpisodeList(season);

		progress.startStep("Load episode details for season "+season+"...");
		progress.initialize(true, episodes.size(), null);
		for (EpisodeData data : episodes)
		{
			if (progress.isStoppedByUser()) return false;
			saveEpisode(data);
			progress.progress();
		}
		return true;
	}

	protected void saveEpisode(EpisodeData data) throws IOException, InterruptedException
	{
		Episode episode=ShowManager.getInstance().getEpisodeByName(show, data.getTitle());
		if (episode==null) episode=createEpisode(data);
		if (episode!=null)
		{
			progress.startStep("Loading details for episode "+episode.getUserKey()+": "+data.getTitle()+"...");
			if (data.getFirstAirdate()==null || data.getFirstAirdate().before(today))
			{
				if (!StringUtils.isEmpty(data.getEpisodeUrl()))
				{
					loadDetails(data);
				}
				saveEpisode(episode, data);
				Thread.sleep(300); // To avoid DOS on the server
			}
		}
	}

	protected abstract List<EpisodeData> loadEpisodeList(int season) throws IOException;

	protected abstract void loadDetails(EpisodeData data) throws IOException;

	protected abstract Episode createEpisode(Show show, EpisodeData info);

	private Episode createEpisode(final EpisodeData data)
	{
		if (autoCreate)
		{
			MyTransactional<Episode> transactional=new MyTransactional<Episode>()
			{
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
				progress.info("New episode "+transactional.value+" created.");
				return transactional.value;
			}
			else
			{
				progress.error("Create of new episode "+data.getTitle()+" failed.");
				return null;
			}
		}
		else return createEpisode(show, data);
	}

	public void saveEpisode(final Episode episode, final EpisodeData data)
	{
		DBSession.execute(new MyTransactional()
		{
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
		saveCrew(episode, CreditType.WRITER, null, data.getWrittenBy());
		saveCrew(episode, CreditType.DIRECTOR, null, data.getDirectedBy());
		saveCrew(episode, CreditType.WRITER, "Story", data.getStoryBy());

		saveCast(episode, CreditType.MAIN_CAST, data.getMainCast());
		saveCast(show, CreditType.MAIN_CAST, data.getMainCast());
		saveCast(episode, CreditType.RECURRING_CAST, data.getRecurringCast());
		saveCast(show, CreditType.RECURRING_CAST, data.getRecurringCast());
		saveCast(episode, CreditType.GUEST_CAST, data.getGuestCast());
		progress.info(episode.getUserKey()+" "+episode.getTitle()+" updated.");
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
				public void run() throws Exception
				{
					for (CastData castData : castList)
					{
						String actorName=castData.person.name;
						if (!castNames.contains(actorName))
						{
							String character=trimString(castData.character);
							Person person=getPerson(personCache, castData.person.key, castData.person.name);
							CastMember castMember=new CastMember();
							castMember.setCreditType(type);
							if (production instanceof Episode) castMember.setEpisode((Episode)production);
							else castMember.setShow((Show)production);
							castMember.setActor(person);
							castMember.setCharacterName(character);
						}
					}
				}
			});
		}
	}

	private static Person getPerson(Map<String, Person> persons, String key, String name)
	{
		Person person=null;
		if (key!=null)
		{
			person=persons.get(key);
			if (person==null) person=PersonManager.getInstance().getPersonByTVcomKey(key);
		}
		if (person==null) person=persons.get(name);
		if (person==null) person=PersonManager.getInstance().getPersonByName(name, true);
		//noinspection ConstantConditions
		if (person==null || (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(person.getTvcomKey()) && !key.equals(person.getTvcomKey())))
		{
			person=new Person();
			person.setName(name);
			persons.put(name, person);
		}
		if (StringUtils.isEmpty(person.getTvcomKey())) person.setTvcomKey(key);
		if (!StringUtils.isEmpty(key)) persons.put(key, person);
		return person;
	}

	private void saveCrew(final Episode episode, final CreditType type, final String subType, final List<PersonData> crewList)
	{
		if (crewList!=null)
		{
			final Set<String> crewNames=new HashSet<String>();
			for (Iterator it=episode.getCredits(type).iterator(); it.hasNext();)
			{
				Credit crewMember=(Credit)it.next();
				crewNames.add(crewMember.getPerson().getName());
			}
			DBSession.execute(new MyTransactional()
			{
				public void run() throws Exception
				{
					for (PersonData personData : crewList)
					{
						if (!crewNames.contains(personData.name))
						{
							Person person=getPerson(personCache, personData.key, personData.name);
							Credit crewMember=new Credit();
							crewMember.setEpisode(episode);
							crewMember.setCreditType(type);
							crewMember.setSubType(subType);
							crewMember.setPerson(person);
						}
					}
				}
			});
		}
	}

	public void dispose() throws IOException
	{
	}

	protected String loadUrl(String episodeUrl) throws IOException
	{
		int tries=0;
		while (true)
		{
			try
			{
				return WebUtils.loadURL(episodeUrl);
			}
			catch (IOException e)
			{
				tries++;
				if (tries<3) progress.warning(e.getMessage());
				else throw e;
			}
		}
	}

	protected String convertHTML(String html)
	{
		return trimString(unescapeHtml(removeTags(html)));
	}

	public static class PersonData
	{
		private String name;
		private String key;

		public PersonData(String key, String actor)
		{
			this.key=key;
			this.name=actor;
		}

		public String getKey()
		{
			return key;
		}

		public String getName()
		{
			return name;
		}

		@Override
		public String toString()
		{
			return name+"["+key+"]";
		}
	}

	protected static class CastData
	{
		private PersonData person;
		private String character;

		public CastData(PersonData person, String character)
		{
			this.person=person;
			this.character=character;
		}

		@Override
		public String toString()
		{
			return person+" as "+character;
		}
	}

	private abstract class MyTransactional<T> implements Transactional
	{
		public T value;

		public void handleError(Throwable e, boolean rollback)
		{
			progress.error(e.getClass().getSimpleName()+": "+e.getMessage());
		}
	}
}
