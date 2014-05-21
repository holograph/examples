package com.tomergabel.examples.java;

/**
 * Created by tomer on 5/20/14.
 */
public class IckyPartiallyKnownPerson {
    private String name;
    private String surname;
    private Integer age;

    public IckyPartiallyKnownPerson( String name, String surname, Integer age ) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Integer getAge() {
        return age;
    }
}
