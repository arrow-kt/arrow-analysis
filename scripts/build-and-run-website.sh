#!/bin/bash

cd $(dirname $0)/..
./gradlew clean buildMetaDoc
./gradlew :arrow-analysis:dokkaJekyll
bundle install --gemfile docs/Gemfile --path vendor/bundle
BUNDLE_GEMFILE=docs/Gemfile bundle exec jekyll serve -s docs/docs/