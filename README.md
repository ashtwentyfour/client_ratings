# COMPANY/CLIENT RATING APPLICATION

## TO RUN

### START THE MySQL SERVER USING DOCKER

   docker run --name my-sql-db -e MYSQL_ROOT_PASSWORD=abcd -p 8081:3306 -d mysql/mysql-server

   docker run --name my-php-admin -d --link my-sql-db:db -p 8080:80 -e PMA_PASSWORD=abcd phpmyadmin/phpmyadmin

Navigate to docker-machine IP:8080 on the browser and enter the username 'root' and password 'abcd'

Create a new database 'client_ratings' and import the 'client_ratings.sql' file to initialize the DB

### START THE NodeJS SERVER

  docker build -t rating_tool .

  docker run -d --name rating_server -p 5000:8081 rating_tool

Navigate to docker-machine IP:5000/Industry/Country to compute the latest scores
