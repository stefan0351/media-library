package com.kiwisoft.media.dataImport;

import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.media.*;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import static com.kiwisoft.utils.StringUtils.isEmpty;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.progress.Job;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.cfg.SimpleConfiguration;

/**
 * @author Stefan Stiller
 */
public abstract class SerienJunkiesDeLoader implements Job
{
	public static final Pattern NUMBER_PATTERN=Pattern.compile("(\\d{2})x(\\d{2})");

	public static void main(String[] args) throws Exception
	{
		Locale.setDefault(Locale.GERMANY);
		File configFile=new File(FileUtils.getRootDirectory(Show.class), "config.xml");
		new SimpleConfiguration().loadDefaultsFromFile(configFile);

//		Jack & Jill,  Drake & Josh, Even Stevens
//		Malcolm in the Middle, Summerland       , "Raising Dad"
//		Grosse Point (Starlets), Dead Like Me, oc, the girl of tommorrow
// 		futurama
	}

	private ProgressSupport progress;

	private Show show;
	private String baseUrl;
	private int startSeason;
	private int endSeason;
	private boolean autoCreate;
	private Language german;
	public SimpleDateFormat airdateFormat;

	protected SerienJunkiesDeLoader(Show show, String baseUrl, int startSeason, int endSeason, boolean autoCreate)
	{
		this.show=show;
		this.baseUrl=baseUrl;
		this.startSeason=startSeason;
		this.endSeason=endSeason;
		this.autoCreate=autoCreate;
		this.german=LanguageManager.getInstance().getLanguageBySymbol("de");
		airdateFormat=new SimpleDateFormat("dd.MM.yyyy");
		airdateFormat.setTimeZone(DateUtils.GMT);
	}

	public String getName()
	{
		return "Load Episodes from SerienJunkies.de";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progress=new ProgressSupport(this, progressListener);
		loadMain(baseUrl);
		return true;
	}

	public void dispose() throws IOException
	{
	}

	private void loadMain(String baseUrl) throws IOException, InterruptedException
	{
		progress.startStep("Load episode list...");
		progress.initialize(false, 1, null);
		String page=WebUtils.loadURL(baseUrl);
//		FileUtils.saveToFile(page, new File("g:"+File.separator+"Stefan"+File.separator+"sj_episoden.html"));

		int index2=0;
		while (!progress.isStoppedByUser())
		{
			int index1=page.indexOf("<tr><td class=\"ep", index2);
			if (index1<0) break;
			index2=page.indexOf("</tr>", index1);
			if (index2<0) break;
			String htmlRow=page.substring(index1+4, index2);
			if (!htmlRow.startsWith("<td class=\"ephead\""))
			{
				List<String> values=XMLUtils.extractCellValues(htmlRow);

				EpisodeData episodeData=new EpisodeData();

				episodeData.airdate=convertDate(values.get(0));

				String text=values.get(1);
				Matcher matcher=NUMBER_PATTERN.matcher(text);
				if (matcher.matches())
				{
					int season=Integer.parseInt(matcher.group(1));
					if (season<startSeason) continue;
					if (season>endSeason) break;
					text=season+"."+Integer.parseInt(matcher.group(2));
				}
				episodeData.episodeKey=text;

				String origNameAndLink=values.get(2);
				String origName=XMLUtils.removeTags(origNameAndLink);
				origName=XMLUtils.unescapeHtml(origName);
				episodeData.origEpisodeName=StringUtils.trimString(origName);

				String nameAndLink=values.get(3);
				String name=XMLUtils.removeTags(nameAndLink);
				name=XMLUtils.unescapeHtml(name);
				episodeData.episodeName=StringUtils.trimString(name);
				XMLUtils.Tag tag=XMLUtils.getNextTag(nameAndLink, 0, "a");
				String link=null;
				if (tag!=null) link=XMLUtils.getAttribute(tag.text, "href");

//				Date germanFirstAired=convertDate(values.get(4));

				ShowManager showManager=ShowManager.getInstance();
				Episode episode=null;
				if (!isEmpty(episodeData.episodeName))
					episode=showManager.getEpisodeByName(show, episodeData.episodeName);
				if (episode==null && !isEmpty(episodeData.origEpisodeName))
					episode=showManager.getEpisodeByName(show, episodeData.origEpisodeName);
				if (episode==null) episode=autoCreateEpisode(show, episodeData);

				if (episode!=null)
				{
					if (!isEmpty(link)) loadSummary(episodeData, link);
					saveEpisode(episode, episodeData);
				}
				Thread.sleep(100); // To avoid DOS on the TV.com server
			}
		}
	}

