package com.tomergabel.examples.java;

/**
 * Created by tomer on 5/20/14.
 */
enum MaritalStatus { single, married, divorced, widowed }
enum Gender { male, female }

public class NoPatternMatching {
}

class PartialPerson {
    private String name;
    private String surname;
    private int age;
    private MaritalStatus maritalStatus;
    private Gender gender;

    PartialPerson( String name, String surname, int age, MaritalStatus maritalStatus, Gender gender ) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public Gender getGender() {
        return gender;
    }

    public boolean isAdult() {
        return age >= 18;
    }

    // Based on rules from http://www.formsofaddress.info/Social_M_W.html
    public String getSalutation() {
        if ( gender == null ) return null;
        switch( gender ) {
            case male:
                return "Mr.";

            case female:
                if ( maritalStatus == null ) return "Ms.";
                switch( maritalStatus ) {
                    case single:
                        return "Miss";

                    case married:
                    case divorced:
                    case widowed:
                        return "Mrs.";
                }

            default:
                return null;
        }
    }
}