/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:04:17 PM
 */
package com.kiwisoft.media.medium;

import java.util.Iterator;

import com.kiwisoft.utils.Identifyable;
import com.kiwisoft.collection.Chain;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.SequenceManager;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.DBLoader;

public class Medium extends IDObject
{
	public static final String TRACKS="tracks";
	public static final String TYPE="type";

	private Integer userKey;
	private String name;
	private int length;
	private int remainingLength;
	private Chain<Track> tracks;
	private String storage;
	private boolean obsolete;

	public Medium()
	{
		setUserKey((int)SequenceManager.getSequence("medium").next());
	}

	public Medium(DBDummy dummy)
	{
		super(dummy);
	}

	public String getFullKey()
	{
		if (userKey!=null)
		{
			StringBuilder builder=new StringBuilder();
			MediumType type=getType();
			if (type!=null) builder.append(type.getUserKeyPrefix());
			builder.append(userKey);
			return builder.toString();
		}
		return null;
	}

	public Integer getUserKey()
	{
		return userKey;
	}

	public void setUserKey(Integer userKey)
	{
		this.userKey=userKey;
		setModified();
	}

	public String getStorage()
	{
		return storage;
	}

	public void setStorage(String storage)
	{
		this.storage=storage;
		setModified();
	}


	public boolean isObsolete()
	{
		return obsolete;
	}

	public void setObsolete(boolean obsolete)
	{
		this.obsolete=obsolete;
		setModified();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
		setModified();
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length=length;
		setModified();
	}

	public Track createTrack()
	{
		Track track=new Track(this);
		getTracks().addNew(track);
		fireElementAdded(TRACKS, track);
		return track;
	}

	public void dropTrack(Track track)
	{
		getTracks().remove(track);
		track.delete();
		fireElementRemoved(TRACKS, track);
	}

	public int getRecordingIndex(Track track)
	{
		return getTracks().indexOf(track);
	}

	public Chain<Track> getTracks()
	{
		if (tracks==null)
			tracks=new Chain<Track>(DBLoader.getInstance().loadSet(Track.class, null, "medium_id=?", getId()));
		return tracks;
	}

	public MediumType getType()
	{
		return (MediumType)getReference(TYPE);
	}

	public void setType(MediumType newValue)
	{
		setReference(TYPE, newValue);
	}

	public int getRemainingLength()
	{
		return remainingLength;
	}

	public void setRemainingLength(int remainingLength)
	{
		this.remainingLength=remainingLength;
		setModified();
	}

	public void delete()
	{
		super.delete();
		Iterator<Track> it=getTracks().iterator();
		while (it.hasNext()) it.next().delete();
	}

	public Identifyable loadReference(String name, Object referenceId)
	{
		if (TYPE.equals(name)) return MediumType.get((Long)referenceId);
		return super.loadReference(name, referenceId);
	}

	public void afterReload()
	{
		tracks=null;
		super.afterReload();
	}
}
