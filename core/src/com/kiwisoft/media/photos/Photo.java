package com.kiwisoft.media.photos;

import java.io.File;
import java.util.Date;

import com.kiwisoft.media.files.ImageFileInfo;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.collection.ChainLink;
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
		return (Long)getReferenceId(ORIGINAL_PICTURE);
	}

	public ImageFile getOriginalPicture()
	{
		return (ImageFile)getReference(ORIGINAL_PICTURE);
	}

	public void setOriginalPicture(ImageFile imageFile)
	{
		setReference(ORIGINAL_PICTURE, imageFile);
	}

	public ImageFile getThumbnail()
	{
		return (ImageFile)getReference(THUMBNAIL);
	}

	public void setThumbnail(ImageFile imageFile)
	{
		setReference(THUMBNAIL, imageFile);
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Date creationDate)
	{
		Date oldCreationDate=this.creationDate;
		this.creationDate=creationDate;
		setModified("creationDate", oldCreationDate, this.creationDate);
	}

	public int getRotation()
	{
		return rotation;
	}

	public void setRotation(int rotation)
	{
		ImageFile picture=getOriginalPicture();
		if (picture==null) return;
		File file=picture.getPhysicalFile();
		if (!file.exists()) return;
		ImageFileInfo thumbnailData=PhotoManager.getInstance().createThumbnail(file, rotation);
		if (thumbnailData==null) return;

		ImageFile thumbnail=getThumbnail();
		setThumbnail(new ImageFile(MediaConfiguration.PATH_ROOT, thumbnailData));
		if (thumbnail!=null) thumbnail.delete();

		int oldRotation=this.rotation;
		this.rotation=rotation;
		setModified(ROTATION, oldRotation, rotation);
	}

	public int getWidth()
	{
		ImageFile picture=getOriginalPicture();
		if (picture!=null)
		{
			if (rotation==90 || rotation==270) return picture.getHeight();
			return picture.getWidth();
		}
		return -1;
	}

	public int getHeight()
	{
		ImageFile picture=getOriginalPicture();
		if (picture!=null)
		{
			if (rotation==90 || rotation==270) return picture.getWidth();
			return picture.getHeight();
		}
		return -1;
	}

	@Override
	public void setChainPosition(int position)
	{
		int oldPosition=this.sequence;
		this.sequence=position;
		setModified("chainPosition", oldPosition, this.sequence);
	}

	@Override
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
		String oldMake=this.cameraMake;
		this.cameraMake=cameraMake;
		setModified("cameraMake", oldMake, this.cameraMake);
	}

	public String getCameraModel()
	{
		return cameraModel;
	}

	public void setCameraModel(String cameraModel)
	{
		String oldModel=this.cameraModel;
		this.cameraModel=cameraModel;
		setModified("cameraModel", oldModel, this.cameraModel);
	}

	public Double getExposureTime()
	{
		return exposureTime;
	}

	public void setExposureTime(Double exposureTime)
	{
		Double oldExposureTime=this.exposureTime;
		this.exposureTime=exposureTime;
		setModified("exposureTime", oldExposureTime, this.exposureTime);
	}

	public Double getFNumber()
	{
		return fNumber;
	}

	public void setFNumber(Double fNumber)
	{
		Double oldFNumber=this.fNumber;
		this.fNumber=fNumber;
		setModified("fNumber", oldFNumber, this.fNumber);
	}

	public Double getFocalLength()
	{
		return focalLength;
	}

	public void setFocalLength(Double focalLength)
	{
		Double oldFocalLength=this.focalLength;
		this.focalLength=focalLength;
		setModified("focalLength", oldFocalLength, focalLength);
	}

	public Integer getIsoSpeed()
	{
		return isoSpeed;
	}

	public void setIsoSpeed(Integer isoSpeed)
	{
		Integer oldIsoSpeed=this.isoSpeed;
		this.isoSpeed=isoSpeed;
		setModified("isoSpeed", oldIsoSpeed, this.isoSpeed);
	}

	public Integer getXResolution()
	{
		return xResolution;
	}

	public void setXResolution(Integer xResolution)
	{
		Integer oldXResolution=this.xResolution;
		this.xResolution=xResolution;
		setModified("xResolution", oldXResolution, this.xResolution);
	}

	public Integer getYResolution()
	{
		return yResolution;
	}

	public void setYResolution(Integer yResolution)
	{
		Integer oldYResolution=this.yResolution;
		this.yResolution=yResolution;
		setModified("yResolution", oldYResolution, this.yResolution);
	}

	public int getColorDepth()
	{
		return colorDepth;
	}

	public void setColorDepth(int colorDepth)
	{
		int oldColorDepth=this.colorDepth;
		this.colorDepth=colorDepth;
		setModified("colorDepth", oldColorDepth, this.colorDepth);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		String oldDescription=this.description;
		this.description=description;
		setModified(DESCRIPTION, oldDescription, description);
	}

	@Override
	public void delete()
	{
		ImageFile picture=getOriginalPicture();
		if (picture!=null) picture.delete();
		ImageFile thumbnail=getThumbnail();
		if (thumbnail!=null) thumbnail.deletePhysically();
		super.delete();
	}
}
