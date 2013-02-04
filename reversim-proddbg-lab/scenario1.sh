#!/bin/bash

jar='target/reversim-proddbg-lab-1.0-SNAPSHOT.jar'
[ ! -f "$jar" ] && { mvn package || exit 1; }

cat symbols.txt | java -cp "$jar" com.tomergabel.examples.Scenario1

