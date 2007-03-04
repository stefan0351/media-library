package com.kiwisoft.media.show;

import java.util.Set;

import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CrewMember;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 02.03.2007
 * Time: 13:18:25
 * To change this template use File | Settings | File Templates.
 */
public interface Production
{
	Set<CastMember> getCastMembers(int type);

	Set<CrewMember> getCrewMembers(String type);
}
