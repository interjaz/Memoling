app = angular.module("app.directive", []);

app.directive("mlMemobases", function() {
    return {
        restrict: "E",
        scope: {
            mlData: "=",
            mlSelect: "&" // Expects function with memoBaseId argument
        },
        templateUrl: TemplateBase + "/Directive/mlMemoBases.html",
        link: function(scope, element, attrs) {
            
        }
    };
});

app.directive("mlMemos", function() {
 return {
     restrict: "E",
     scope: {
         mlData: "=",
         mlSelect: "&" // Expects function with memoId argument
     },
     templateUrl: TemplateBase + "/Directive/mlMemos.html",
     link: function(scope, element, attrs) {
         
     }
 }; 
});