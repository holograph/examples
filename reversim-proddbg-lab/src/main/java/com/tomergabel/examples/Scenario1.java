package com.tomergabel.examples;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by tomer on 2/4/13.
 */
public class Scenario1 {

    static Pattern testPattern = Pattern.compile( "^(a+)+$" );

    public static void main( String[] args ) {
        Scanner s = new Scanner( System.in );
        while ( s.hasNextLine() ) {
            String line = s.nextLine();
            System.out.println( String.format(
                    "Line \"%s\" is%s good",
                    line, testPattern.matcher( line ).matches() ? "" : " not" ) );
        }
    }
}
