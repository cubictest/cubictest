if [ ! $1 ]
then
        echo "Please provide a version number, e.g. 1.8.5"
        exit -1
fi

./copyToLocalRepo.sh $@
echo "OK to continue?"
read

./zipRepo.sh $@
echo "OK to continue?"
read

echo "Repo files:"
scp -r ./cubictest_m2_jars/* schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/m2repo/

#echo "Repo zip file:"
#scp -r ./cubictest_m2_jars.zip schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/m2repo/

#echo "Update site files: (CTRL+C to abort)"
#scp -r ./CubicTestLocalUpdateSite/*  schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/update/

#echo "Update site zip file:"
#scp -r ./CubicTestLocalUpdateSite.zip  schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/update/

