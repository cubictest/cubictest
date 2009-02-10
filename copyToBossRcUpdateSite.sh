#!/bin/bash

if [ ! $1 ]
then
        echo "Please provide a version number, e.g. 1.8.5"
        exit -1
fi

# Build release dir structure
./zipUpdateSite.sh

echo "Update site files: (CTRL+C to skip)"
ssh schwarz@boss.bekk.no "mkdir /srv/www/htdocs/cubictest/update-release-candidates/${1}/"
scp -r ./CubicTestLocalUpdateSite/* schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/update-release-candidates/$1/
