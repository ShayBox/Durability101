#!/usr/bin/env bash

# Trap SIGINT signal (Ctrl-C)
trap exit 1 SIGINT

# Cleanup
rm -r Build
mkdir Build

# Find all subdirectories containing gradlew script
for dir in */
do
    # Navigate into the directory
    cd "${dir}" || continue

    # Check if gradlew script exists
    if [ -f "gradlew" ]
    then
        # Clean
        rm -rf .gradle

        # Strip trailing /
        dir=${dir%*/}

        # Detect java version
        case $dir in
            Fabric-1.20-6 | Fabric-1.21-1 | Fabric-1.21.2-4 | Forge-1.20-6 | Forge-1.21-2 | Forge-1.21.2-4)
                export PATH="/usr/lib/jvm/java-21-temurin/bin/:$PATH"
                ;;
            *)
                export PATH="/usr/lib/jvm/java-17-temurin/bin/:$PATH"
                ;;
        esac

        # Grant execute permission to gradlew script
        chmod +x gradlew

        # Clean and build using gradlew
        ./gradlew clean build

        # Get the built JAR file
        jar_file=$(find build/libs -name "durability101-*.jar" -type f)

        # Extract version number from the JAR file name
        version=$(basename "$jar_file" | grep -oP 'durability101-\K[0-9]+\.[0-9]+\.[0-9]+')

        # Move the built JAR file to the specified directory
        mv build/libs/durability101-"${version}".jar ../Build/Durability101-"${dir,,}"-"${version}".jar

        # Cleanup
        rm -rf .idea .vscode build logs run
    fi

    # Navigate back to the parent directory
    cd ..
done
