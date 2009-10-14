package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.IndexByUtils;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.*;
import com.kiwisoft.persistence.DBErrors;
import com.kiwisoft.persistence.DBException;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.utils.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Stefan Stiller
 */
public abstract class CreateMovieTx implements Transactional
{
    private MovieData movieData;
    private Movie movie;

    protected CreateMovieTx(MovieData movieData)
    {
        this.movieData=movieData;
    }

    public void run() throws Exception
    {
        Language english=LanguageManager.getInstance().getLanguageBySymbol("en");
        Map<String, Person> persons=new HashMap<String, Person>();

		// todo
//        movie=movieData.getMovie();
//        if (movie==null) movie=new Movie();

        if (StringUtils.isEmpty(movie.getImdbKey())) movie.setImdbKey(movieData.getImdbKey());
        if (StringUtils.isEmpty(movie.getTitle())) movie.setTitle(movieData.getTitle());
        if (StringUtils.isEmpty(movie.getIndexBy())) movie.setIndexBy(IndexByUtils.createIndexBy(movie.getTitle()));
        if (StringUtils.isEmpty(movie.getGermanTitle())) movie.setGermanTitle(movieData.getGermanTitle());
        if (StringUtils.isEmpty(movie.getSummaryText(english))) movie.setSummaryText(english, movieData.getSummary());
        if (movie.getYear()==null) movie.setYear(movieData.getYear());
        if (movie.getRuntime()==null) movie.setRuntime(movieData.getRuntime());
		// todo
//        if (movie.getLanguages().isEmpty()) movie.setLanguages(movieData.getLanguages());
//        if (movie.getCountries().isEmpty()) movie.setCountries(movieData.getCountries());

        Set<CrewData> crew=new HashSet<CrewData>();
        for (Credit crewMember : movie.getCredits()) crew.add(new CrewData(crewMember));
        for (CrewData crewData : movieData.getCrew())
        {
            if (!crew.contains(crewData))
            {
                Person person=getPerson(persons, crewData.getImdbKey(), crewData.getName());
                Credit crewMember=new Credit();
                crewMember.setMovie(movie);
                crewMember.setPerson(person);
                crewMember.setCreditType(crewData.getType());
                crewMember.setSubType(crewData.getSubType());
                crew.add(crewData);
            }
        }

        Set<CastData> cast=new HashSet<CastData>();
        for (CastMember castMember : movie.getCastMembers()) cast.add(new CastData(castMember));
        for (CastData castData : movieData.getCast())
        {
            if (!cast.contains(castData))
            {
                Person person=getPerson(persons, castData.getImdbKey(), castData.getActor());
                CastMember castMember=new CastMember();
                castMember.setMovie(movie);
                castMember.setActor(person);
                castMember.setCreditType(CreditType.MAIN_CAST);
                castMember.setCharacterName(castData.getRole());
                castMember.setCreditOrder(castData.getCreditOrder());
                cast.add(castData);
            }
        }
    }

    private static Person getPerson(Map<String, Person> persons, String imdbKey, String name)
    {
        Person person=null;
        if (imdbKey!=null)
        {
            person=persons.get(imdbKey);
            if (person==null) person=PersonManager.getInstance().getPersonByIMDbKey(imdbKey);
        }
        if (person==null) person=persons.get(name);
        if (person==null)
        {
            try
            {
                person=DBLoader.getInstance().load(Person.class, null, "binary name=?"+(imdbKey!=null ? " and imdb_key is null" : ""), name);
                if (person==null)
                {
                    person=DBLoader.getInstance().load(Person.class, "names", "names.type=? and names.ref_id=persons.id"+
                            " and binary names.name=?"+(imdbKey!=null ? " and imdb_key is null" : ""), Name.PERSON, name);
                }
            }
            catch (DBException e)
            {
                if (e.getErrorCode()==DBErrors.MULTIPLE_OBJECTS_FOUND)
                {
                    throw new RuntimeException("Multiple persons with name '"+name+"' found. Set the IMDb key for the correct person to '"+imdbKey+"' and try again.");
                }
                throw e;
            }
        }
        if (person==null ||
                (!StringUtils.isEmpty(imdbKey) && !StringUtils.isEmpty(person.getImdbKey()) && !imdbKey.equals(person.getImdbKey())))
        {
            person=new Person();
            person.setName(name);
            persons.put(name, person);
        }
        if (StringUtils.isEmpty(person.getImdbKey())) person.setImdbKey(imdbKey);
        if (!StringUtils.isEmpty(imdbKey)) persons.put(imdbKey, person);
        return person;
    }

    public Movie getMovie()
    {
        return movie;
    }
}
