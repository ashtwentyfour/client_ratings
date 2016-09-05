var express = require('express');

var mysql = require('mysql');

var app = express();

var exec = require('child_process').exec

const EventEmitter = require('events');

var bodyParser = require('body-parser');

var urlencodedParser = bodyParser.urlencoded({ extended: true });

app.use(express.static('public'));

app.use(bodyParser.json());

var code_compile = 'javac -d rating_system/bin -sourcepath rating_system/src rating_system/src/scoring/*.java';

/*
   compute scores for companies from a particular industry and location
*/

app.post('/rate_client/:industry/:country', function(req,res){

    var body = new EventEmitter();
    // compile java classes - scoring system
    var compile = exec(code_compile, function(error, stdout, stderr){
           if (error !== null) {
               console.log('exec error: ' + error);
           }
           else {
             console.log('code compiled');
             body.emit('update');
           }
    });
    // compute scores and update database
    body.on('update', function() {
      var code_run = 'java -cp rating_system/external_jars/mysql-connector-java-5.1.39-bin.jar:rating_system/bin scoring.Driver';
      code_run = code_run + ' ' + req.params.industry + ' ' + req.params.country;
      var run = exec(code_run, function(error, stdout, stderr){
             if (error !== null) {
                 console.log('exec error: ' + error);
             }
             res.end('rating complete\n');
      });
    });
});

/*
    retrieve all the assessment results for a particular company
*/

app.get('/getassessmentscores/:client', function(req, res){

  var body = new EventEmitter();
  // db parameters
  var con = mysql.createConnection({
    host: "192.168.99.100",
    user: "root",
    password: "abcd",
    port: 8081,
    database: 'client_ratings'
  });
  // connect to the db and execute queries
  con.connect(function(err) {
    if(err) {
      console.log('Error connecting to Db');
      return;
    }
    console.log('Connection to DB established');
    body.emit('connected');
  });
  // select client info from the client table
  body.on('connected', function(){
    var query = 'SELECT * FROM client WHERE client_name = "' + req.params.client + '"';
    con.query(query,function(err,rows){
       if(err) throw err;
       var client_id = (rows[0].client_id).toString();
       // use the client_id to retrieve the assessment results
       var query_final = 'SELECT * FROM assessments WHERE client_id = "' + client_id + '"';
       con.query(query_final, function(err2,rows2){
          if(err2) throw err2;
          // return json object with the assessment info
          res.json(rows2);
       });
    });
  });
});

app.post('/addcompany', function(req,res){

    var query = 'INSERT INTO client SET ?';

    var body = new EventEmitter();
    // db parameters
    var con = mysql.createConnection({
      host: "192.168.99.100",
      user: "root",
      password: "abcd",
      port: 8081,
      database: 'client_ratings'
    });
    // connect to the db and execute queries
    con.connect(function(err) {
      if(err) {
        console.log('Error connecting to Db');
        return;
      }
      console.log('Connection to DB established');
      body.emit('connected');
    });
    // add client to database
    body.on('connected', function(){
       con.query(query, req.body, function(err,resp){
          if(err) throw err;
          res.end('added new client\n');
       });
    });
});


// listen on port 8081
var server = app.listen(8081, function () {

  var host = '0.0.0.0';
  var port = server.address().port
  console.log("app listening at http://%s:%s", host, port)

})
