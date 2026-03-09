#!/usr/bin/env bash

PROFILE=${PROFILE:-prod}

echo "Starting service with profile: $PROFILE"
exec java -jar /srv/iam_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=$PROFILE