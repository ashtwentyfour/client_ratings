var express = require('express');

var app = express();

var exec = require('child_process').exec

const EventEmitter = require('events');

var code_compile = 'javac -d rating_system/bin -sourcepath rating_system/src rating_system/src/scoring/*.java';

var code_run = 'java -cp rating_system/external_jars/mysql-connector-java-5.1.39-bin.jar:rating_system/bin scoring.Driver';

app.get('/rate_client/:industry/:country', function(req,res){

    var body = new EventEmitter();

    var compile = exec(code_compile, function(error, stdout, stderr){
           if (error !== null) {
               console.log('exec error: ' + error);
           }
           else {
             console.log('code compiled');
             body.emit('update');
             //res.send('java classes compiled');
           }
    });

    body.on('update', function() {
      code_run = code_run + ' ' + req.params.industry + ' ' + req.params.country;
      var run = exec(code_run, function(error, stdout, stderr){
             if (error !== null) {
                 console.log('exec error: ' + error);
             }
             res.end('rating complete');
      });
    });
});

var server = app.listen(8081, function () {

  var host = '0.0.0.0';
  var port = server.address().port

  console.log("app listening at http://%s:%s", host, port)

})
