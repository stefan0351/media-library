package com.kiwisoft.media.dataimport;

import java.util.List;

/**
 * @author Stefan Stiller
 * @since 24.07.2010
 */
public class FanFicData
{
	private String title;
	private String author;
	private String authorUrl;
	private int chapterCount;
	private List<String> chapters;
	private String summary;
	private boolean complete;
	private String domainUrl;
	private String domain;
	private String rating;

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author=author;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title=title;
	}

	public int getChapterCount()
	{
		return chapterCount;
	}

	public void setChapterCount(int chapterCount)
	{
		this.chapterCount=chapterCount;
	}

	public List<String> getChapters()
	{
		return chapters;
	}

	public void setChapters(List<String> chapters)
	{
		this.chapters=chapters;
	}

	public void setSummary(String summary)
	{
		this.summary=summary;
	}

	public String getSummary()
	{
		return summary;
	}

	public void setComplete(boolean complete)
	{
		this.complete=complete;
	}

	public boolean isComplete()
	{
		return complete;
	}

	public void setDomain(String domain)
	{
		this.domain=domain;
	}

	public String getDomain()
	{
		return domain;
	}

	public void setDomainUrl(String domainUrl)
	{
		this.domainUrl=domainUrl;
	}

	public String getDomainUrl()
	{
		return domainUrl;
	}

	public void setAuthorUrl(String authorUrl)
	{
		this.authorUrl=authorUrl;
	}

	public String getAuthorUrl()
	{
		return authorUrl;
	}

	public void setRating(String rating)
	{
		this.rating=rating;
	}

	public String getRating()
	{
		return rating;
	}
}
