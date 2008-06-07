#!/bin/bash

if [ ! $1 ]
then
        echo "Please provide a version number, e.g. 1.8.5-rc1"
        exit -1
fi


echo "Repo files: (CTRL+C to skip)"
scp -r ./cubictest_m2_jars/* schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/m2repo/

#echo "Repo zip file:"
#scp -r ./cubictest_m2_jars.zip schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/m2repo/

echo "Update site files: (CTRL+C to skip)"
ssh schwarz@boss.bekk.no "mkdir /srv/www/htdocs/cubictest/update-release-candidates/${1}/"
scp -r ./CubicTestLocalUpdateSite/* schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/update-release-candidates/$1/

#echo "Update site zip file:"
#scp -r ./CubicTestLocalUpdateSite.zip  schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/update-release-candidates/$1/

