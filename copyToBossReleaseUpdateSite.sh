if [ ! $1 ]
then
        echo "Please provide a version number, e.g. 1.8.5"
        exit -1
fi

# Build release dir structure
./zipUpdateSite.sh

echo "Update site files: (CTRL+C to abort)"
scp -r ./CubicTestLocalUpdateSite/*  schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/update/

echo "Update site zip file:"
scp -r ./CubicTestLocalUpdateSite.zip  schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/update/

