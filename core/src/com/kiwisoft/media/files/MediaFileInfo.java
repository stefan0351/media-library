package com.kiwisoft.media.files;

import com.kiwisoft.utils.SimpleTimeFormat;
import com.kiwisoft.utils.Time;
import com.kiwisoft.utils.TimeFormat;

/**
 * @author Stefan Stiller
 */
public class MediaFileInfo
{
	private Integer width;
	private Integer height;
	private String format;
	private Long duration;
	private String videoFormat;
	private String audioFormat;
	private int videoStreamCount;
	private int audioStreamCount;
	private int imageStreamCount;

	public Integer getWidth()
	{
		return width;
	}

	public void setWidth(Integer width)
	{
		this.width=width;
	}

	public Integer getHeight()
	{
		return height;
	}

	public void setHeight(Integer height)
	{
		this.height=height;
	}

	public void setFormat(String format)
	{
		this.format=format;
	}

	public String getFormat()
	{
		return format;
	}

	public void setDuration(Long duration)
	{
		this.duration=duration;
	}

	public Long getDuration()
	{
		return duration;
	}

	public void setVideoFormat(String videoFormat)
	{
		this.videoFormat=videoFormat;
	}

	public void setAudioFormat(String audioFormat)
	{
		this.audioFormat=audioFormat;
	}

	public String getVideoFormat()
	{
		return videoFormat;
	}

	public String getAudioFormat()
	{
		return audioFormat;
	}

	public int getVideoStreamCount()
	{
		return videoStreamCount;
	}

	public void setVideoStreamCount(int videoStreamCount)
	{
		this.videoStreamCount=videoStreamCount;
	}

	public int getAudioStreamCount()
	{
		return audioStreamCount;
	}

	public void setAudioStreamCount(int audioStreamCount)
	{
		this.audioStreamCount=audioStreamCount;
	}

	public int getImageStreamCount()
	{
		return imageStreamCount;
	}

	public void setImageStreamCount(int imageStreamCount)
	{
		this.imageStreamCount=imageStreamCount;
	}

	@Override
	public String toString()
	{
		return "MediaFileInfo{"+
			   "audioFormat='"+audioFormat+'\''+
			   ", width="+width+
			   ", height="+height+
			   ", format='"+format+'\''+
			   ", duration="+(duration!=null ? new SimpleTimeFormat("H:mm:ss").format(new Time(duration)) : null)+
			   ", videoFormat='"+videoFormat+'\''+
			   ", videoStreamCount="+videoStreamCount+
			   ", audioStreamCount="+audioStreamCount+
			   ", imageStreamCount="+imageStreamCount+
			   '}';
	}

	public boolean isImage()
	{
		return imageStreamCount>0 && videoStreamCount==0 && audioStreamCount==0;
	}

	public boolean isAudio()
	{
		return videoStreamCount==0 && audioStreamCount>0;
	}

	public boolean isVideo()
	{
		return videoStreamCount>0;
	}
}
