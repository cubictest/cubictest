rm -r cubictest_m2_jars
mkdir cubictest_m2_jars
cd cubictest_m2_jars
mkdir org
cd org
mkdir cubictest
cd ..
cd ..
cp -r ~/.m2/repository/org/cubictest/* ./cubictest_m2_jars/org/cubictest/
cd cubictest_m2_jars
zip -r ../cubictest_m2_jars.zip *
cd ..



