app.controller('two_way_control',function($scope,$http,$interval){
  load_pictures();
  $interval(function(){
    load_pictures();
  },300);
  // Loading all the data from the database
  function load_pictures(){
  $http.get('http://localhost:3000/load').success(function(data){
    $scope.user_names=data;
    $scope.image_dates=data;
    $scope.user_images=data;
    $scope.happinesss=data;
  });
  };
});
