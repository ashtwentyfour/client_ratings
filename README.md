# COMPANY/CLIENT RATING APPLICATION (PROOF OF CONCEPT)

## TO RUN

### START THE MySQL SERVER USING DOCKER

   docker run --name my-sql-db -e MYSQL_ROOT_PASSWORD=abcd -p 8081:3306 -d mysql/mysql-server

   docker run --name my-php-admin -d --link my-sql-db:db -p 8080:80 -e PMA_PASSWORD=abcd phpmyadmin/phpmyadmin

Navigate to docker-machine-IP:8080 on the browser and enter the username 'root' and password 'abcd'

Create a new database 'client_ratings' and import the 'client_ratings.sql' file to initialize the DB

### START THE NodeJS SERVER

   docker build -t rating_tool .

   docker run -d --name rating_server -p 5000:8081 rating_tool

### COMPUTE SCORES FOR CLIENTS OF A GIVEN INDUSTRY AND LOCATION

   curl -X POST http://docker-machine-IP:5000/rate_client/Industry/Country

### RETRIEVE ASSESSMENT INFORMATION FOR A CLIENT

   curl http://docker-machine-IP:5000/getassessmentscores/Client

### ADD A NEW CLIENT/COMPANY

   curl -X POST http://docker-machine-IP:5000/addcompany --header 'Content-Type:application/json' -d '{"rater_id": 1, "client_name": "Apple", "client_industry": "Consumer Electronics", "parent_company": "Apple", "client_division": "Hardware", "client_location": "USA", "industry_size": 2000}' --header 'Accept:text/html'

### ADD A NEW DOMAIN AND CORRESPONDING QUESTION LIST TO THE database

   curl -X POST http://docker-machine-IP:5000/adddomain --header 'Content-Type:application/json' -d '{"domain_name": "Web Security", "domain_description": "Web Security", "questions": [{"question_text": "How secure is the web security ?", "question_rank": 0.2}, {"question_text": "What is the web ?", "question_rank": 0.1}]}'
