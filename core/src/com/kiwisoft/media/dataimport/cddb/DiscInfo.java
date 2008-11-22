package com.kiwisoft.media.dataimport.cddb;

/**
 * @author Stefan Stiller
*/
public class DiscInfo
{
	private String genre;
	private String discId;
	private String name;

	public DiscInfo(String genre, String discId, String name)
	{
		this.genre=genre;
		this.discId=discId;
		this.name=name;
	}

	public String getGenre()
	{
		return genre;
	}

	public String getDiscId()
	{
		return discId;
	}

	public String getName()
	{
		return name;
	}
}
