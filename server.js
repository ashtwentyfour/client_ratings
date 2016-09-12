var express = require('express');

var mysql = require('mysql');

var app = express();

var exec = require('child_process').exec

const EventEmitter = require('events');

var bodyParser = require('body-parser');

var urlencodedParser = bodyParser.urlencoded({ extended: true });

app.use(express.static('public'));

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

app.use(bodyParser.json());

// compile scoring system code
var code_compile = 'javac -d rating_system/bin -sourcepath rating_system/src rating_system/src/scoring/*.java';

// MySQL db connection
var con = mysql.createConnection({
  host: "192.168.99.100",
  user: "root",
  password: "abcd",
  port: 8081,
  database: 'client_ratings'
});

/*
   compute scores for companies from a particular industry and location
*/

app.post('/rate_clients/:industry/:country', function(req,res){

    var body = new EventEmitter();
    // compile java classes - scoring system
    var compile = exec(code_compile, function(error, stdout, stderr){
           if (error !== null) {
               console.log('java exec error: ' + error);
               res.end('code compilation error');
           }
           else {
             console.log('code compiled\n');
             body.emit('update');
           }
    });
    // compute scores and update database
    body.on('update', function() {
      var code_run = 'java -cp rating_system/external_jars/mysql-connector-java-5.1.39-bin.jar:rating_system/bin scoring.Driver';
      code_run = code_run + ' ' + req.params.industry + ' ' + req.params.country;
      var run = exec(code_run, function(error, stdout, stderr){
             if (error !== null) {
                 console.log('java exec error: ' + error);
                 res.end('error occurred while computing scores');
             }
             res.end('rating completed successfully !');
      });
    });
});

/*
    retrieve all the assessment results for a particular company
*/

app.get('/getassessmentscores/:client', function(req, res){

    var query = 'SELECT * FROM client WHERE client_name = "' + req.params.client + '"';
    con.query(query,function(err,rows){
       if(err) throw err;
       var client_id = (rows[0].client_id).toString();
       // use the client_id to retrieve the assessment results
       var query_final = 'SELECT * FROM assessments WHERE client_id = "' + client_id + '"';
       con.query(query_final, function(clienterr,clientrow){
          if(clienterr) {
             console.log(clienterr);
             res.end('error occurred while retrieving client information');
          };
          // return json object with the assessment info
          res.json(clientrow);
       });
    });

});

/*
    add a client company to the system
*/

app.post('/addclient', function(req,res){

   var query = 'INSERT INTO client SET ?';
   // add client to database
   con.query(query, req.body, function(err,rowres){
       if(err) {
         console.log(err);
         res.end('error occurred while adding client information');
       }
       res.end('added new client !');
   });

});

/*
    add a domain to the system
*/

app.post('/adddomain', function(req,res){

   // retrieve the domain id of the most recently added domain
   var get_max_dom_id = 'SELECT MAX(domain_id) FROM domain_info';
   con.query(get_max_dom_id, function(err,row){
        if(err) {
          console.log(err);
          res.end('error occurred while retrieving domain information');
        }
        var max_id = row[0]['MAX(domain_id)'];
        // id of the new/next domain
        max_id = max_id + 1;
        var query = 'INSERT INTO `domain_info`(`domain_id`, `domain_name`, `domain_description`) VALUES (';
        query = query + max_id.toString() + ', ';
        query = query + '"' + req.body['domain_name'] + '", ';
        query = query + '"' + req.body['domain_description'] + '")';
        con.query(query, function(domadderr,resp){
           if(domadderr) {
             console.log(domadderr);
             res.end('error occurred while adding domain details')
           }
           for(var i = 0; i < req.body['questions'].length; i++) {
              req.body['questions'][i]['domain_id'] = max_id;
              con.query('INSERT INTO questions SET ?', req.body['questions'][i], function(quesadderr,quesres){
                  if(quesadderr) {
                     console.log(quesadderr);
                     res.end('error occurred while adding question');
                  };
                  console.log('question added');
              });
           }
           res.end('domain and initial question set added to database\n');
        });
   });

});

/*
    create/register a new client assessment
*/

app.post('/createnewassessment', function(req,res){

   // client id of the client whose assessment is being added
   var query = 'SELECT * FROM client WHERE client_name = "' + req.body['client'] + '"';
   con.query(query, function(err,row){
         if(err) {
           console.log(err);
           res.end('error occurred while retrieving client information');
         }
         var client_id = row[0]["client_id"];
         // get domain ids of the domains that are part of the assessment
         var domain_ids = [];
         var domain_query = 'SELECT domain_id FROM domain_info WHERE domain_name = "';
         for(var i = 0; i < req.body["domains"].length; i++) {
           con.query(domain_query+req.body["domains"][i]["name"]+'"', function(domerr,domid){
               if(domerr) {
                 console.log(domerr);
                 res.end('error occurred while identifying domain');
               }
               domain_ids.push(domid[0]["domain_id"]);
           });
         }
         var assessment = {};
         assessment["client_id"] = client_id;
         assessment["user_id"] = req.body["user_id"];
         assessment["assess_date"] = req.body["date"];
         var max_id = -1;
         // add assessment ot the list of assessments
         con.query("INSERT INTO assessments SET ?", assessment, function(asserr, assres){
             if(asserr) {
                console.log(asserr);
                res.end('error occurred while adding assessment');
             }
             // create the (domain , assessment) pairs
             con.query("SELECT MAX(assess_id) FROM assessments", function(errmaxass, rowmaxass){
                 if(errmaxass) {
                   console.log(errmaxass);
                   res.end('error occurred while adding assessment');
                 }
                 max_id = rowmaxass[0]['MAX(assess_id)'];
                 for(var i = 0; i < domain_ids.length; i++) {
                    var domassess = {};
                    domassess["domain_id"] = domain_ids[i];
                    domassess["assess_id"] = max_id;
                    domassess["domain_name"] = req.body["domains"][i]["name"];
                    domassess["domain_explanation"] = req.body["domains"][i]["explanation"];
                    con.query("INSERT INTO domain SET ?", domassess, function(errdomass, resdomass){
                        if(errdomass) {
                          console.log(errdomass);
                          res.end('error occurred while adding domain-assessment pair');
                        }
                        console.log('domain-assessment added\n');
                    });
                 }
                 res.end('assessment '+(max_id).toString()+' created\n');
             });
      });
  });

});

/*
    add a question for a particular domain to the system
*/

app.post('/addquestion', function(req,res){

   var getdomainid = 'SELECT domain_id FROM domain_info WHERE domain_name = '+'"' +req.body["domain_name"]+'"';
   con.query(getdomainid, function(err,row){
        if(err) {
          console.log(err);
          res.end('error occurred while retrieving domain information');
        }
        var domainid = row[0]["domain_id"];
        var question = {
          domain_id: domainid,
          question_text: req.body["text"],
          question_rank: req.body["weight"]
        };
        con.query("INSERT INTO questions SET ?", question, function(qerr,qres){
            if(qerr) {
              console.log(qerr);
              res.end('error occurred while adding question');
            }
            res.end("new question added\n");
        });
   });

});


// listen on port 8081
var server = app.listen(8081, function () {

  var host = '0.0.0.0';
  var port = server.address().port
  console.log("app listening at http://%s:%s", host, port)

});
