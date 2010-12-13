/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media.fanfic;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.filestore.FileStore;

public class FanFicPart extends IDObject implements ChainLink
{
	public static final String TYPE_HTML="html";
	public static final String TYPE_IMAGE="image";
	
	public static final String FANFIC="fanFic";
	public static final String NAME="name";
	public static final String SEQUENCE="sequence";
	public static final String TYPE="type";
	public static final String EXTENSION="extension";
	public static final String ENCODING="encoding";
	public static final String SIZE="size";

	private String name;
	private String type;
	private String extension;
	private int sequence;
	private String encoding;

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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, this.name);
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		String oldType=this.type;
		this.type=type;
		setModified(TYPE, oldType, this.type);
	}

	public String getExtension()
	{
		return extension;
	}

	protected void setExtension(String extension)
	{
		String oldExtension=this.extension;
		this.extension=extension;
		setModified(EXTENSION, oldExtension, this.extension);
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		String oldEncoding=this.encoding;
		this.encoding=encoding;
		setModified(ENCODING, oldEncoding, this.encoding);
	}

	public InputStream getContent() throws Exception
	{
		File file=FileStore.getInstance().getFile(this, "content."+extension);
		if (file!=null) return new FileInputStream(file);
		else return null;
	}

	public File getContentFile()
	{
		if (extension!=null) return FileStore.getInstance().getFile(this, "content."+extension);
		return null;
	}

	public void putContent(InputStream content, String extension, String encoding) throws Exception
	{
		if (extension==null) throw new IllegalArgumentException("extension must not be null");
		Long oldSize=getSize();
		setExtension(extension);
		setEncoding(encoding);
		FileStore.getInstance().putFile(this, "content."+extension, content);
		firePropertyChange(SIZE, oldSize, getSize());
	}

	public Long getSize()
	{
		try
		{
			File file=getContentFile();
			return file!=null && file.exists() ? file.length() : null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
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

	@Override
	public void setChainPosition(int position)
	{
		setSequence(position);
	}

	@Override
	public int getChainPosition()
	{
		return getSequence();
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public void delete()
	{
		super.delete();
		FileStore.getInstance().removeAllFiles(this);
	}

	public static class Comparator implements java.util.Comparator
	{
		@Override
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
