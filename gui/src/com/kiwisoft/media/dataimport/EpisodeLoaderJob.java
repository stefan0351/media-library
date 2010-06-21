package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.person.*;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author Stefan Stiller
 * @since 05.04.2010
 */
class EpisodeLoaderJob implements Job
{
	private EpisodeLoader episodeLoader;
	private Show show;
	private List<EpisodeData> episodeDataList;
	private Map<EpisodeData, Episode> episodes;
	private ProgressSupport progressSupport;
	private Language english;
	private Language german;

	EpisodeLoaderJob(EpisodeLoader episodeLoader, Show show, List<EpisodeData> episodeDataList, Map<EpisodeData, Episode> episodes)
	{
		this.episodeLoader=episodeLoader;
		this.show=show;
		this.episodeDataList=episodeDataList;
		this.episodes=episodes;
		this.english=LanguageManager.getInstance().getLanguageBySymbol("en");
		this.german=LanguageManager.getInstance().getLanguageBySymbol("de");
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
				episodeLoader.loadDetails(progressSupport, episodeData);
				Thread.sleep(300);
				progressSupport.info("Loaded details for "+episodeData.getTitle());
			}

			Episode episode=episodes.get(episodeData);
			if (!resolvePersons(episodeData, episode)) return false;
			if (!saveEpisode(episode, episodeData)) return false;
			progressSupport.progress();
		}
		return true;
	}

	/**
	 * Match persons of download data to persons in database
	 */
	private boolean resolvePersons(EpisodeData episodeData, Episode episode)
	{
		return resolveCast(episode, episodeData.getMainCast())
			   && resolveCast(episode, episodeData.getRecurringCast())
			   && resolveCast(episode, episodeData.getGuestCast())
			   && resolveCrew(episode, episodeData.getDirectedBy(), CreditType.DIRECTOR)
			   && resolveCrew(episode, episodeData.getWrittenBy(), CreditType.WRITER);
	}

	private boolean resolveCast(Episode episode, List<CastData> cast)
	{
		if (cast!=null)
		{
			Map<String, CastMember> roleMap=new HashMap<String, CastMember>();
			Map<Person, CastMember> actorMap=new HashMap<Person, CastMember>();
			if (episode!=null)
			{
				for (CastMember castMember : episode.getCastMembers())
				{
					if (!StringUtils.isEmpty(castMember.getCharacterName())) roleMap.put(castMember.getCharacterName(), castMember);
					if (castMember.getActor()!=null) actorMap.put(castMember.getActor(), castMember);
				}
			}
			for (CastData castData : cast)
			{
				castData.setRole(StringUtils.trimAll(castData.getRole()));
				if (!StringUtils.isEmpty(castData.getRole()))
				{
					CastMember castMember=roleMap.get(castData.getRole());
					if (castMember!=null)
					{
						castData.setCastMember(castMember);
						continue;
					}
				}
				String key=StringUtils.emptyToNull(castData.getKey());
				if (key!=null) castData.setPerson(PersonManager.getInstance().getPersonByTVcomKey(castData.getKey()));
				String name=StringUtils.emptyToNull(castData.getActor());
				if (castData.getPerson()==null && name!=null)
				{
					Set<Person> persons=DBLoader.getInstance().loadSet(Person.class, null, "binary name=?"+(key!=null ? " and tvcom_key is null" : ""), name);
					if (!persons.isEmpty())
					{
						if (persons.size()==1) castData.setPerson(persons.iterator().next());
						else
						{
							progressSupport.error("Multiple persons with name '"+name+"' found. Set the TV.com key for the correct person to '"+key+"' and try again.");
							return false;
						}
					}
				}
				if (castData.getPerson()!=null)
				{
					if (key!=null && StringUtils.isEmpty(castData.getPerson().getTvcomKey()))
					{
						if (!DBSession.execute(new UpdatePerson(castData.getPerson(), key))) return false;
					}
					CastMember castMember=actorMap.get(castData.getPerson());
					if (castMember!=null)
					{
						castData.setCastMember(castMember);
						continue;
					}
				}
				else if (name!=null)
				{
					CreatePerson createPerson=new CreatePerson(name, key);
					if (DBSession.execute(createPerson)) castData.setPerson(createPerson.person);
					else return false;
				}
			}
		}
		return true;
	}

	private boolean resolveCrew(Episode episode, List<CrewData> crew, CreditType creditType)
	{
		if (crew!=null)
		{
			Map<Person, Credit> personMap=new HashMap<Person, Credit>();
			if (episode!=null)
			{
				for (Iterator it=episode.getCredits(creditType).iterator(); it.hasNext();)
				{
					Credit crewMember=(Credit) it.next();
					personMap.put(crewMember.getPerson(), crewMember);
				}
			}
			for (CrewData crewData : crew)
			{
				String key=StringUtils.emptyToNull(crewData.getKey());
				if (key!=null) crewData.setPerson(PersonManager.getInstance().getPersonByTVcomKey(crewData.getKey()));
				String name=StringUtils.emptyToNull(crewData.getName());
				if (crewData.getPerson()==null && name!=null)
				{
					Set<Person> persons=DBLoader.getInstance().loadSet(Person.class, null, "binary name=?"+(key!=null ? " and tvcom_key is null" : ""), name);
					if (!persons.isEmpty())
					{
						if (persons.size()==1) crewData.setPerson(persons.iterator().next());
						else
						{
							progressSupport.error("Multiple persons with name '"+name+"' found. Set the TV.com key for the correct person to '"+key+"' and try again.");
							return false;
						}
					}
				}
				if (crewData.getPerson()!=null)
				{
					if (key!=null && StringUtils.isEmpty(crewData.getPerson().getTvcomKey()))
					{
						if (!DBSession.execute(new UpdatePerson(crewData.getPerson(), key))) return false;
					}
					Credit credit=personMap.get(crewData.getPerson());
					if (credit!=null)
					{
						crewData.setCredit(credit);
						continue;
					}
				}
				else if (name!=null)
				{
					CreatePerson createPerson=new CreatePerson(name, key);
					if (DBSession.execute(createPerson)) crewData.setPerson(createPerson.person);
					else return false;
				}
			}
		}
		return true;
	}

	private boolean saveEpisode(Episode episode, EpisodeData data)
	{
		boolean newEpisode=episode==null;
		SaveEpisode transaction=new SaveEpisode(data, episode);
		if (DBSession.execute(transaction))
		{
			episode=transaction.episode;
			if (newEpisode) progressSupport.info(episode.getUserKey()+": \""+episode.getTitle()+"\" created.");
			else progressSupport.info(episode.getUserKey()+": \""+episode.getTitle()+"\" updated.");
			return true;
		}
		return false;
	}


	@Override
	public void dispose() throws IOException
	{
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

	private class UpdatePerson extends MyTransactional
	{
		private String key;
		private Person person;

		public UpdatePerson(Person person, String key)
		{
			this.person=person;
			this.key=key;
		}

		@Override
		public void run() throws Exception
		{
			person.setTvcomKey(key);
		}
	}

	private class CreatePerson extends MyTransactional
	{
		private String name;
		private String key;
		private Person person;

		public CreatePerson(String name, String key)
		{
			this.name=name;
			this.key=key;
		}

		@Override
		public void run() throws Exception
		{
			person=PersonManager.getInstance().createPerson();
			person.setName(name);
			person.setTvcomKey(key);
		}
	}

	private class SaveEpisode extends MyTransactional
	{
		private EpisodeData episodeData;
		private Episode episode;

		public SaveEpisode(EpisodeData episodeData, Episode episode)
		{
			this.episodeData=episodeData;
			this.episode=episode;
		}

		@Override
		public void run()
		{
			if (episode==null)
			{
				episode=show.createEpisode();
				episode.setUserKey(episodeData.getKey());
			}
			String oldOrigName=episode.getTitle();
			String newOrigName=episodeData.getTitle();
			if (StringUtils.isEmpty(oldOrigName) && !StringUtils.isEmpty(newOrigName)) episode.setTitle(newOrigName);

			String oldName=episode.getGermanTitle();
			String newName=episodeData.getGermanTitle();
			if (StringUtils.isEmpty(oldName) && !StringUtils.isEmpty(newName)) episode.setGermanTitle(newName);

			String oldCode=episode.getProductionCode();
			String newCode=episodeData.getProductionCode();
			if (StringUtils.isEmpty(oldCode) && !StringUtils.isEmpty(newCode)) episode.setProductionCode(newCode);

			String newSummary=episodeData.getEnglishSummary();
			if (!StringUtils.isEmpty(newSummary))
			{
				String oldSummary=episode.getSummaryText(english);
				if (StringUtils.isEmpty(oldSummary)) episode.setSummaryText(english, newSummary);
			}

			newSummary=episodeData.getGermanSummary();
			if (!StringUtils.isEmpty(newSummary))
			{
				String oldSummary=episode.getSummaryText(german);
				if (StringUtils.isEmpty(oldSummary)) episode.setSummaryText(german, newSummary);
			}

			Date oldAirdate=episode.getAirdate();
			Date newAirdate=episodeData.getFirstAirdate();
			if (oldAirdate==null && newAirdate!=null) episode.setAirdate(newAirdate);

			saveEpisodeCast(episodeData.getMainCast(), CreditType.MAIN_CAST);
			saveEpisodeCast(episodeData.getRecurringCast(), CreditType.RECURRING_CAST);
			saveEpisodeCast(episodeData.getGuestCast(), CreditType.GUEST_CAST);

			saveShowCast(episodeData.getMainCast(), CreditType.MAIN_CAST);
			saveShowCast(episodeData.getRecurringCast(), CreditType.RECURRING_CAST);
			deleteDuplicateShowCast();

			saveCrew(episodeData.getWrittenBy(), CreditType.WRITER);
			saveCrew(episodeData.getDirectedBy(), CreditType.DIRECTOR);
		}

		private void saveEpisodeCast(List<CastData> cast, CreditType type)
		{
			if (cast!=null)
			{
				for (CastData castData : cast)
				{
					if (castData.getCastMember()!=null) continue;
					CastMember castMember=new CastMember();
					castMember.setEpisode(episode);
					castMember.setCreditType(type);
					castMember.setActor(castData.getPerson());
					castMember.setCharacterName(castData.getRole());
				}
			}
		}

		private void saveShowCast(List<CastData> cast, CreditType creditType)
		{
			if (cast!=null)
			{
				Map<String, CastMember> roleMap=new HashMap<String, CastMember>();
				Map<Person, CastMember> actorMap=new HashMap<Person, CastMember>();
				for (CastMember castMember : episode.getShow().getCastMembers(creditType))
				{
					if (!StringUtils.isEmpty(castMember.getCharacterName())) roleMap.put(castMember.getCharacterName(), castMember);
					if (castMember.getActor()!=null) actorMap.put(castMember.getActor(), castMember);
				}
				for (CastData castData : cast)
				{
					if (!StringUtils.isEmpty(castData.getRole()) && roleMap.containsKey(castData.getRole())) continue;
					if (castData.getPerson()!=null && actorMap.containsKey(castData.getPerson())) continue;
					CastMember castMember=new CastMember();
					castMember.setShow(show);
					castMember.setCreditType(creditType);
					castMember.setActor(castData.getPerson());
					castMember.setCharacterName(castData.getRole());
				}
			}
		}

		private void deleteDuplicateShowCast()
		{
			Set<String> castSet=new HashSet<String>();
			deleteDuplicateCast(castSet, show.getCastMembers(CreditType.MAIN_CAST));
			deleteDuplicateCast(castSet, show.getCastMembers(CreditType.RECURRING_CAST));
			deleteDuplicateCast(castSet, show.getCastMembers(CreditType.GUEST_CAST));
		}

		private void deleteDuplicateCast(Set<String> castSet, Set<CastMember> castMembers)
		{
			for (CastMember castMember : castMembers)
			{
				StringBuilder castString=new StringBuilder();
				if (castMember.getActor()!=null) castString.append(castMember.getActor().getName());
				castString.append("\n");
				castString.append(StringUtils.nullToEmpty(castMember.getCharacterName()));
				if (castSet.contains(castString.toString())) castMember.delete();
				castSet.add(castString.toString());
			}
		}

		private void saveCrew(List<CrewData> crew, CreditType creditType)
		{
			if (crew!=null)
			{
				for (CrewData crewData : crew)
				{
					if (crewData.getCredit()!=null) continue;
					Credit credit=new Credit();
					credit.setEpisode(episode);
					credit.setCreditType(creditType);
					credit.setPerson(crewData.getPerson());
					credit.setSubType(crewData.getSubType());
				}
			}
		}
	}
}
