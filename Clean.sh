#!/usr/bin/env bash

rm -rf jars

for dir in */
do
    # Strip trailing /
    dir=${dir%*/}

    # Gradle clean
    cd $dir
    rm -rf build logs
    cd ..
done
