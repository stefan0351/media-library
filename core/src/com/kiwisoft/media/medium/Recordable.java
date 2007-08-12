package com.kiwisoft.media.medium;

import com.kiwisoft.media.Language;

public interface Recordable
{
	int getRecordableLength();

	String getRecordableName(Language language);

	void initRecord(Track track);
}
