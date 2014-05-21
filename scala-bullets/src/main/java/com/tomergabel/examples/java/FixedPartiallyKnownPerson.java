package com.tomergabel.examples.java;

/**
 * Created by tomer on 5/20/14.
 */
public class FixedPartiallyKnownPerson {
    private String name;
    private String surname;
    private Integer age;

    /**
     * Creates a new {@link com.tomergabel.examples.java.FixedPartiallyKnownPerson}.
     * @param name The person's name (or {@literal null} if unknown).
     * @param surname The person's surname (or {@literal null} if unknown).
     * @param age The person's age (or {@literal null} if unknown).
     */
    public FixedPartiallyKnownPerson( String name, String surname, Integer age ) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    /**
     * Gets the person's name.
     * @return The person's name, or {@literal null} if unknown.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the person's surname.
     * @return The person's surname, or {@literal null} if unknown.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Gets the person's age.
     * @return The person's age, or {@literal null} if unknown.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Is this person an adult?
     * @return {@literal true} if this person is an adult, {@literal false} if not, or {@literal null} if unknown.
     */
    public Boolean isAdult() {
        if ( age != null )
            return age >= 18;
        else
            return null;
    }

    public static void main( String[] args ) {
        FixedPartiallyKnownPerson person =
                new FixedPartiallyKnownPerson( "Brandt", null, null );

        int timeLeft = 125 - person.getAge();       // <-- Boom, NPE here
        System.out.println(
                person.getName() + ", you have up to " +
                timeLeft + " years to live!" );
    }
}
