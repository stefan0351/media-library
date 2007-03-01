package com.kiwisoft.media;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 12:32:09
 * To change this template use File | Settings | File Templates.
 */
public interface Linkable
{
	Link createLink();

	void dropLink(Link link);

	Set<Link> getLinks();

	int getLinkCount();
}
