package com.kiwisoft.media.show;

import com.kiwisoft.media.common.MediaFilesAction;
import com.kiwisoft.media.files.MediaType;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileManager;
import com.kiwisoft.collection.SetMap;

import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 07.10.2009
 */
public class EpisodeMediaFilesAction extends EpisodeAction implements MediaFilesAction
{
	private Long typeId;

	private MediaType type;
	private SetMap<String, MediaFile> mediaFileGroups;

	@Override
	public String execute() throws Exception
	{
		super.execute();
		if (typeId!=null) type=MediaType.valueOf(typeId);
		if (type!=null && getEpisode()!=null)
		{
			Set<MediaFile> files=MediaFileManager.getInstance().getMediaFiles(getEpisode(), type);
			mediaFileGroups=MediaFileManager.getInstance().groupMediaFiles(files);
		}
		return SUCCESS;
	}

	@Override
	public String getGalleryName()
	{
		return "Episode "+getEpisode().toString()+" - "+type.getPluralName();
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