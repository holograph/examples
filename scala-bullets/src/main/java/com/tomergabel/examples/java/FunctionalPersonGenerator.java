package com.tomergabel.examples.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by tomer on 5/20/14.
 */
public class FunctionalPersonGenerator {
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

    // Age constants per http://en.wikipedia.org/wiki/Maximum_life_span and http://en.wikipedia.org/wiki/Toddler
    public static int MINIMUM_HUMAN_AGE = 0;
    public static int MAXIMUM_HUMAN_AGE = 125;
    public static int MINIMUM_TODDLER_AGE = 1;
    public static int MAXIMUM_TODDLER_AGE = 3;

    public static Person generatePerson( int minAge, int maxAge ) {
        String name = names.get( random.nextInt( names.size() ) );
        String surname = surnames.get( random.nextInt( surnames.size() ) );
        int age = random.nextInt( maxAge - minAge + 1 ) + minAge;
        return new Person( name, surname, age );
    }

    public static Person generatePerson() {
        return generatePerson( MINIMUM_HUMAN_AGE, MAXIMUM_HUMAN_AGE );
    }

    public static List<Person> generatePeople( int count, int minAge, int maxAge ) {
        List<Person> people = new ArrayList<>( count );
        for ( int i = 0; i < count; i++ )
            people.add( generatePerson( minAge, maxAge ) );
        return Collections.unmodifiableList( people );
    }

    public static List<Person> generatePeople( int count ) {
        return generatePeople( count, MINIMUM_HUMAN_AGE, MAXIMUM_HUMAN_AGE );
    }

    public static Person generateToddler() {
        return generatePerson( MINIMUM_TODDLER_AGE, MAXIMUM_TODDLER_AGE );
    }

    public static List<Person> generateToddlers( int count ) {
        return generatePeople( count, MINIMUM_TODDLER_AGE, MAXIMUM_TODDLER_AGE );
    }

    public static void main( String[] args ) {
        System.out.println( "Five people:" );
        for ( Person person : generatePeople( 5 ) )
            System.out.println( person.toString() );
        System.out.println( "Two toddlers:" );
        for ( Person toddler : generateToddlers( 2 ) )
            System.out.println( toddler.toString() );
    }
}
