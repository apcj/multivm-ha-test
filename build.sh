#!/bin/bash
set -e

# wget http://dist.neo4j.org/neo4j-enterprise-1.6.M03-unix.tar.gz
# tar xf neo4j-enterprise-1.6.M03-unix.tar.gz
# 
# mv neo4j-enterprise-1.6.M03 neo4j-enterprise-1.6.M03-1
# cp -R neo4j-enterprise-1.6.M03-1 neo4j-enterprise-1.6.M03-2
# cp -R neo4j-enterprise-1.6.M03-1 neo4j-enterprise-1.6.M03-3

for i in 1 2 3;
do
  cp conf/$i/coord.cfg neo4j-enterprise-1.6.M03-$i/conf/
done

for i in 1 2 3;
do
  neo4j-enterprise-1.6.M03-$i/bin/neo4j-coordinator start
done

mkdir -p target/classes

javac -d target/classes -cp `echo \`find neo4j-enterprise-1.6.M03-1/lib/ -name *.jar\` | tr ' ' ':'` src/main/java/org/neo4j/testing/UniqueFactoryPlay.java
