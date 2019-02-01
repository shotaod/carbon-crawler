#!/usr/bin/env bash

if [ -d './stats' ]; then
  rm -r ./stats/*
else
  mkdir ./stats
fi

webpack --mode production --profile --json > ./stats/webpack.stats.json
webpack-bundle-analyzer ./stats/webpack.stats.json ./build -m static -r ./stats/report.html --no-open
