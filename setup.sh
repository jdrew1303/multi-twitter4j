#!/bin/bash
mvn clean
mvn install
mvn assembly:single
cp target/multi-twitter4j-0.0.3-SNAPSHOT-jar-with-dependencies.jar multi-twitter4j-0.0.3-SNAPSHOT-jar-with-dependencies.jar
