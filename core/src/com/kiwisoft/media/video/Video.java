/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:04:17 PM
 */
package com.kiwisoft.media.video;

import java.util.Iterator;

import com.kiwisoft.utils.db.*;

public class Video extends IDObject
{
	public static final String RECORDINGS="recordings";
	public static final String TYPE="type";

	private String userKey;
	private String name;
	private int length;
	private int remainingLength;
	private Chain<Recording> recordings;

	public Video()
	{
	}

	public Video(DBDummy dummy)
	{
		super(dummy);
	}

	public String getUserKey()
	{
		return userKey;
	}

	public void setUserKey(String userKey)
	{
		this.userKey=userKey;
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

	public Recording createRecording()
	{
		Recording recording=new Recording(this);
		getRecordings().addNew(recording);
		fireElementAdded(RECORDINGS, recording);
		return recording;
	}

	public void dropRecording(Recording recording)
	{
		getRecordings().remove(recording);
		recording.delete();
		fireElementRemoved(RECORDINGS, recording);
	}

	public int getRecordingIndex(Recording recording)
	{
		return getRecordings().indexOf(recording);
	}

	public Chain<Recording> getRecordings()
	{
		if (recordings==null)
			recordings=new Chain<Recording>(DBLoader.getInstance().loadSet(Recording.class, null, "video_id=?", getId()));
		return recordings;
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
		Iterator<Recording> it=getRecordings().iterator();
		while (it.hasNext()) it.next().delete();
	}

	public Identifyable loadReference(String name, Long referenceId)
	{
		if (TYPE.equals(name)) return MediumType.get(referenceId);
		return super.loadReference(name, referenceId);
	}

	public void afterReload()
	{
		recordings=null;
		super.afterReload();
	}
}
