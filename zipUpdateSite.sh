#!/bin/bash

if [ -d CubicTestLocalUpdateSite ]
then 
	rm -r CubicTestLocalUpdateSite
fi

if [ -e CubicTestLocalUpdateSite.zip ]
then 
	rm CubicTestLocalUpdateSite.zip
fi

mkdir CubicTestLocalUpdateSite
cp -r CubicTestUpdateSite/* CubicTestLocalUpdateSite/
cd CubicTestLocalUpdateSite
rm -rf .svn
cd features
rm -rf .svn
cd ..
cd plugins
rm -rf .svn
cd ..
cd ..
zip -r CubicTestLocalUpdateSite.zip CubicTestLocalUpdateSite

