angular.module("RatingSystem", ["ngRoute"])

.controller("computeScoresController", function($scope, $route, $http){

    $scope.computeScores = function() {
      var request = 'http://localhost/rate_client/';
      request = request + this.industry + '/' + this.client;
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
