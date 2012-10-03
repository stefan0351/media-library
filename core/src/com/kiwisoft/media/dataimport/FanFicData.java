package com.kiwisoft.media.dataimport;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
	private Set<String> domains;
	private String rating;
	private Date publishedDate;
	private String language;
	private List<String> characters;
	private List<String> genres;

	public Date getPublishedDate()
	{
		return publishedDate;
	}

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
		if (domain==null) domains=null;
		else domains=Collections.singleton(domain);
	}

	public Set<String> getDomains()
	{
		return domains;
	}

	public void setDomains(Set<String> domains)
	{
		this.domains=domains;
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

	@Override
	public String toString()
	{
		final StringBuilder sb=new StringBuilder();
		sb.append("FanFicData");
		sb.append("{title='").append(title).append('\'');
		sb.append(", author='").append(author).append('\'');
		sb.append(", authorUrl='").append(authorUrl).append('\'');
		sb.append(", chapterCount=").append(chapterCount);
		sb.append(", chapters=").append(chapters);
		sb.append(", complete=").append(complete);
		sb.append(", domain='").append(domains).append('\'');
		sb.append(", domainUrl='").append(domainUrl).append('\'');
		sb.append(", rating='").append(rating).append('\'');
		sb.append(", summary='").append(summary).append('\'');
		sb.append('}');
		return sb.toString();
	}

	public void setPublishedDate(Date publishedDate)
	{
		this.publishedDate=publishedDate;
	}

	public void setLanguage(String language)
	{
		this.language=language;
	}

	public void setGenres(List<String> genres)
	{
		this.genres=genres;
	}

	public void setCharacters(List<String> characters)
	{
		this.characters=characters;
	}

	public List<String> getCharacters()
	{
		return characters;
	}

	public List<String> getGenres()
	{
		return genres;
	}

	public String getLanguage()
	{
		return language;
	}
}
