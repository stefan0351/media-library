package com.kiwisoft.media.person;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.files.*;
import com.kiwisoft.web.RecentItemManager;
import com.kiwisoft.web.RecentIdObject;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public class PersonAction extends BaseAction
{
	private static final long serialVersionUID=6615382046797116533L;

	private Long personId;

	private Person person;
	private MediaFile picture;
	private ImageFile thumbnail;

	@Override
	public String getPageTitle()
	{
		return person.getName();
	}

	@Override
	public String execute() throws Exception
	{
		if (personId!=null) person=PersonManager.getInstance().getPerson(personId);
		if (person!=null)
		{
			RecentItemManager.getInstance().addItem(new RecentIdObject<Person>(Person.class, person));
			picture=person.getPicture();
			if (picture!=null)
			{
				thumbnail=picture.getThumbnailSidebar();
				if (thumbnail==null && picture.getWidth()<=MediaFileUtils.THUMBNAIL_SIDEBAR_WIDTH) thumbnail=picture;
			}
		}
		return super.execute();
	}

	public Long getPersonId()
	{
		return personId;
	}

	public void setPersonId(Long personId)
	{
		this.personId=personId;
	}

	public Person getPerson()
	{
		return person;
	}

	public MediaFile getPicture()
	{
		return picture;
	}

	public ImageFile getThumbnail()
	{
		return thumbnail;
	}

	public boolean hasImages()
	{
		return MediaFileManager.getInstance().getNumberOfMediaFiles(person, MediaType.IMAGE)>0;
	}

	public boolean hasVideos()
	{
		return MediaFileManager.getInstance().getNumberOfMediaFiles(person, MediaType.VIDEO)>0;
	}
}