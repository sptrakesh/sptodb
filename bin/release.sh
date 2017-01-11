#!/bin/sh

# Script to create a tagged release in subversion

if [ $# -ne 1 ]
then
  echo "Usage: $0 <version>"
  exit 1
fi

svn copy https://sptodb.dev.java.net/svn/sptodb/trunk \
  https://sptodb.dev.java.net/svn/sptodb/tags/$1 \
  -m "Created release: $1 of library"
