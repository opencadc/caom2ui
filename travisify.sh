#!/bin/bash

cd caom2-ui-server && gradle clean build javadoc install && cd ..
cd caom2-meta-ui && gradle clean dockerize && cd ..
cd caom2-search-server && gradle clean build install
cd examples/default
gradle -i -Pdocker_image_name=opencadc/archive-search:SNAPSHOT clean dockerize
cd ../_int_test_
docker-compose up -d
docker ps -a
cd ../default
gradle -i -PintTest_selenium_server_url=http://localhost:4444 -PintTest_web_app_url=http://localhost -PintTest_web_app_endpoint=/search/ intTestFirefox
