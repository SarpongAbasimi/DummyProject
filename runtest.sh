#!/bin/bash

echo "About to run docker compose"

docker-compose -f docker-compose-test.yml up -d

sleep 5

echo "About to run test suites"

sbt test

docker-compose -f docker-compose-test.yml down

echo "Done . . . "