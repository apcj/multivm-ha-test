#!/bin/bash
set -e

classpath=target/classes:`echo \`find neo4j-enterprise-1.6.M03-1/lib/ -name *.jar\` | tr ' ' ':'`:neo4j-enterprise-1.6.M03-1/system/coordinator/lib/log4j-1.2.16.jar

java -cp $classpath org.neo4j.testing.UniqueFactoryPlay 1 &
java -cp $classpath org.neo4j.testing.UniqueFactoryPlay 2 &
java -cp $classpath org.neo4j.testing.UniqueFactoryPlay 3 &

