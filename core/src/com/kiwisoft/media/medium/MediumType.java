/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 11:16:36 PM
 */
package com.kiwisoft.media.medium;

import java.util.*;

import com.kiwisoft.utils.Identifyable;

public class MediumType implements Identifyable
{
	public static final Map<Long, MediumType> map=new HashMap<Long, MediumType>();

	public static final MediumType VHS=new MediumType(new Long(2), "VHS", "Videos", "V", true);
	public static final MediumType VHS_ORIGINAL=new MediumType(new Long(1), "VHS(O)", "Videos - Original", "V");
	public static final MediumType VIDEO_CD=new MediumType(new Long(3), "VCD", "Video-CD's", "C");
	public static final MediumType DVD=new MediumType(new Long(4), "DVD", "DVD's", "D");
	public static final MediumType DVD_ORIGINAL=new MediumType(new Long(5), "DVD(O)", "Original DVD's", "D");
	public static final MediumType DVD_RW=new MediumType(new Long(6), "DVD(RW)", "Rewritable DVD's", "D", true);
	public static final MediumType HDR=new MediumType(new Long(7), "HDR", "Hard Disk Recorder", "H", true);
	public static final MediumType AUDIO_CD=new MediumType(new Long(8), "Audio-CD", "Audio-CD's", "A");
	public static final MediumType HD=new MediumType(new Long(9), "HD", "Hard Disk", "H", true);

	public static MediumType valueOf(Long id)
	{
		return map.get(id);
	}

	public static Collection<MediumType> values()
	{
		return map.values();
	}

	private Long id;
	private String name;
	private String pluralName;
	private boolean rewritable;
	private String userKeyPrefix;

	private MediumType(Long id, String name, String pluralName, String userKeyPrefix)
	{
		this(id, name, pluralName, userKeyPrefix, false);
		this.userKeyPrefix=userKeyPrefix;
	}

	private MediumType(Long id, String name, String pluralName, String userKeyPrefix, boolean rewritable)
	{
		this.id=id;
		this.name=name;
		this.pluralName=pluralName;
		this.userKeyPrefix=userKeyPrefix;
		this.rewritable=rewritable;
		map.put(id, this);
	}

	public Long getId()
	{
		return id;
	}

	public boolean isRewritable()
	{
		return rewritable;
	}

	public String getPluralName()
	{
		return pluralName;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public String getUserKeyPrefix()
	{
		return userKeyPrefix;
	}

	@Override
	public Object getPrimaryKey()
	{
		return getId();
	}
}
