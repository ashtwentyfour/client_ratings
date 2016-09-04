docker run --name my-sql-db -e MYSQL_ROOT_PASSWORD=12Slashaxel -p 8081:3306 -d mysql/mysql-server

docker run --name my-php-admin -d --link my-sql-db:db -p 8080:80 -e PMA_PASSWORD=12Slashaxel phpmyadmin/phpmyadmin
