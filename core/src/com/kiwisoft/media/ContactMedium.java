/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 21, 2003
 * Time: 6:56:46 PM
 */
package com.kiwisoft.media;

import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.media.fanfic.Author;

public class ContactMedium extends IDObject
{
	public static final int WEB=1;
	public static final int MAIL=2;

	public static final String AUTHOR="author";

	private int type;
	private String value;

	public ContactMedium(Author author, int type)
	{
		setReference(AUTHOR, author);
		this.type=type;
	}

	public ContactMedium(DBDummy dummy)
	{
		super(dummy);
	}

	public int getType()
	{
		return type;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value=value;
		setModified();
	}

	public Author getAuthor()
	{
		return (Author)getReference(AUTHOR);
	}

	public void setAuthor(Author author)
	{
		setReference(AUTHOR, author);
	}
}
