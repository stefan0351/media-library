package com.kiwisoft.utils.websearch;

/**
 * @author Stefan Stiller
 * @since 19.02.11
 */
public class WebSearchResult
{
	private String title;
	private String url;
	private String description;

	public WebSearchResult(String title, String url)
	{
		this.title=title;
		this.url=url;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description=description;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title=title;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url=url;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb=new StringBuilder();
		sb.append("GoogleResult");
		sb.append("{title='").append(title).append('\'');
		sb.append(", url='").append(url).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
