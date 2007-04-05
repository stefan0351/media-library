package com.kiwisoft.media.dataImport;

import static com.kiwisoft.utils.xml.XMLUtils.removeTags;
import static com.kiwisoft.utils.xml.XMLUtils.unescapeHtml;
import static com.kiwisoft.utils.StringUtils.trimString;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.person.*;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.utils.DateUtils;
import static com.kiwisoft.utils.StringUtils.isEmpty;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;

/**
 * @author Stefan Stiller
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
	private Pattern nameLinkPattern;
	private Map<String, Person> personCache;

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
		nameLinkPattern=Pattern.compile("http://www.tv.com/.*/person/([0-9]+)/summary.html");
		personCache=new HashMap<String, Person>();
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
		content=unescapeHtml(content);
		ImportUtils.replaceHtmlFormatTags(content);
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
				if ("Writer:".equals(creditName) || "Director:".equals(creditName) || "Story:".equals(creditName))
				{
					for (String html : creditValue.split("</a>,"))
					{
						String key=getNameLink(html);
						String name=convertHTML(html);
						PersonData person=new PersonData(key, name);
						if ("Writer:".equals(creditName)) episodeData.addWrittenBy(person);
						else if ("Director:".equals(creditName)) episodeData.addDirectedBy(person);
						else if ("Story:".equals(creditName)) episodeData.addStoryBy(person);
					}
				}
				else if ("Star:".equals(creditName) || "Recurring Role:".equals(creditName) || "Guest Star:".equals(creditName))
				{
					for (String html : creditValue.split(",&nbsp;"))
					{
						String key=getNameLink(html);
						int index=html.indexOf("</a>");
						String role=convertHTML(html.substring(index));
						if (role.startsWith("(") && role.endsWith(")")) role=role.substring(1, role.length()-1);
						String name=convertHTML(html.substring(0, index));
						if (!isValidName(name) || !isValidName(role))
						{
							String[] data=resolveCastString(convertHTML(html));
							if (data==null) continue;
							name=data[0];
							role=data[1];
						}
						PersonData person=new PersonData(key, name);
						CastData cast=new CastData(person, role);
						if ("Star:".equals(creditName)) episodeData.addMainCast(cast);
						else if ("Recurring Role:".equals(creditName)) episodeData.addRecurringCast(cast);
						else if ("Guest Star:".equals(creditName)) episodeData.addGuestCast(cast);
					}
				}
				else progress.warning("Unknown credit: "+creditName+"="+creditValue);
			}
			index1=index2;
		}
		saveEpisode(episode, episodeData);
	}

	protected String[] resolveCastString(String cast)
	{
		progress.warning("Invalid cast pattern: "+cast);
		return null;
	}

	private static String convertHTML(String html)
	{
		return trimString(unescapeHtml(removeTags(html)));
	}

	private static boolean isValidName(String text)
	{
		Stack<Character> stack=new Stack<Character>();
		Character lastOpened;
		for (int i=0; i<text.length(); i++)
		{
			char ch=text.charAt(i);
			switch (ch)
			{
				case'(':
				case'[':
					stack.push(ch);
					break;
				case')':
					if (stack.isEmpty()) return false;
					lastOpened=stack.pop();
					if (lastOpened==null || lastOpened.charValue()!='(') return false;
					break;
				case']':
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
//		System.out.println("TVComLoader.saveEpisode: data.getDirectedBy() = "+data.getDirectedBy());
//		System.out.println("TVComLoader.saveEpisode: data.getWrittenBy() = "+data.getWrittenBy());
//		System.out.println("TVComLoader.saveEpisode: data.getStoryBy() = "+data.getStoryBy());
//		System.out.println("TVComLoader.saveEpisode: data.getMainCast() = "+data.getMainCast());
//		System.out.println("TVComLoader.saveEpisode: data.getRecurringCast() = "+data.getRecurringCast());
//		System.out.println("TVComLoader.saveEpisode: data.getGuestCast() = "+data.getGuestCast());
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
			for (Iterator it=episode.getCrewMembers(type).iterator(); it.hasNext();)
			{
				CrewMember crewMember=(CrewMember)it.next();
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
							CrewMember crewMember=new CrewMember();
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

	private String getNameLink(String html)
	{
		Matcher keyMatcher=nameLinkPattern.matcher(XMLUtils.getAttribute(html, "href"));
		if (keyMatcher.matches()) return keyMatcher.group(1);
		return null;
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
		private List<PersonData> writtenBy;
		private List<PersonData> directedBy;
		private List<PersonData> storyBy;

		public EpisodeData(String episodeKey, String episodeName, Date airdate, String productionCode)
		{
			this.episodeKey=episodeKey;
			this.episodeName=episodeName;
			this.airdate=airdate;
			this.productionCode=productionCode;
			writtenBy=new ArrayList<PersonData>();
			directedBy=new ArrayList<PersonData>();
			storyBy=new ArrayList<PersonData>();
			mainCast=new ArrayList<CastData>();
			recurringCast=new ArrayList<CastData>();
			guestCast=new ArrayList<CastData>();
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

		public List<PersonData> getWrittenBy()
		{
			return writtenBy;
		}

		public List<PersonData> getDirectedBy()
		{
			return directedBy;
		}

		public List<PersonData> getStoryBy()
		{
			return storyBy;
		}

		public void addWrittenBy(PersonData personData)
		{
			writtenBy.add(personData);
		}

		public void addDirectedBy(PersonData person)
		{
			directedBy.add(person);
		}

		public void addStoryBy(PersonData person)
		{
			storyBy.add(person);
		}

		public void addMainCast(CastData cast)
		{
			mainCast.add(cast);
		}

		public void addRecurringCast(CastData cast)
		{
			recurringCast.add(cast);
		}

		public void addGuestCast(CastData cast)
		{
			guestCast.add(cast);
		}
	}

	private static class PersonData
	{
		private String name;
		private String key;

		public PersonData(String key, String actor)
		{
			
			this.key=key;
			this.name=actor;
		}

		@Override
		public String toString()
		{
			return name+"["+key+"]";
		}
	}

	private static class CastData
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

		public void handleError(Throwable e)
		{
			progress.error(e.getClass().getSimpleName()+": "+e.getMessage());
		}
	}
}
