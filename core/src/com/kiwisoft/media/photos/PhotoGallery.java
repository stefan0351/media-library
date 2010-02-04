package com.kiwisoft.media.photos;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.kiwisoft.media.files.ImageFileInfo;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.PhotoFileInfo;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.collection.Chain;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBLoader;

public class PhotoGallery extends IDObject
{
	public static final String NAME="name";
	public static final String PHOTOS="photos";
	public static final String CREATION_DATE="creationDate";
	public static final String PARENTS="parents";
	public static final String CHILDREN="children";

	private String name;
	private Chain<Photo> photos;
	private Date creationDate;

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
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, name);
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Date creationDate)
	{
		if (creationDate!=null) creationDate=DateUtils.getStartOfDay(creationDate);
		Date oldCreationDate=this.creationDate;
		this.creationDate=creationDate;
		setModified(CREATION_DATE, oldCreationDate, creationDate);
	}

	public Chain<Photo> getPhotos()
	{
		if (photos==null) photos=new Chain<Photo>(DBLoader.getInstance().loadSet(Photo.class, null, "photogallery_id=?", getId()));
		return photos;
	}

	public Photo createPhoto(PhotoFileInfo imageData, ImageFileInfo thumbnailData)
	{
		ImageFile photoFile=new ImageFile(MediaConfiguration.PATH_PHOTOS, imageData);
		ImageFile thumbnailFile=new ImageFile(MediaConfiguration.PATH_ROOT, thumbnailData);

		Photo photo=new Photo(this);
		photo.setOriginalPicture(photoFile);
		photo.setThumbnail(thumbnailFile);
		photo.setXResolution(imageData.getXResolution());
		photo.setYResolution(imageData.getYResolution());
		photo.setColorDepth(imageData.getColorDepth());
		photo.setCreationDate(imageData.getDate());
		photo.setCameraMake(imageData.getCameraMake());
		photo.setCameraModel(imageData.getCameraModel());
		photo.setExposureTime(imageData.getExposureTime());
		photo.setFocalLength(imageData.getFocalLength());
		photo.setFNumber(imageData.getFNumber());
		photo.setIsoSpeed(imageData.getIsoSpeed());

		addPhoto(photo);
		return photo;
	}

	public void dropPhoto(Photo photo)
	{
		removePhoto(photo);
		photo.delete();
	}

	public void addPhoto(Photo photo)
	{
		photo.setGallery(this);
		getPhotos().addNew(photo);
		fireElementAdded(PHOTOS, photo);
		if (getCreationDate()==null) setCreationDate(photo.getCreationDate());
	}

	public void removePhoto(Photo photo)
	{
		getPhotos().remove(photo);
		photo.setGallery(null);
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

	private Photo[] galleryPhoto;

	public Photo getGalleryPhoto()
	{
		if (galleryPhoto==null)
		{
			galleryPhoto=new Photo[1];
			galleryPhoto[0]=DBLoader.getInstance().load(Photo.class, null, "photogallery_id=? and gallery_photo=1", getId());
		}
		return galleryPhoto[0];
	}

	public void setGalleryPhoto(Photo photo)
	{
		Photo oldGalleryPhoto=getGalleryPhoto();
		if (oldGalleryPhoto!=null) oldGalleryPhoto.setGalleryPhoto(false);
		galleryPhoto[0]=photo;
		if (photo!=null) photo.setGalleryPhoto(true);
	}

	public ImageFile getThumbnail()
	{
		Photo photo=getGalleryPhoto();
		if (photo==null) photo=DBLoader.getInstance().load(Photo.class, null, "photogallery_id=?"+
																			  " and sequence=(select min(sequence) from photos where photogallery_id=?)",
														   getId(), getId());
		return photo!=null ? photo.getThumbnail() : null;
	}

	public Set<PhotoGallery> getParents()
	{
		return getAssociations(PARENTS);
	}

	public void setParents(Set<PhotoGallery> galleries)
	{
		setAssociations(PARENTS, galleries);
	}

	public Set<PhotoGallery> getChildren()
	{
		return getAssociations(CHILDREN);
	}

	public void addChildGallery(PhotoGallery gallery)
	{
		if (!containsAssociation(CHILDREN, gallery)) createAssociation(CHILDREN, gallery);
	}

	public void removeChildGallery(PhotoGallery gallery)
	{
		dropAssociation(CHILDREN, gallery);
	}

	public PhotoGallery createChildGallery()
	{
		PhotoGallery gallery=new PhotoGallery();
		addChildGallery(gallery);
		return gallery;
	}
}
