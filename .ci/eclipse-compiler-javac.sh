#!/bin/bash
set -e

ECJ_JAR="ecj-4.7.jar"
ECJ_MAVEN_VERSION="R-4.7-201706120950"
ECJ_PATH=~/.m2/repository/$ECJ_MAVEN_VERSION/$ECJ_JAR

if [ ! -f ~/.m2/repository/$ECJ_MAVEN_VERSION/$ECJ_JAR ]; then
    wget http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/eclipse/downloads/drops4/$ECJ_MAVEN_VERSION/$ECJ_JAR -P $ECJ_PATH
fi

mkdir -p target/classes
mkdir -p target/eclipse

RESULT_FILE=target/eclipse/report.txt

echo "Executing eclipse compiler, output is redirected to $RESULT_FILE..."
java -jar $ECJ_PATH -target 1.8 -source 1.8 -cp $1 \
        -nowarn:[./target/generated-sources/antlr] -d target/eclipse-compile \
        -enableJavadoc src/main/java target/generated-sources/antlr -properties config/org.eclipse.jdt.core.prefs \
    > $RESULT_FILE 2>&1 | true

echo "Checking for ERRORs in $RESULT_FILE ..."
if [[ $(grep ERROR $RESULT_FILE | cat | wc -l) > 0 ]]; then
  cat $RESULT_FILE
  false
fi
