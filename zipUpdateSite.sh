rm -r CubicTestLocalUpdateSite
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
rm .project
cd ..
zip -r CubicTestLocalUpdateSite.zip CubicTestLocalUpdateSite

