package com.tomergabel.examples.java;

import java.util.*;

/**
 * Created by tomer on 5/20/14.
 */
public class SimplePersonGenerator {
    private static List<String> names;
    private static List<String> surnames;
    private static Random random = new Random();

    static {
        names = new ArrayList<>();
        names.add( "Jeffrey" );
        names.add( "Walter" );
        names.add( "Donald" );

        surnames = new ArrayList<>();
        surnames.add( "Lebowsky" );
        surnames.add( "Sobchak" );
        surnames.add( "Kerabatsos" );
    }

    public static Person generatePerson( int age ) {
        String name = names.get( random.nextInt( names.size() ) );
        String surname = surnames.get( random.nextInt( surnames.size() ) );
        return new Person( name, surname, age );
    }

    public List<Person> generatePeople( int count, int age ) {
        List<Person> people = new ArrayList<>( count );
        for ( int i = 0; i < count; i++ )
            people.add( generatePerson( age ) );
        return Collections.unmodifiableList( people );
    }
}
