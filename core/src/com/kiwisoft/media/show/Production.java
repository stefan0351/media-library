package com.kiwisoft.media.show;

import java.util.Set;

import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.person.CreditType;

/**
 * @author Stefan Stiller
 */
public interface Production
{
	Set<CastMember> getCastMembers(CreditType type);

	Set<Credit> getCredits(CreditType type);
}
