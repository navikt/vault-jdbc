#!/bin/bash -e
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_45bbcaff3fce_key -iv $encrypted_45bbcaff3fce_iv \
      -in travis/codesigning.asc.enc -out travis/codesigning.asc -d

    gpg --fast-import travis/codesigning.asc
    mvn --settings travis/settings.xml deploy -Prelease -DskipTests=true
fi
