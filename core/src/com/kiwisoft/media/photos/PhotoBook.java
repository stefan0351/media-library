package com.kiwisoft.media.photos;

import java.util.Set;
import java.io.File;

import com.kiwisoft.utils.db.*;

public class PhotoBook extends IDObject 
{
	public static final String NAME="name";
	public static final String PHOTOS="photos";

	private String name;
	private Set<Photo> photos;

	public PhotoBook()
	{
	}

	public PhotoBook(DBDummy dummy)
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

	public Set<Photo> getPhotos()
	{
		if (photos==null) photos=DBLoader.getInstance().loadSet(Photo.class, null, "photobook_id=?", getId());
		return photos;
	}

	public Photo createPhoto(File file)
	{
		Photo photo=new Photo(this, file);
		getPhotos().add(photo);
		fireElementAdded(PHOTOS, photo);
		return photo;
	}

	public void dropPhoto(Photo photo)
	{
		getPhotos().remove(photo);
		photo.delete();
		fireElementRemoved(PHOTOS, photo);
	}

	@Override
	public void afterReload()
	{
		photos=null;
		super.afterReload();
	}
}
