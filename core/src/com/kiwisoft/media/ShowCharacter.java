/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 10:09:20 PM
 */
package com.kiwisoft.media;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.Identifyable;

public class ShowCharacter extends IDObject
{
	public static final String NAME="name";
	public static final String NICK_NAME="nickName";
	public static final String SEX="sex";

	private String name;
	private String nickName;

	public ShowCharacter()
	{
	}

	public ShowCharacter(DBDummy dummy)
	{
		super(dummy);
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

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName=nickName;
		setModified();
	}

	public Sex getSex()
	{
		return (Sex)getReference(SEX);
	}

	public void setSex(Sex newValue)
	{
		setReference(SEX, newValue);
	}

	public String toString()
	{
		return getName();
	}

	public Identifyable loadReference(String name, Long referenceId)
	{
		if (SEX.equals(name)) return Sex.get(referenceId);
		return super.loadReference(name, referenceId);
	}

	public boolean isUsed()
	{
		return super.isUsed() || CharacterManager.getInstance().isCharacterUsed(this);
	}
}
