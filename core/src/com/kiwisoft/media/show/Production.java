package com.kiwisoft.media.show;

import java.util.Set;

import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.collection.CollectionChangeSource;

/**
 * @author Stefan Stiller
 */
public interface Production extends CollectionChangeSource
{
	String CAST_MEMBERS="castMembers";
	String CREDITS="credits";

	Long getId();

	String getProductionTitle();

	CreditType[] getSupportedCastTypes();

	Set<CastMember> getCastMembers();

	Set<CastMember> getCastMembers(CreditType type);

	CastMember createCastMember(CreditType type);

	void dropCastMember(CastMember cast);

	Set<Credit> getCredits();

	Set<Credit> getCredits(CreditType type);

	Credit createCredit();

	void dropCredit(Credit credit);
}
