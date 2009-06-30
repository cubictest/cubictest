#!/bin/bash

BASE_DIR=`pwd`

if [ ! $1 ]
then
        echo "Please provide a version number, e.g. 1.8.5"
        exit -1
fi

if [ -e cubictest_m2_jars.zip ]
then 
	rm cubictest_m2_jars.zip
fi

if [ -d cubictest_m2_jars ]
then 
	rm -r cubictest_m2_jars
fi


mkdir cubictest_m2_jars
cd cubictest_m2_jars

mkdir org
cd org

mkdir cubictest
cd cubictest
MOD_BASE_DIR=`pwd`

mkdir cubictest
cd cubictest
mkdir $1

cd "$MOD_BASE_DIR"
mkdir selenium-exporter
cd selenium-exporter
mkdir $1

cd "$MOD_BASE_DIR"
mkdir cubictest-selenium-rc
cd cubictest-selenium-rc
mkdir $1

cd "$BASE_DIR"

CUBICFILES=~/.m2/repository/org/cubictest/cubictest/$1
EXPORTERFILES=~/.m2/repository/org/cubictest/selenium-exporter/$1
SELENIUMRCFILES=~/.m2/repository/org/cubictest/cubictest-selenium-rc/$1

cp -r "$CUBICFILES"/* ./cubictest_m2_jars/org/cubictest/cubictest/$1/
cp -r "$EXPORTERFILES"/* ./cubictest_m2_jars/org/cubictest/selenium-exporter/$1/
cp -r "$SELENIUMRCFILES"/* ./cubictest_m2_jars/org/cubictest/cubictest-selenium-rc/$1/


cd cubictest_m2_jars

#Linux: 
#zip -r ../cubictest_m2_jars.zip *

#Windows
echo "FYI: 7-zip must be on PATH"
7z a -r ../cubictest_m2_jars.zip *

cd "$BASE_DIR"


