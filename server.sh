#!/bin/bash
mvn clean
mvn install
mvn assembly:single
cp target/multi-twitter4j-0.0.3-SNAPSHOT-jar-with-dependencies.jar multi-twitter4j-0.0.3-SNAPSHOT-jar-with-dependencies.jar
java -cp /home/igor/git/multi-twitter4j/multi-twitter4j-0.0.3-SNAPSHOT-jar-with-dependencies.jar org.insight.twitter.rpc.RPCServer
