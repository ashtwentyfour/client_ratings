javac -d bin -sourcepath src src/scoring/*.java

java -cp external_jars/mysql-connector-java-5.1.39-bin.jar:bin scoring.Driver

docker run --name my-sql-db -e MYSQL_ROOT_PASSWORD=abcd -p 8081:3306 -d mysql/mysql-server

docker run --name my-php-admin -d --link my-sql-db:db -p 8080:80 -e PMA_PASSWORD=abcd phpmyadmin/phpmyadmin
