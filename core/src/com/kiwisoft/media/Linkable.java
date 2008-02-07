package com.kiwisoft.media;

public interface Linkable
{
	String LINK_GROUP="linkGroup";

	LinkGroup getLinkGroup(boolean create);
}