	private Date convertDate(String text)
	{
		if (text!=null) text=XMLUtils.unescapeHtml(text);
		if (!StringUtils.isEmpty(text))
		{
			try
			{
				return airdateFormat.parse(text);
			}
			catch (ParseException e)
			{
				progress.error(e);
			}
		}
		return null;
	}

	private Episode autoCreateEpisode(final Show show, final EpisodeData data)
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
				progress.info("Episode "+transactional.value+" created.");
				return transactional.value;
			}
			else
			{
				progress.error("Creation of episode "+data.getEpisodeTitle()+" failed.");
				return null;
			}
		}
		else return createEpisode(show, data);
	}

	protected abstract Episode createEpisode(Show show, ImportEpisode info);

	private void loadSummary(EpisodeData episodeData, String episodeLink) throws IOException
	{
		String page=WebUtils.loadURL("http://www.serienjunkies.de"+episodeLink);
//		FileUtils.saveToFile(page, new File("g:"+File.separator+"Stefan"+File.separator+"sj_episode_"+episodeData.episodeKey+".html"));

		int index1=page.indexOf("<td class=\"episodetext2");
		index1=page.indexOf(">", index1)+1;
		int index2=page.indexOf("</td>", index1);
		String summary=page.substring(index1, index2).replace("\n", "").replace("<br />", "\n");
		episodeData.summary=XMLUtils.unescapeHtml(summary).trim();
	}

	public void saveEpisode(final Episode episode, final EpisodeData data)
	{
		boolean success=DBSession.execute(new MyTransactional()
		{
			public void run()
			{
				String oldName=episode.getGermanTitle();
				String newName=data.getGermanEpisodeTitle();
				if (isEmpty(oldName) && !isEmpty(newName)) episode.setGermanTitle(newName);

				String oldOrigName=episode.getTitle();
				String newOrigName=data.getEpisodeTitle();
				if (isEmpty(oldOrigName) && !isEmpty(newOrigName)) episode.setTitle(newOrigName);

				Date oldDate=episode.getAirdate();
				Date newDate=data.getFirstAirdate();
				if (oldDate==null && newDate!=null)
					episode.setAirdate(newDate);
				else if (oldDate!=null && newDate!=null && !oldDate.equals(newDate))
					progress.warning("Different valules for 'First Aired' found."+
							"\n\tDatabase: "+airdateFormat.format(oldDate)+
							"\n\tSerienJunkies.de: "+airdateFormat.format(newDate));

				String oldSummary=episode.getSummaryText(german);
				String newSummary=data.summary;
				if (isEmpty(oldSummary) && !isEmpty(newSummary)) episode.setSummaryText(german, newSummary);
			}
		});
		if (success) progress.info("Episode "+episode+" updated.");
	}

	private static class EpisodeData implements ImportEpisode
	{
		private String episodeKey;
		private String episodeName;
		private String origEpisodeName;
		private Date airdate;
		private String summary;

		public EpisodeData()
		{
		}

		public String getEpisodeKey()
		{
			return episodeKey;
		}

		public String getGermanEpisodeTitle()
		{
			return episodeName;
		}

		public String getEpisodeTitle()
		{
			return origEpisodeName;
		}

		public Date getFirstAirdate()
		{
			return airdate;
		}

		public String getProductionCode()
		{
			return null;
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
