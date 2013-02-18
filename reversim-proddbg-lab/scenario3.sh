#!/bin/bash

jar='target/reversim-proddbg-lab-1.0-SNAPSHOT.jar'
[ ! -f "$jar" ] && { mvn package || exit 1; }

java -Xmx64m $* -cp "$jar" com.tomergabel.examples.Scenario3
