package com.kiwisoft.media.dataimport;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.person.Person;

/**
 * @author Stefan Stiller
*/
class TvTvDeAirdateData
{
	private Date time;
	private String title;
	private String channelName;
	private String subTitle;
	private String detailLink;
	private Show show;
	private Movie movie;
	private Channel channel;
	private List<TvTvDeEpisodeData> episodes=new ArrayList<TvTvDeEpisodeData>();
	private String cast;
	private Integer length;
	private Person person;
	private String channelKey;
	private String channelLogo;

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (o==null || getClass()!=o.getClass()) return false;

		TvTvDeAirdateData that=(TvTvDeAirdateData)o;

		if (channelName!=null ? !channelName.equals(that.channelName) : that.channelName!=null) return false;
		if (time!=null ? !time.equals(that.time) : that.time!=null) return false;
		if (title!=null ? !title.equals(that.title) : that.title!=null) return false;

		return true;
	}

	public int hashCode()
	{
		int result;
		result=(time!=null ? time.hashCode() : 0);
		result=31*result+(title!=null ? title.hashCode() : 0);
		result=31*result+(channelName!=null ? channelName.hashCode() : 0);
		return result;
	}

	public void setTime(Date time)
	{
		this.time=time;
	}

	public String getCast()
	{
		return cast;
	}

	public void setCast(String cast)
	{
		this.cast=cast;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(Channel channel)
	{
		this.channel=channel;
	}

	public String getChannelKey()
	{
		return channelKey;
	}

	public void setChannelKey(String channelKey)
	{
		this.channelKey=channelKey;
	}

	public String getChannelLogo()
	{
		return channelLogo;
	}

	public void setChannelLogo(String channelLogo)
	{
		this.channelLogo=channelLogo;
	}

	public String getChannelName()
	{
		return channelName;
	}

	public void setChannelName(String channelName)
	{
		this.channelName=channelName;
	}

	public String getDetailLink()
	{
		return detailLink;
	}

	public void setDetailLink(String detailLink)
	{
		this.detailLink=detailLink;
	}

	public List<TvTvDeEpisodeData> getEpisodes()
	{
		return episodes;
	}

	public void setEpisodes(List<TvTvDeEpisodeData> episodes)
	{
		this.episodes=episodes;
	}

	public Integer getLength()
	{
		return length;
	}

	public void setLength(Integer length)
	{
		this.length=length;
	}

	public Movie getMovie()
	{
		return movie;
	}

	public void setMovie(Movie movie)
	{
		this.movie=movie;
	}

	public Person getPerson()
	{
		return person;
	}

	public void setPerson(Person person)
	{
		this.person=person;
	}

	public Show getShow()
	{
		return show;
	}

	public void setShow(Show show)
	{
		this.show=show;
	}

	public String getSubTitle()
	{
		return subTitle;
	}

	public void setSubTitle(String subTitle)
	{
		this.subTitle=subTitle;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title=title;
	}

	public Date getTime()
	{
		return time;
	}

	public void addEpisode(TvTvDeEpisodeData episodeData)
	{
		episodes.add(episodeData);
	}
}
