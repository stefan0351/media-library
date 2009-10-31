package com.kiwisoft.media.person;

import com.kiwisoft.media.show.Production;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBObject;
import com.kiwisoft.utils.NaturalComparator;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;

import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 20.10.2009
 */
public class SubProductionCreditsAction extends PersonAction
{
	private Long productionId;
	private String productionClass;
	private Long typeId;

	private Production production;
	private CreditType type;
	private Credits credits;


	@Override
	public String execute() throws Exception
	{
		super.execute();
		if (productionId!=null && !StringUtils.isEmpty(productionClass))
		{
			Class<? extends DBObject> clazz=Utils.cast(Class.forName(productionClass));
			production=(Production) DBLoader.getInstance().load(clazz, productionId);
		}
		if (typeId!=null) type=CreditType.valueOf(typeId);
		Person person=getPerson();
		if (person!=null && production instanceof Show && type!=null)
		{
			if (type.isActingCredit())
			{
				Set<CastMember> castMembers=DBLoader.getInstance().loadSet(CastMember.class, "_join episodes e on e.id=cast.episode_id",
																		   "actor_id=? and e.show_id=?",
																		   person.getId(), production.getId());
				Credits<CastMember> credits=new Credits<CastMember>(new NaturalComparator());
				for (CastMember castMember : castMembers)
				{
					credits.addProduction(castMember.getEpisode());
					credits.addCredit(castMember.getEpisode(), castMember);
				}
				this.credits=credits;
			}
			else
			{
				Set<Credit> crewMembers=DBLoader.getInstance().loadSet(Credit.class, "_join episodes e on e.id=credit.episode_id",
											   "person_id=? and e.show_id=? and credit_type_id=?",
											   person.getId(), production.getId(), type.getId());
				Credits<Credit> credits=new Credits<Credit>(new NaturalComparator());
				for (Credit crewMember : crewMembers)
				{
					credits.addProduction(crewMember.getEpisode());
					credits.addCredit(crewMember.getEpisode(), crewMember);
				}
				this.credits=credits;
			}

		}
		return SUCCESS;
	}

	public void setProductionId(Long productionId)
	{
		this.productionId=productionId;
	}

	public void setProductionClass(String productionClass)
	{
		this.productionClass=productionClass;
	}

	public void setTypeId(Long typeId)
	{
		this.typeId=typeId;
	}

	public Credits getCredits()
	{
		return credits;
	}

	public Production getProduction()
	{
		return production;
	}

	public CreditType getType()
	{
		return type;
	}
}
