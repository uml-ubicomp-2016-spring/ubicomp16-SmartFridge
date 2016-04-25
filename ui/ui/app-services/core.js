app.controller('two_way_control',function($scope,$http,$interval){
  load_pictures();
  $interval(function(){
    load_pictures();
  },300);
  function load_pictures(){
  $http.get('http://localhost:3000/load').success(function(data){
    $scope.user_names=data;
    $scope.image_date=data;
    $scope.user_image=data;
  });
  };
});
