#!/bin/bash
brunch=$(git rev-parse --abbrev-ref HEAD)
./gradlew :$1:publish \
  -Psigning.keyId=$SONATYPE_GPG_KEY_ID \
  -Psigning.password=$SONATYPE_GPG_PASSWORD \
  -Psigning.secretKeyRingFile=../keystore.gpg \
  -Dgit.branch=$brunch
if [ $brunch == 'master' ]; then ./gradlew :$1:closeAndReleaseRepository; fi
