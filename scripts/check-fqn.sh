#!/usr/bin/env bash

violations=$(
  grep -rnE '[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+\.[A-Z][a-zA-Z0-9_]*' \
    --include="*.kt" --include="*.java" --include="*.scala" \
    --exclude-dir=target \
    --exclude-dir=generated \
    --exclude-dir=.git \
    . \
    | grep -vE ':[0-9]+:\s*(package |import |//|\*|/\*)' \
    | grep -vE '"[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+\.[A-Z][a-zA-Z0-9_]*"'
)

if [ -n "$violations" ]; then
  echo "ERROR: Fully-qualified class references found:"
  echo "$violations"
  exit 1
fi
