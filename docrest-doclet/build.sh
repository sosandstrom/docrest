#!/bin/sh
mvn clean install javadoc:javadoc
cp target/site/apidocs/* /var/www/generated_cms/
echo ""
echo ""
echo "------------------------------------------------------------------------"
echo "-------------------------- Build Complete ------------------------------"
echo "------------------------------------------------------------------------"
echo "-- To access goto http://127.0.0.1/generated_cms/"
echo "------------------------------------------------------------------------"
echo ""
