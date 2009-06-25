package com.kiwisoft.media.files;

import java.io.File;
import java.util.Date;

/**
 * @author Stefan Stiller
 */
public class PhotoFileInfo
{
	private File file;
	private int width;
	private int height;
	private String type;
	private int colorDepth;
	private String cameraMake;
	private String cameraModel;
	private Date date;
	private Double exposureTime;
	private Double fNumber;
	private Double focalLength;
	private Integer isoSpeed;
	private Integer xResolution;
	private Integer yResolution;

	public PhotoFileInfo(File file)
	{
		setFile(file);
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file=file;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public String getType()
	{
		return type;
	}

	public void setWidth(int width)
	{
		this.width=width;
	}

	public void setHeight(int height)
	{
		this.height=height;
	}

	public void setType(String type)
	{
		this.type=type;
	}

	public void setColorDepth(int colorDepth)
	{
		this.colorDepth=colorDepth;
	}

	public int getColorDepth()
	{
		return colorDepth;
	}

	public void setCameraMake(String cameraMake)
	{
		this.cameraMake=cameraMake;
	}

	public String getCameraMake()
	{
		return cameraMake;
	}

	public void setCameraModel(String cameraModel)
	{
		this.cameraModel=cameraModel;
	}

	public String getCameraModel()
	{
		return cameraModel;
	}

	public void setDate(Date date)
	{
		this.date=date;
	}

	public Date getDate()
	{
		return date;
	}

	public void setExposureTime(Double exposureTime)
	{
		this.exposureTime=exposureTime;
	}

	public Double getExposureTime()
	{
		return exposureTime;
	}

	public void setFNumber(Double fNumber)
	{
		this.fNumber=fNumber;
	}

	public Double getFNumber()
	{
		return fNumber;
	}

	public void setFocalLength(Double focalLength)
	{
		this.focalLength=focalLength;
	}

	public Double getFocalLength()
	{
		return focalLength;
	}

	public void setIsoSpeed(Integer isoSpeed)
	{
		this.isoSpeed=isoSpeed;
	}

	public Integer getIsoSpeed()
	{
		return isoSpeed;
	}

	public void setXResolution(Integer XResolution)
	{
		this.xResolution=XResolution;
	}

	public Integer getXResolution()
	{
		return xResolution;
	}

	public Integer getYResolution()
	{
		return yResolution;
	}

	public void setYResolution(Integer yResolution)
	{
		this.yResolution=yResolution;
	}

	@Override
	public String toString()
	{
		return "ImageDescriptor{"+
			   "file="+file+
			   ", size="+width+"x"+height+
			   ", resolution="+xResolution+"x"+yResolution+"dpi"+
			   ", type='"+type+'\''+
			   ", colorDepth="+colorDepth+
			   ", cameraMake='"+cameraMake+'\''+
			   ", cameraModel='"+cameraModel+'\''+
			   ", date="+date+
			   ", exposureTime="+exposureTime+"s"+
			   ", fNumber=F/"+fNumber+
			   ", focalLength="+focalLength+"mm"+
			   ", isoSpeed=ISO-"+isoSpeed+
			   '}';
	}
}
