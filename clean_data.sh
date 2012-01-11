#!/bin/bash
set -e

for i in 1 2 3;
do
  rm -rf neo4j-enterprise-1.6.M03-$i/data/graph.db/
done
