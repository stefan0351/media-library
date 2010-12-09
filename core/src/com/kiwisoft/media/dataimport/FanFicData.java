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
	private int chapterCount;
	private List<String> chapters;
	private String summary;
	private boolean complete;

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
}
