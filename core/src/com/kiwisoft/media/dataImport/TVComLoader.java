package com.kiwisoft.media.dataImport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.media.*;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.CrewMember;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.utils.StringUtils;
import static com.kiwisoft.utils.StringUtils.isEmpty;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 28.02.2007
 * Time: 21:00:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class TVComLoader implements Job
{
	private ProgressSupport progress;

	private Show show;
	private String baseUrl;
	private int startSeason;
	private int endSeason;
	private boolean autoCreate;
	private Language language;
	private SimpleDateFormat airdateFormat;
	private Map<String, CastData> castCache=new HashMap<String, CastData>();

	protected TVComLoader(Show show, String baseUrl, int startSeason, int endSeason, boolean autoCreate)
	{
		this.show=show;
		this.baseUrl=baseUrl;
		this.startSeason=startSeason;
		this.endSeason=endSeason;
		this.autoCreate=autoCreate;
		this.language=LanguageManager.getInstance().getLanguageBySymbol("en");
		airdateFormat=new SimpleDateFormat("M/d/yyyy");
		airdateFormat.setTimeZone(DateUtils.GMT);
	}

	public String getName()
	{
		return "Load Episodes from TV.com";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progress=new ProgressSupport(this, progressListener);
		for (int season=startSeason; season<=endSeason && !progress.isStoppedByUser(); season++)
		{
			loadSeason(baseUrl, season);
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}

	private void loadSeason(String baseUrl, int season) throws IOException, InterruptedException
	{
		progress.startStep("Load season "+season+"...");
		progress.initialize(false, 1, null);
		String page=WebUtils.loadURL(baseUrl+"?season="+season);
//		FileUtils.saveToFile(page, new File("c:"+File.separator+"Incoming"+File.separator+"season"+season+".html"));

		// Search title
		int index1=page.indexOf("<h1>");

		// Search episode list
		int index2=page.indexOf("<th>Episode</th>", index1);
		int episodeIndex=1;
		while (!progress.isStoppedByUser())
		{
			index1=page.indexOf("<tr", index2);
			if (index1<0) break;
			index1=page.indexOf(">", index1);
			index2=page.indexOf("</tr>", index1);
			if (index2<0) break;
			String htmlRow=page.substring(index1+1, index2);
			List<String> values=XMLUtils.extractCellValues(htmlRow);
			if (values.size()<4) break;

			String episodeKey=values.get(0);
			if (isNumber(episodeKey)) episodeKey=season+"."+(episodeIndex++);
			String nameAndLink=values.get(1);
			XMLUtils.Tag startTag=XMLUtils.getNextTag(nameAndLink, 0, "a");
			XMLUtils.Tag endTag=XMLUtils.getNextTag(nameAndLink, startTag.end, "/a");
			Date airdate=null;
			try
			{
				airdate=airdateFormat.parse(values.get(2));
				if (airdate.getTime()<0L) airdate=null;
			}
			catch (ParseException e)
			{
				e.printStackTrace();
				progress.error(e.getMessage());
			}

			EpisodeData data=new EpisodeData(episodeKey, nameAndLink.substring(startTag.end+1, endTag.start),
											 airdate, values.get(3));
			String episodeUrl=XMLUtils.getAttribute(startTag.text, "href");

			Episode episode=ShowManager.getInstance().getEpisodeByName(show, data.getEpisodeTitle());
			if (episode==null) episode=createEpisode(data);
			if (episode!=null)
			{
				progress.startStep("Load episode "+episode.getUserKey()+": "+data.getEpisodeTitle()+"...");
				loadEpisode(episode, data, episodeUrl);
				Thread.sleep(100); // To avoid DOS on the TV.com server
			}
			progress.progress();
		}
	}

	private Episode createEpisode(final EpisodeData data)
	{
		if (autoCreate)
		{
			MyTransactional<Episode> transactional=new MyTransactional<Episode>()
			{
				public void run() throws Exception
				{
					value=show.createEpisode();
					value.setUserKey(data.getEpisodeKey());
					value.setGermanTitle(data.getGermanEpisodeTitle());
					value.setTitle(data.getEpisodeTitle());
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
				progress.error("Create of new episode "+data.getEpisodeTitle()+" failed.");
				return null;
			}
		}
		else return createEpisode(show, data);
	}

	private boolean isNumber(String text)
	{
		try
		{
			Integer.parseInt(text);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	protected abstract Episode createEpisode(Show show, ImportEpisode info);

	private void loadEpisode(Episode episode, EpisodeData episodeData, String episodeUrl) throws IOException
	{
		String episodePage=WebUtils.loadURL(episodeUrl);
//		FileUtils.saveToFile(episodePage, new File("c:"+File.separator+"Incoming"+File.separator+"episode"+data.getEpisodeNumber()+".html"));

		int index1=episodePage.indexOf("<div id=\"episode-tabs\">");
		index1=episodePage.indexOf("<div id=\"main-col\">", index1);
		index1=episodePage.indexOf("<div>", index1);
		int index2=episodePage.indexOf("<div", index1+5);
		String content=episodePage.substring(index1+5, index2).trim();
		content=content.replaceAll("<br */>", "\n");
		content=XMLUtils.unescapeHtml(content);
		episodeData.setSummary(content);

		index1=episodePage.indexOf("<h1>Cast and Crew</h1>", index2);
		int index3=episodePage.indexOf("</table>", index1);
		while (index1>0 && index1<index3)
		{
			index1=episodePage.indexOf("<tr", index1);
			index2=episodePage.indexOf("<tr", index1+3);
			if (index2>index3) index2=index3;
			String htmlRow=episodePage.substring(index1, index2);
			List<String> values=XMLUtils.extractCellValues(htmlRow);
			if (values.size()==2)
			{
				String creditName=values.get(0);
				String creditValue=values.get(1);
				// todo handle pattern errors
				if ("Writer:".equals(creditName) || "Director:".equals(creditName) || "Story:".equals(creditName))
				{
					creditValue=XMLUtils.removeTags(creditValue).trim();
					creditValue=XMLUtils.unescapeHtml(creditValue);
					if ("Writer:".equals(creditName)) episodeData.setWrittenBy(creditValue.split(","));
					else if ("Director:".equals(creditName)) episodeData.setDirectedBy(creditValue.split(","));
					else if ("Story:".equals(creditName)) episodeData.setStoryBy(creditValue.split(","));
				}
				else if ("Star:".equals(creditName) || "Recurring Role:".equals(creditName) || "Guest Star:".equals(creditName))
				{
					creditValue=XMLUtils.removeTags(creditValue).trim();
					List<CastData> cast=extractCast(creditValue);
					if ("Star:".equals(creditName)) episodeData.setMainCast(cast);
					else if ("Recurring Role:".equals(creditName)) episodeData.setRecurringCast(cast);
					else if ("Guest Star:".equals(creditName)) episodeData.setGuestCast(cast);
				}
				else progress.warning("Unknown credit: "+creditName+"="+creditValue);
			}
			index1=index2;
		}
		saveEpisode(episode, episodeData);
	}

	private List<CastData> extractCast(String value)
	{
		List<CastData> castList=new ArrayList<CastData>();
		String[] castStrings=value.split(",&nbsp;");
		Pattern pattern1=Pattern.compile("(.+) \\((.*\\(.+\\))\\).*");
		Pattern pattern2=Pattern.compile("(.+) \\((.*)\\).*");
		for (int i=0; i<castStrings.length; i++)
		{
			String cast=castStrings[i];
			CastData castData=castCache.get(cast);
			if (castData==null)
			{
				String actorName=null;
				String characterName=null;
				Matcher matcher=pattern1.matcher(cast);
				if (!matcher.matches()) matcher=pattern2.matcher(cast);
				if (matcher.matches())
				{
					actorName=matcher.group(1);
					characterName=matcher.group(2);
				}
				if (actorName==null || !isValidName(actorName) || (characterName!=null && !isValidName(characterName)))
				{
					String[] data=resolveCastString(cast);
					if (data==null) continue;
					actorName=data[0];
					characterName=data[1];
				}
				castData=new CastData(actorName, characterName);
				castCache.put(cast, castData);
			}
			castList.add(castData);
		}
		return castList;
	}

	protected String[] resolveCastString(String cast)
	{
		progress.warning("Invalid cast pattern: "+cast);
		return null;
	}

	private static boolean isValidName(String text)
	{
		Stack<Character> stack=new Stack<Character>();
		Character lastOpened;
		for (int i=0;i<text.length();i++)
		{
			char ch=text.charAt(i);
			switch (ch)
			{
				case '(':
				case '[':
					stack.push(ch);
					break;
				case ')':
					if (stack.isEmpty()) return false;
					lastOpened=stack.pop();
					if (lastOpened==null || lastOpened.charValue()!='(') return false;
					break;
				case ']':
					if (stack.isEmpty()) return false;
					lastOpened=stack.pop();
					if (lastOpened==null || lastOpened.charValue()!='[') return false;
					break;
			}
		}
		return stack.isEmpty();
	}

	public void saveEpisode(final Episode episode, final EpisodeData data)
	{
		DBSession.execute(new MyTransactional()
		{
			public void run()
			{
				String oldCode=episode.getProductionCode();
				String newCode=data.getProductionCode();
				if (isEmpty(oldCode) && !isEmpty(newCode)) episode.setProductionCode(newCode);

				String oldSummary=episode.getSummaryText(language);
				String newSummary=data.getSummary();
				if (isEmpty(oldSummary) && !isEmpty(newSummary)) episode.setSummaryText(language, newSummary);

				Date oldAirdate=episode.getAirdate();
				Date newAirdate=data.getFirstAirdate();
				if (oldAirdate==null && newAirdate!=null) episode.setAirdate(newAirdate);
			}
		});
		saveCrew(episode, CrewMember.WRITER, data.getWrittenBy());
		saveCrew(episode, CrewMember.DIRECTOR, data.getDirectedBy());
		saveCrew(episode, CrewMember.STORY, data.getStoryBy());

		saveCast(episode, CastMember.MAIN_CAST, data.getMainCast());
		saveCast(show, CastMember.MAIN_CAST, data.getMainCast());
		saveCast(episode, CastMember.RECURRING_CAST, data.getRecurringCast());
		saveCast(show, CastMember.RECURRING_CAST, data.getRecurringCast());
		saveCast(episode, CastMember.GUEST_CAST, data.getGuestCast());
		progress.info(episode.getUserKey()+" "+episode.getTitle()+" updated.");
	}

	private void saveCast(final Production production, final int type, final List<CastData> castList)
	{
		if (castList!=null)
		{
			final Set<String> castNames=new HashSet<String>();
			for (CastMember castMember : production.getCastMembers(type))
			{
				castNames.add(castMember.getActor().getName());
			}
			if (type==CastMember.RECURRING_CAST)
			{
				for (CastMember castMember : production.getCastMembers(CastMember.MAIN_CAST))
				{
					castNames.add(castMember.getActor().getName());
				}
			}
			DBSession.execute(new MyTransactional()
			{
				public void run() throws Exception
				{
					Map<String, Person> cache=new HashMap<String, Person>();
					for (CastData castData : castList)
					{
						String actorName=StringUtils.trimString(castData.actor);
						if (!castNames.contains(actorName))
						{
							String character=StringUtils.trimString(castData.character);
							Person person=PersonManager.getInstance().getPersonByName(actorName, true);
							if (person==null) person=cache.get(actorName);
							if (person==null)
							{
								person=new Person();
								person.setName(actorName);
								person.setActor(true);
								cache.put(actorName, person);
							}
							CastMember castMember=new CastMember();
							castMember.setType(type);
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

	private void saveCrew(final Episode episode, final String type, final String[] crewList)
	{
		if (crewList!=null)
		{
			final Set<String> crewNames=new HashSet<String>();
			for (Iterator it=episode.getCrewMembers(type).iterator(); it.hasNext();)
			{
				CrewMember crewMember=(CrewMember)it.next();
				crewNames.add(crewMember.getPerson().getName());
			}
			DBSession.execute(new MyTransactional()
			{
				public void run() throws Exception
				{
					Map<String, Person> cache=new HashMap<String, Person>();
					for (int i=0; i<crewList.length; i++)
					{
						String name=StringUtils.trimString(crewList[i]);
						if (!crewNames.contains(name))
						{
							Person person=PersonManager.getInstance().getPersonByName(name, true);
							if (person==null) person=cache.get(name);
							if (person==null)
							{
								person=new Person();
								person.setName(name);
								cache.put(name, person);
							}
							CrewMember crewMember=new CrewMember();
							crewMember.setEpisode(episode);
							crewMember.setType(type);
							crewMember.setPerson(person);
						}
					}
				}
			});
		}
	}

	private static class EpisodeData implements ImportEpisode
	{
		private String episodeKey;
		private String episodeName;
		private Date airdate;
		private String productionCode;
		private String summary;
		private List<CastData> mainCast;
		private List<CastData> recurringCast;
		private List<CastData> guestCast;
		private String[] writtenBy;
		private String[] directedBy;
		private String[] storyBy;

		public EpisodeData(String episodeKey, String episodeName, Date airdate, String productionCode)
		{
			this.episodeKey=episodeKey;
			this.episodeName=episodeName;
			this.airdate=airdate;
			this.productionCode=productionCode;
		}

		public String getEpisodeKey()
		{
			return episodeKey;
		}

		public String getGermanEpisodeTitle()
		{
			return null;
		}

		public String getEpisodeTitle()
		{
			return episodeName;
		}

		public void setSummary(String summary)
		{
			this.summary=summary;
		}

		public String getSummary()
		{
			return summary;
		}

		public void setMainCast(List<CastData> mainCast)
		{
			this.mainCast=mainCast;
		}

		public void setRecurringCast(List<CastData> recurringCast)
		{
			this.recurringCast=recurringCast;
		}

		public void setGuestCast(List<CastData> guestCast)
		{
			this.guestCast=guestCast;
		}

		public void setWrittenBy(String[] writers)
		{
			this.writtenBy=writers;
		}

		public void setDirectedBy(String[] directors)
		{
			this.directedBy=directors;
		}

		public void setStoryBy(String[] storyBy)
		{
			this.storyBy=storyBy;
		}

		public Date getFirstAirdate()
		{
			return airdate;
		}

		public String getProductionCode()
		{
			return productionCode;
		}

		public List<CastData> getMainCast()
		{
			return mainCast;
		}

		public List<CastData> getRecurringCast()
		{
			return recurringCast;
		}

		public List<CastData> getGuestCast()
		{
			return guestCast;
		}

		public String[] getWrittenBy()
		{
			return writtenBy;
		}

		public String[] getDirectedBy()
		{
			return directedBy;
		}

		public String[] getStoryBy()
		{
			return storyBy;
		}
	}

	private static class CastData
	{
		private String actor;
		private String character;

		public CastData(String actor, String character)
		{
			this.actor=actor;
			this.character=character;
		}

		@Override
		public String toString()
		{
			return actor+" as "+character;
		}
	}

	private abstract class MyTransactional<T> implements Transactional
	{
		public T value;

		public void handleError(Throwable e)
		{
			progress.error(e.getClass().getSimpleName()+": "+e.getMessage());
		}
	}
}
