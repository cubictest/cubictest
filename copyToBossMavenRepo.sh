if [ ! $1 ]
then
        echo "Please provide a version number, e.g. 1.8.5"
        exit -1
fi

# Copy jars and poms, generate checksums
./buildAndCopyToLocalMavenRepo.sh $@
echo "OK to continue? (CTRL+C to abort)"
read

# Build release dir structure
./zipLocalMavenRepo.sh $@
echo "OK to continue? (CTRL+C to abort)"
read



echo "Repo files:"
#Linux:
#scp -r ./cubictest_m2_jars/* schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/m2repo/

#Windows:
#echo "FYI: WinSCP must be on PATH"
winscp.com /command "open schwarz@boss.bekk.no" "put ./cubictest_m2_jars/* /srv/www/htdocs/cubictest/m2repo/" "exit"



echo "Repo zip file:"
#Linux:
#scp -r ./cubictest_m2_jars.zip schwarz@boss.bekk.no:/srv/www/htdocs/cubictest/m2repo/

#Windows:
winscp.com /command "open schwarz@boss.bekk.no" "put ./cubictest_m2_jars.zip /srv/www/htdocs/cubictest/m2repo/" "exit"
