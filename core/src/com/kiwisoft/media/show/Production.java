package com.kiwisoft.media.show;

import java.util.Set;

import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CrewMember;
import com.kiwisoft.media.person.CreditType;

/**
 * @author Stefan Stiller
 */
public interface Production
{
	Set<CastMember> getCastMembers(CreditType type);

	Set<CrewMember> getCrewMembers(CreditType type);
}
