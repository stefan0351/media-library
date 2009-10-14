package com.kiwisoft.media.person;

/**
 * @author Stefan Stiller
* @since 04.10.2009
*/
public class CastCreditComparator implements java.util.Comparator<CastMember>
{
	@Override
	public int compare(CastMember castMember1, CastMember castMember2)
	{
		Integer creditOrder2=castMember2.getCreditOrder();
		Integer creditOrder1=castMember1.getCreditOrder();
		if (creditOrder1!=null && creditOrder2!=null) return creditOrder1.compareTo(creditOrder2);
		else if (creditOrder1!=null) return -1;
		else if (creditOrder2!=null) return 1;
		return castMember1.getActor().getName().compareToIgnoreCase(castMember2.getActor().getName());
	}
}
