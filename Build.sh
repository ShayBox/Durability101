#!/usr/bin/env bash

rm -rf jars
mkdir -p jars

for dir in */
do
    # Strip trailing /
    dir=${dir%*/}

    # Detect java version
    case $dir in
        Forge-1.12 | Forge-1.13 | Forge-1.14 | Forge-1.15 | Fabric-1.14 | Fabric-1.15)
            export PATH="/usr/lib/jvm/java-8-openjdk/bin/:$PATH"
            ;;
        *)
            export PATH="/usr/lib/jvm/java-17-openjdk/bin/:$PATH"
            ;;
    esac

    # Gradle Build
    cd "${dir}"
    chmod +x gradlew
    ./gradlew clean build

    # Move jar
    mv build/libs/durability101-0.0.4.jar ../jars/Durability101-${dir,,}-0.0.4.jar

    cd ..
done
