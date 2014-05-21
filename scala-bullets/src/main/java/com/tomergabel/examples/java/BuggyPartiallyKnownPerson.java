package com.tomergabel.examples.java;

/**
 * Created by tomer on 5/20/14.
 */
public class BuggyPartiallyKnownPerson {
    private String name;
    private String surname;
    private Integer age;

    /**
     * Creates a new {@link com.tomergabel.examples.java.BuggyPartiallyKnownPerson}.
     * @param name The person's name (or {@literal null} if unknown).
     * @param surname The person's surname (or {@literal null} if unknown).
     * @param age The person's age (or {@literal null} if unknown).
     */
    public BuggyPartiallyKnownPerson( String name, String surname, Integer age ) {
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
     * @return {@literal true} if this person is an adult, {@literal false} otherwise.
     */
    public boolean isAdult() {
        return age >= 18;
    }
}
