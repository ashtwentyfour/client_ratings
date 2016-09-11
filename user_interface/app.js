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
});
