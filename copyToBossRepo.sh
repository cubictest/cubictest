if [ ! $1 ]
then
        echo "Please provide a version number, e.g. 1.8.5"
        exit -1
fi

# Copy jars and poms, generate checksums
./copyToLocalRepo.sh $@
echo "OK to continue? (CTRL+C to abort)"
read

# Build release dir structure
./zipRepo.sh $@
echo "OK to continue? (CTRL+C to abort)"
read

echo "Repo files:"
scp -r ./cubictest_m2_jars/* schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/m2repo/

echo "Repo zip file:"
scp -r ./cubictest_m2_jars.zip schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/m2repo/

