angular.module("RatingSystem", ["ngRoute"])

.controller("computeScoresController", function($scope, $route, $routeParams, $location, $http){

    $scope.computeScores = function() {
      var request = 'http://localhost:8081/rate_clients/';
      request = request + this.industry + '/' + this.country;
      var data = {},
          config = {};
      $http.post(request, data, config)
      .success(function(data, status, headers, config){
          $scope.computeScoresConfirmation = data;
          alert($scope.computeScoresConfirmation);
      })
      .error(function(data, status, header, config){
          console.log('Error occured while computing scores');
          alert('Error occured while computing scores');
      });
    }

})

.controller("getClientScoresController", function($scope, $route, $routeParams, $location, $http){

    $scope.getClientScores = function() {
      var request = 'http://localhost:8081/getassessmentscores/';
      request = request + this.client;
      $http.get(request).then(function(response){
         var asssessmentResults = [];
         for(var i = 0; i < response.data.length; i++) {
             var assessment = [];
             assessment.push(response.data[i]["total_score"]);
             assessment.push(response.data[i]["assess_date"]);
             assessment.push(response.data[i]["global_rel_score"]);
             asssessmentResults.push(assessment);
         }
         $scope.clientAssessments = asssessmentResults;
       });
    }
})

.controller("addClientController", function($scope, $route, $routeParams, $location, $http){

    $scope.addNewClient = function() {
       var requestUrl = 'http://localhost:8081/addclient';
       var clientInfo = {};
       if(this.client_name) {
          clientInfo["client_name"] = this.client_name;
       }
       if(this.client_location) {
         clientInfo["client_location"] = this.client_location;
       }
       if(this.client_division) {
         clientInfo["client_division"] = this.client_division;
       }
       if(this.client_industry) {
         clientInfo["client_industry"] = this.client_industry;
       }
       var request = {
            method: 'POST',
            url: requestUrl,
            headers: {
              'Content-Type': 'application/json'
            },
            data: clientInfo
       };
       $http(request).then(function(response){
          console.log('client added\n');
          alert(response.data);
        },
        function(response){
          console.log('error occured while adding client\n');
          alert(response.data);
        }
       );
    }

})

.config(function($routeProvider, $locationProvider){
  $routeProvider
  .when('/assessmentresults',{
    templateUrl: "./pages/assessmentresults.html",
    controller: "getClientScoresController"
  })
  .when('/rateclients',{
    templateUrl: "./pages/rateclient.html",
    controller: "computeScoresController"
  })
  .when('/addclient',{
    templateUrl: "./pages/addclient.html",
    controller: "addClientController"
  })
});
