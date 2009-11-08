package com.kiwisoft.media.dataimport;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.awt.Dimension;

import com.kiwisoft.media.*;
import com.kiwisoft.media.files.*;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.DateUtils;

public class TVTVDeLoader implements Job
{
	static final String BASE_URL="http://www.tvtv.de/tvtv/";
	static final String SEARCH_URL=BASE_URL
										   +"index.vm?mainTemplate=web/search_result.vm&search_input={0}&x=0&y=0&lang=de"
										   +"&search_psel=display_all&as_rgrp=progStartTime&as_rsort=progStart";

	private List<Object> objects;
	private ProgressSupport progressSupport=new ProgressSupport(this, null);
	private Date startDate;
	private boolean dryRun;
	private Set<String> ignoredChannels=new HashSet<String>();

	public TVTVDeLoader(List<Object> objects)
	{
		this.objects=objects;
	}

	public void setDryRun(boolean dryRun)
	{
		this.dryRun=dryRun;
	}

	public boolean isDryRun()
	{
		return dryRun;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	@Override
	public String getName()
	{
		return "Load Schedule from TVTV.de";
	}

	public ProgressSupport getProgressSupport()
	{
		return progressSupport;
	}

	@Override
	public boolean run(ProgressListener progressListener) throws Exception
	{
		progressSupport=new ProgressSupport(this, progressListener);

		startDate=new Date();

		List<Object> objects=new ArrayList<Object>();
		if (this.objects==null)
		{
			progressSupport.startStep("Load search patterns...");
			Collection patterns=SearchManager.getInstance().getSearchPattern(SearchPattern.TVTV);

			Iterator it=patterns.iterator();
			while (it.hasNext() && !progressSupport.isStoppedByUser())
			{
				SearchPattern pattern=(SearchPattern)it.next();
				Object reference=pattern.getReference();
				if (reference!=null) objects.add(reference);
				progressSupport.progress(1, true);
			}
		}
		else objects.addAll(this.objects);

		Collections.sort(objects, new Comparator<Object>()
		{
			@Override
			public int compare(Object o1, Object o2)
			{
				int result=getClassPriority(o1).compareTo(getClassPriority(o2));
				if (result==0) result=o1.toString().compareToIgnoreCase(o2.toString());
				return result;
			}

			private Integer getClassPriority(Object o1)
			{
				if (o1 instanceof Show) return 1;
				if (o1 instanceof Movie) return 2;
				if (o1 instanceof Person) return 3;
				else return 4;
			}
		});

//		objects=objects.subList(90, objects.size());
		Iterator it=objects.iterator();
		progressSupport.startStep("Load schedules...");
		progressSupport.initialize(true, objects.size(), null);
		while (it.hasNext() && !progressSupport.isStoppedByUser())
		{
			Object object=it.next();
			if (object instanceof Show) new TvTvDeShowHandler(this, (Show)object).loadSchedule();
			else if (object instanceof Movie) new TvTvDeMovieHandler(this, (Movie)object).loadSchedule();
			else if (object instanceof Person) new TvTvDePersonHandler(this, (Person)object).loadSchedule();
			else progressSupport.warning("Unhandled object "+object.getClass());
			progressSupport.progress(1, true);
		}
		return true;
	}

	@Override
	public void dispose() throws IOException
	{
	}

	Episode findEpisode(Show show, String title)
	{
		Episode episode1=null;
		try
		{
			episode1=ShowManager.getInstance().getEpisodeByName(show, title);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			progressSupport.error(e);
		}
		return episode1;
	}

	Channel getChannel(final TvTvDeAirdateData airdate)
	{
		Channel channel=ChannelManager.getInstance().getChannelByTvtvKey(airdate.getChannelKey());
		if (channel==null)
		{
			if (!StringUtils.isEmpty(airdate.getChannelKey()))
			{
				if (!ignoredChannels.contains(airdate.getChannelKey()))
				{
					if (askMissingChannel(airdate.getChannelName(), airdate.getChannelKey()))
					{
						final String logoPath="icons"+File.separator+"tv"+File.separator+"logo"+airdate.getChannelKey()+".jpg";
						final File logoFile=FileUtils.getFile(MediaConfiguration.getRootPath(), logoPath);
						try
						{
							byte[] logoData=ImportUtils.loadUrlBinary(airdate.getChannelLogo());
							FileUtils.saveToFile(logoData, logoFile);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							progressSupport.warning("Error loading channel logo "+e.getMessage());
						}
						CreateChannelTransaction transaction=new CreateChannelTransaction(airdate, logoFile, logoPath);
						boolean success=DBSession.execute(transaction);
						if (!success) progressSupport.error("Failed to create channel "+airdate.getChannelName());
						else
						{
							channel=transaction.channel;
							progressSupport.info("Created channel "+airdate.getChannelName());
						}
					}
					else
					{
						ignoredChannels.add(airdate.getChannelKey());
					}
				}
			}
		}
		else
		{
			if (!airdate.getChannelName().equals(channel.getName()) && !ignoredChannels.contains(airdate.getChannelKey()))
			{
				if (askUpdateChannel(airdate.getChannelKey(), channel.getName(), airdate.getChannelName()))
				{
					final Channel finalChannel=channel;
					boolean success=DBSession.execute(new Transactional()
					{
						@Override
						public void run() throws Exception
						{
							finalChannel.setName(airdate.getChannelName());
						}

						@Override
						public void handleError(Throwable throwable, boolean rollback)
						{
							progressSupport.error(throwable);
						}
					});
					if (!success) progressSupport.error("Failed to update channel "+channel.getName());

				}
			}
		}
		return channel;
	}

	protected boolean askMissingChannel(String channelName, String channelKey)
	{
		return false;
	}

	protected boolean askUpdateChannel(String channelKey, String oldChannelName, String newChannelName)
	{
		return false;
	}

	Airdate createAirdate(TvTvDeAirdateData airingData, Date date, Integer length)
	{
		Set<Airdate> airdates=AirdateManager.getInstance().getAirdates(airingData.getChannel(), date);
		Airdate airdate;
		if (!airdates.isEmpty()) airdate=airdates.iterator().next();
		else
		{
			airdate=new Airdate();
			airdate.setChannel(airingData.getChannel());
			airdate.setDate(date);
		}
		airdate.setDetailsLink(airingData.getDetailLink());
		airdate.setShow(airingData.getShow());
		airdate.setMovie(airingData.getMovie());
		if (length==null) length=90;
		airdate.setEndDate(DateUtils.add(date, Calendar.MINUTE, length));
		if (airingData.getPerson()!=null) airdate.addPerson(airingData.getPerson());
		airdate.setLanguage(airingData.getChannel().getLanguage());
		airdate.setDataSource(DataSource.TVTV);
		return airdate;
	}

	private class CreateChannelTransaction implements Transactional
	{
		private final TvTvDeAirdateData airdate;
		private final File logoFile;
		private final String logoPath;
		public Channel channel;

		public CreateChannelTransaction(TvTvDeAirdateData airdate, File logoFile, String logoPath)
		{
			this.airdate=airdate;
			this.logoFile=logoFile;
			this.logoPath=logoPath;
		}

		@Override
		public void run() throws Exception
		{
			channel=ChannelManager.getInstance().createChannel();
			channel.setName(airdate.getChannelName());
			channel.setTvtvKey(airdate.getChannelKey());
			channel.setReceivable(true);

			if (logoFile!=null && logoFile.exists())
			{
				Dimension logoSize=MediaFileUtils.getImageSize(logoFile);
				if (logoSize!=null)
				{
					MediaFile logo=MediaFileManager.getInstance().createImage(MediaConfiguration.PATH_ROOT);
					logo.setName(airdate.getChannelName()+" - Logo");
					logo.setContentType(ContentType.LOGO);
					logo.setFile(logoPath);
					logo.setWidth(logoSize.width);
					logo.setHeight(logoSize.height);
					channel.setLogo(logo);
				}
			}
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			progressSupport.error(throwable);
		}
	}
}
