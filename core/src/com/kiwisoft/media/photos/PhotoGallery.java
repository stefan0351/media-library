package com.kiwisoft.media.photos;

import java.util.HashSet;

import com.kiwisoft.media.pics.ImageData;
import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.Chain;
import com.kiwisoft.utils.gui.ImageDescriptor;

public class PhotoGallery extends IDObject
{
	public static final String NAME="name";
	public static final String PHOTOS="photos";

	private String name;
	private Chain<Photo> photos;

	public PhotoGallery()
	{
	}

	public PhotoGallery(DBDummy dummy)
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

	public Chain<Photo> getPhotos()
	{
		if (photos==null) photos=new Chain<Photo>(DBLoader.getInstance().loadSet(Photo.class, null, "photogallery_id=?", getId()));
		return photos;
	}

	public Photo createPhoto(ImageDescriptor imageData, ImageData thumbnailData)
	{
		PictureFile picture=new PictureFile(imageData);
		PictureFile thumbnail=new PictureFile(thumbnailData);

		Photo photo=new Photo(this);
		photo.setOriginalPicture(picture);
		photo.setThumbnail(thumbnail);
		photo.setCreationDate(imageData.getDate());
		photo.setCameraMake(imageData.getCameraMake());
		photo.setCameraModel(imageData.getCameraModel());
		photo.setExposureTime(imageData.getExposureTime());
		photo.setColorDepth(imageData.getColorDepth());

		getPhotos().addNew(photo);
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


	@Override
	public void delete()
	{
		for (Photo photo : new HashSet<Photo>(getPhotos().elements())) photo.delete();
		super.delete();
	}
}
