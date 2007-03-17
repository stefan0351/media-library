package com.kiwisoft.media.video;

import com.kiwisoft.media.Language;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 11.03.2007
 * Time: 11:16:18
 * To change this template use File | Settings | File Templates.
 */
public interface Recordable
{
	int getRecordableLength();

	String getRecordableName(Language language);

	void initRecord(Recording recording);
}
