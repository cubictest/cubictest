#!/bin/bash

#Usage: copyToLocalRepo.sh <version number> 
#e.g. copyToLocalRepo.s 1.8.5

if [ ! $1 ]
then
	echo "Please provide a version number"
	exit -1
fi

BASE_DIR=`pwd`

# Copy hash CubicTest JARs to local repo:

CUBICTEST_REPO_DIR=~/.m2/repository/org/cubictest/cubictest/$1
if [ ! -d $CUBICTEST_REPO_DIR ] 
then
	mkdir $CUBICTEST_REPO_DIR
fi

cd $BASE_DIR/CubicTestPlugin
cp cubictest-$1.jar $CUBICTEST_REPO_DIR
sha1 cubictest-$1.jar | awk '{print $1}' > $CUBICTEST_REPO_DIR/cubictest-$1.jar.sha1
md5 cubictest-$1.jar | awk '{print $1}' > $CUBICTEST_REPO_DIR/cubictest-$1.jar.md5

cp cubictest-$1.pom $CUBICTEST_REPO_DIR
sha1 cubictest-$1.pom | awk '{print $1}' > $CUBICTEST_REPO_DIR/cubictest-$1.pom.sha1
md5 cubictest-$1.pom | awk '{print $1}' > $CUBICTEST_REPO_DIR/cubictest-$1.pom.md5


# Copy and hash Selenium exporter JARs to local repo:

SELENIUM_EXPORTER_DIR=~/.m2/repository/org/cubictest/selenium-exporter/$1
if [ ! -d $SELENIUM_EXPORTER_DIR ]
then
	mkdir $SELENIUM_EXPORTER_DIR
fi

cd $BASE_DIR/CubicTestSeleniumExporter
cp selenium-exporter-$1.jar $SELENIUM_EXPORTER_DIR
sha1 selenium-exporter-$1.jar | awk '{print $1}' > $SELENIUM_EXPORTER_DIR/selenium-exporter-$1.jar.sha1
md5 selenium-exporter-$1.jar | awk '{print $1}' > $SELENIUM_EXPORTER_DIR/selenium-exporter-$1.jar.md5

cp selenium-exporter-$1.pom $SELENIUM_EXPORTER_DIR
sha1 selenium-exporter-$1.pom | awk '{print $1}' > $SELENIUM_EXPORTER_DIR/selenium-exporter-$1.pom.sha1
md5 selenium-exporter-$1.pom | awk '{print $1}' > $SELENIUM_EXPORTER_DIR/selenium-exporter-$1.pom.md5


# Maven 2 runner:

cd $BASE_DIR/CubicTestMojo
mvn clean install -DcreateChecksum=true


#Done:

cd $BASE_DIR

