#!/bin/bash

#Usage: copyToLocalMavenRepo.sh <version number> 
#e.g. copyToLocalMavenRepo.s 1.8.5

if [ ! $1 ]
then
	echo "Please provide a version number"
	exit -1
fi


sha1() {
	if [ ! -x sha1 ]
	then
		#probably cygwin
		sha1sum "$@" | awk '{print $1}'
	else
		#linux
		sha1 "$@"
	fi
}

md5() {
	if [ ! -x md5 ]
	then
		#probably cygwin
		md5sum "$@" | awk '{print $1}'
	else
		#linux
		md5 "$@"
	fi
}


BASE_DIR=`pwd`

# Copy hash CubicTest JARs to local repo:

CUBICTEST_REPO_DIR=~/.m2/repository/org/cubictest/cubictest/$1
if [ ! -d "$CUBICTEST_REPO_DIR" ] 
then
	mkdir -p "$CUBICTEST_REPO_DIR"
fi

cd $BASE_DIR/CubicTestSeleniumExporter/lib
cp cubictest-$1.jar "$CUBICTEST_REPO_DIR"
sha1 cubictest-$1.jar | awk '{print $1}' > "$CUBICTEST_REPO_DIR/cubictest-$1.jar.sha1"
md5 cubictest-$1.jar | awk '{print $1}' > "$CUBICTEST_REPO_DIR/cubictest-$1.jar.md5"

cd $BASE_DIR/CubicTestPlugin
cp cubictest-$1.pom "$CUBICTEST_REPO_DIR"
sha1 cubictest-$1.pom | awk '{print $1}' > "$CUBICTEST_REPO_DIR/cubictest-$1.pom.sha1"
md5 cubictest-$1.pom | awk '{print $1}' > "$CUBICTEST_REPO_DIR/cubictest-$1.pom.md5"


# Copy and hash Selenium exporter JARs to local repo:

SELENIUM_EXPORTER_DIR=~/.m2/repository/org/cubictest/selenium-exporter/$1
if [ ! -d "$SELENIUM_EXPORTER_DIR" ]
then
	mkdir -p "$SELENIUM_EXPORTER_DIR"
fi

cd $BASE_DIR/CubicTestSeleniumExporter/lib
cp selenium-exporter-$1.jar "$SELENIUM_EXPORTER_DIR"
sha1 selenium-exporter-$1.jar | awk '{print $1}' > "$SELENIUM_EXPORTER_DIR/selenium-exporter-$1.jar.sha1"
md5 selenium-exporter-$1.jar | awk '{print $1}' > "$SELENIUM_EXPORTER_DIR/selenium-exporter-$1.jar.md5"

cd $BASE_DIR/CubicTestSeleniumExporter
cp selenium-exporter-$1.pom "$SELENIUM_EXPORTER_DIR"
sha1 selenium-exporter-$1.pom | awk '{print $1}' > "$SELENIUM_EXPORTER_DIR/selenium-exporter-$1.pom.sha1"
md5 selenium-exporter-$1.pom | awk '{print $1}' > "$SELENIUM_EXPORTER_DIR/selenium-exporter-$1.pom.md5"


# Copy hash CubicTest Selenium RC JARs to local repo:

SELENIUM_RC_DIR=~/.m2/repository/org/cubictest/cubictest-selenium-rc/$1
if [ ! -d "$SELENIUM_RC_DIR" ] 
then
	mkdir -p "$SELENIUM_RC_DIR"
fi

cd $BASE_DIR/CubicTestSeleniumExporter/lib
cp cubictest-selenium-rc-$1.jar "$SELENIUM_RC_DIR"
sha1 cubictest-selenium-rc-$1.jar | awk '{print $1}' > "$SELENIUM_RC_DIR/cubictest-selenium-rc-$1.jar.sha1"
md5 cubictest-selenium-rc-$1.jar | awk '{print $1}' > "$SELENIUM_RC_DIR/cubictest-selenium-rc-$1.jar.md5"

cd $BASE_DIR/CubicTestSeleniumRC
cp cubictest-selenium-rc-$1.pom "$SELENIUM_RC_DIR"
sha1 cubictest-selenium-rc-$1.pom | awk '{print $1}' > "$SELENIUM_RC_DIR/cubictest-selenium-rc-$1.pom.sha1"
md5 cubictest-selenium-rc-$1.pom | awk '{print $1}' > "$SELENIUM_RC_DIR/cubictest-selenium-rc-$1.pom.md5"


#Done:
echo Jars copied to local m2 repo.
cd $BASE_DIR


