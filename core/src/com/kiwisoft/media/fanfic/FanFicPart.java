/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media.fanfic;

import java.io.File;

import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;

public class FanFicPart extends IDObject implements ChainLink
{
	public static final String FANFIC="fanFic";
	public static final String SEQUENCE="sequence";

	private String source;
	private int sequence;

	public FanFicPart(FanFic fanFic)
	{
		setFanFic(fanFic);
	}

	public FanFicPart(DBDummy dummy)
	{
		super(dummy);
	}

	public FanFic getFanFic()
	{
		return (FanFic)getReference(FANFIC);
	}

	private void setFanFic(FanFic fanFic)
	{
		setReference(FANFIC, fanFic);
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		String oldSource=this.source;
		this.source=source;
		setModified("source", oldSource, this.source);
	}

	public long getSize()
	{
		try
		{
			File file=FileUtils.getFile(MediaConfiguration.getFanFicPath(), source);
			return file.length();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public int getSequence()
	{
		return sequence;
	}

	public void setSequence(int sequence)
	{
		int oldSequence=getSequence();
		this.sequence=sequence;
		setModified(SEQUENCE, oldSequence, sequence);
	}

	public void setChainPosition(int position)
	{
		setSequence(position);
	}

	public int getChainPosition()
	{
		return getSequence();
	}

	public String toString()
	{
		return source;
	}

	public static class Comparator implements java.util.Comparator
	{
		public int compare(Object o1, Object o2)
		{
			FanFicPart e1=(FanFicPart)o1;
			FanFicPart e2=(FanFicPart)o2;
			if (e1.getSequence()<e2.getSequence()) return -1;
			if (e1.getSequence()>e2.getSequence()) return 1;
			return 0;
		}
	}
}
