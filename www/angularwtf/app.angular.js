var app = angular.module("memoling", ["ngRoute"])

app.config(function($routeProvider) {
  
    
    $routeProvider
        .when("/test", {
            template: "test"
            
        })
        .otherwise({
            template: "404"
            
        })
    
})