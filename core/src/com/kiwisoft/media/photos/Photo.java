package com.kiwisoft.media.photos;

import java.io.File;
import java.util.Date;

import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.pics.ImageData;
import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;

public class Photo extends IDObject implements ChainLink
{
	public static final String ROTATION="rotation";
	public static final String DESCRIPTION="description";
	public static final String GALLERY="gallery";
	public static final String ORIGINAL_PICTURE="originalPicture";
	public static final String THUMBNAIL="thumbnail";
	public static final String GALLERY_PHOTO="galleryPhoto";

	public static final int THUMBNAIL_WIDTH=160;
	public static final int THUMBNAIL_HEIGHT=120;

	private int rotation;
	private int sequence;
	private int colorDepth;
	private String description;

	private Date creationDate;
	private String cameraMake;
	private String cameraModel;
	private Double exposureTime;
	private Double fNumber;
	private Double focalLength;
	private Integer isoSpeed;
	private Integer xResolution;
	private Integer yResolution;
	private boolean galleryPhoto;

	public Photo(PhotoGallery gallery)
	{
		setGallery(gallery);
	}

	public Photo(DBDummy dummy)
	{
		super(dummy);
	}

	public PhotoGallery getGallery()
	{
		return (PhotoGallery)getReference(GALLERY);
	}

	public void setGallery(PhotoGallery gallery)
	{
		setReference(GALLERY, gallery);
	}


	public boolean isGalleryPhoto()
	{
		return galleryPhoto;
	}

	/**
	 * Should not be called directly. Use {@link PhotoGallery#setGalleryPhoto(Photo)} instead.
	 */
	public void setGalleryPhoto(boolean galleryPhoto)
	{
		boolean oldGalleryPhoto=this.galleryPhoto;
		this.galleryPhoto=galleryPhoto;
		setModified(GALLERY_PHOTO, oldGalleryPhoto, galleryPhoto);
	}

	public Long getOriginalPictureId()
	{
		return getReferenceId(ORIGINAL_PICTURE);
	}

	public PictureFile getOriginalPicture()
	{
		return (PictureFile)getReference(ORIGINAL_PICTURE);
	}

	public void setOriginalPicture(PictureFile pictureFile)
	{
		setReference(ORIGINAL_PICTURE, pictureFile);
	}

	public PictureFile getThumbnail()
	{
		return (PictureFile)getReference(THUMBNAIL);
	}

	public void setThumbnail(PictureFile pictureFile)
	{
		setReference(THUMBNAIL, pictureFile);
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
		PictureFile picture=getOriginalPicture();
		if (picture==null) return;
		File file=FileUtils.getFile(MediaConfiguration.getRootPath(), picture.getFile());
		if (!file.exists()) return;
		ImageData thumbnailData=PhotoManager.getInstance().createThumbnail(file, rotation);
		if (thumbnailData==null) return;

		PictureFile thumbnail=getThumbnail();
		setThumbnail(new PictureFile(thumbnailData));
		if (thumbnail!=null) thumbnail.deletePhysically();

		int oldRotation=this.rotation;
		this.rotation=rotation;
		setModified();
		firePropertyChange(ROTATION, oldRotation, rotation);
	}

	public int getWidth()
	{
		PictureFile picture=getOriginalPicture();
		if (picture!=null)
		{
			if (rotation==90 || rotation==270) return picture.getHeight();
			return picture.getWidth();
		}
		return -1;
	}

	public int getHeight()
	{
		PictureFile picture=getOriginalPicture();
		if (picture!=null)
		{
			if (rotation==90 || rotation==270) return picture.getWidth();
			return picture.getHeight();
		}
		return -1;
	}

	public void setChainPosition(int position)
	{
		this.sequence=position;
		setModified();
	}

	public int getChainPosition()
	{
		return sequence;
	}


	public String getCameraMake()
	{
		return cameraMake;
	}

	public void setCameraMake(String cameraMake)
	{
		this.cameraMake=cameraMake;
		setModified();
	}

	public String getCameraModel()
	{
		return cameraModel;
	}

	public void setCameraModel(String cameraModel)
	{
		this.cameraModel=cameraModel;
		setModified();
	}

	public Double getExposureTime()
	{
		return exposureTime;
	}

	public void setExposureTime(Double exposureTime)
	{
		this.exposureTime=exposureTime;
		setModified();
	}

	public Double getFNumber()
	{
		return fNumber;
	}

	public void setFNumber(Double fNumber)
	{
		this.fNumber=fNumber;
		setModified();
	}

	public Double getFocalLength()
	{
		return focalLength;
	}

	public void setFocalLength(Double focalLength)
	{
		this.focalLength=focalLength;
		setModified();
	}

	public Integer getIsoSpeed()
	{
		return isoSpeed;
	}

	public void setIsoSpeed(Integer isoSpeed)
	{
		this.isoSpeed=isoSpeed;
		setModified();
	}

	public Integer getXResolution()
	{
		return xResolution;
	}

	public void setXResolution(Integer xResolution)
	{
		this.xResolution=xResolution;
		setModified();
	}

	public Integer getYResolution()
	{
		return yResolution;
	}

	public void setYResolution(Integer yResolution)
	{
		this.yResolution=yResolution;
		setModified();
	}

	public int getColorDepth()
	{
		return colorDepth;
	}

	public void setColorDepth(int colorDepth)
	{
		this.colorDepth=colorDepth;
		setModified();
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		String oldDescription=this.description;
		this.description=description;
		setModified();
		firePropertyChange(DESCRIPTION, oldDescription, description);
	}

	@Override
	public void delete()
	{
		PictureFile picture=getOriginalPicture();
		if (picture!=null) picture.delete();
		PictureFile thumbnail=getThumbnail();
		if (thumbnail!=null) thumbnail.deletePhysically();
		super.delete();
	}
}
