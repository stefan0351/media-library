package com.kiwisoft.media.medium;

import java.util.Set;
import java.util.Collections;

import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.Credit;

public class Song extends IDObject implements Production
{
	public static final String TITLE="title";
	public static final String VERSION="songVersion";

	private String title;
	private String songVersion;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		String oldTitle=this.title;
		this.title=title;
		setModified(TITLE, oldTitle, title);
	}

	public String getSongVersion()
	{
		return songVersion;
	}

	public void setSongVersion(String songVersion)
	{
		String oldVersion=this.songVersion;
		this.songVersion=songVersion;
		setModified(VERSION, oldVersion, songVersion);
	}

	public Set<CastMember> getCastMembers(CreditType type)
	{
		return Collections.emptySet();
	}

	public Set<Credit> getCredits(CreditType type)
	{
		return DBLoader.getInstance().loadSet(Credit.class, null, "song_id=? and credit_type_id=?", getId(), type.getId());
	}
}
