cd CubicTestPlugin
cp cubictest*.jar ~/.m2/repository/org/cubictest/cubictest/1.8.1
cp cubictest*.pom ~/.m2/repository/org/cubictest/cubictest/1.8.1
cd ../CubicTestSeleniumExporter
cp selenium-exporter*.jar ~/.m2/repository/org/cubictest/selenium-exporter/1.8.1
cp selenium-exporter*.pom ~/.m2/repository/org/cubictest/selenium-exporter/1.8.1
cd ../CubicTestMojo
mvn install
cd .. 


