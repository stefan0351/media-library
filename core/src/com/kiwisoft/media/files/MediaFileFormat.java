package com.kiwisoft.media.files;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class MediaFileFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object value)
	{
		if (value instanceof MediaFile)
		{
			MediaFile mediaFile=(MediaFile)value;
			return mediaFile.getName();
		}
		return super.format(value);
	}


	@Override
	public String getIconName(Object value)
	{
		if (value instanceof MediaFile)
		{
			MediaFile mediaFile=(MediaFile)value;
			switch (mediaFile.getMediaType())
			{
				case MediaFile.IMAGE: return "file.image";
				case MediaFile.AUDIO: return "file.audio";
				case MediaFile.VIDEO: return "file.video";
			}
		}
		return super.getIconName(value);
	}
}
