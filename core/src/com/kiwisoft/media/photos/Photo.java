package com.kiwisoft.media.photos;

import java.util.Date;
import java.io.File;

import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.DBDummy;

public class Photo extends IDObject
{
	public static final String ROTATION="rotation";
	public static final String BOOK="book";

	private String originalFile;
	private int originalWidth;
	private int originalHeight;
	private Date creationDate;
	private int rotation;

	public Photo(PhotoBook book)
	{
		setBook(book);
	}

	public Photo(DBDummy dummy)
	{
		super(dummy);
	}

	public Photo(PhotoBook book, File file)
	{
		this(book);
		setOriginalFile(file.getAbsolutePath());
	}

	public PhotoBook getBook()
	{
		return (PhotoBook)getReference(BOOK);
	}

	public void setBook(PhotoBook book)
	{
		setReference(BOOK, book);
	}

	public String getOriginalFile()
	{
		return originalFile;
	}

	public void setOriginalFile(String originalFile)
	{
		this.originalFile=originalFile;
		setModified();
	}

	public int getOriginalWidth()
	{
		return originalWidth;
	}

	public void setOriginalWidth(int originalWidth)
	{
		this.originalWidth=originalWidth;
		setModified();
	}

	public int getOriginalHeight()
	{
		return originalHeight;
	}

	public void setOriginalHeight(int originalHeight)
	{
		this.originalHeight=originalHeight;
		setModified();
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Date creationDate)
	{
		this.creationDate=creationDate;
		setModified();
	}

	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rotation)
	{
		int oldRotation=this.rotation;
		this.rotation=rotation;
		setModified();
		firePropertyChange(ROTATION, oldRotation, rotation);
	}
}
