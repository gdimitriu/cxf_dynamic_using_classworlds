# CXF using REST on classworlds and dynamic routes

This is compiled and run with java 13 without modules.

Replace /home/gaby/public-git/cxf_dynamic_using_classworlds everywhere it is in the 
code or file with the location where the project has been checkout.

## BUILD
mvn install in cxf_dynamic_using_classworlds


after build the project make package of route1

run from application/target

run with

java -classpath ./lib/plexus-classworlds-2.8.0.jar -Dclassworlds.conf=./config/classworlds.conf -Dlog4j.configuration=file:/home/gaby/public-git/cxf_dynamic_using_classworlds/application/target/config/LogConfig.xml org.codehaus.plexus.classworlds.launcher.Launcher 

## TESTING
see the open api to make request to the REST API.

from client/target

run : java -jar client-1.0-SNAPSHOT.jar start

http://localhost:8080/openapi.json

http://localhost:8080/api-docs

see the open api with the routes and make request to the REST API.

make the package route2

from client/target

run: java -jar client-1.0-SNAPSHOT.jar reconfigure

see the new api that have been modified.