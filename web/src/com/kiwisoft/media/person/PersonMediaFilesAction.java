package com.kiwisoft.media.person;

import com.kiwisoft.collection.SetMap;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileManager;
import com.kiwisoft.media.files.MediaType;
import com.kiwisoft.media.common.MediaFilesAction;

import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public class PersonMediaFilesAction extends PersonDetailsAction implements MediaFilesAction
{
	private Long typeId;

	private MediaType type;
	private SetMap<String, MediaFile> mediaFileGroups;

	@Override
	public String execute() throws Exception
	{
		String result=super.execute();
		if (typeId!=null) type=MediaType.valueOf(typeId);
		if (type!=null && getPerson()!=null)
		{
			Set<MediaFile> files=MediaFileManager.getInstance().getMediaFiles(getPerson(), type);
			mediaFileGroups=MediaFileManager.getInstance().groupMediaFiles(files);
		}
		return result;
	}

	@Override
	public String getGalleryName()
	{
		return type.getPluralName();
	}

	@Override
	public MediaType getType()
	{
		return type;
	}

	@Override
	public Set<String> getMediaFileGroups()
	{
		return mediaFileGroups!=null ? mediaFileGroups.keySet() : null;
	}

	@Override
	public Set<MediaFile> getMediaFiles(String group)
	{
		return mediaFileGroups.get(group);
	}

	public Long getTypeId()
	{
		return typeId;
	}

	public void setTypeId(Long typeId)
	{
		this.typeId=typeId;
	}
}


