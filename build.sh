#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p .m2/repository/com/magmaguy/EliteMobs/8.7.4-SNAPSHOT
if [[ ! -f stub-elitemobs.jar ]]; then
  podman run --rm -v "$PWD":/app:Z -v /home/mc/test/libraries:/libs:Z -w /app/stub-elitemobs docker.io/library/eclipse-temurin:25-jdk \
    sh -c "javac --release 21 -cp /libs/org/purpurmc/purpur/purpur-api/26.2.build.2609-stable/purpur-api-26.2.build.2609-stable.jar:/libs/com/google/guava/guava/33.6.0-jre/guava-33.6.0-jre.jar:/libs/net/kyori/adventure-api/4.25.0/adventure-api-4.25.0.jar com/magmaguy/elitemobs/api/EliteMobSpawnEvent.java && jar cf /app/stub-elitemobs.jar com/magmaguy/elitemobs/api/EliteMobSpawnEvent.class"
fi
cp -f stub-elitemobs.jar .m2/repository/com/magmaguy/EliteMobs/8.7.4-SNAPSHOT/EliteMobs-8.7.4-SNAPSHOT.jar
podman run --rm \
  -v "$PWD":/app:Z \
  -v "$PWD/.m2":/root/.m2:Z \
  -w /app \
  docker.io/library/maven:3.9-eclipse-temurin-25 \
  mvn -B clean package -DskipTests -Dbuild.number=mc
echo "Built: $PWD/target/GPFlags-5.13.9.mc.jar"
