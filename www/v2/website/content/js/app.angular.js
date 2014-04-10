var app = angular.module('app', ['ngRoute', 'app.directive', 'app.service'])
var TemplateBase = "website/view/App/Template/";


function setFacebookUserId(facebookUserId) {
    if(facebookUserId != null) {
        console.log('providing credentias')
        appCtrl.setFacebookUser(facebookUserId)
        //window.location.replace("?#/")
    } else {
        console.log('log out')
        window.location.replace("?#/logout")
    }
}

var viewModel = app.factory("viewModel", function($location, languageList) {
    return {
        
        findMemoBase: function(memoBaseId) {
          if(this.MemoBases.length == 0) {
              return null;
          }
            for(var i=0;i<this.MemoBases.length;i++) {
                var memoBase = this.MemoBases[i];
                if(memoBase.memoBaseId == memoBaseId) {
                    return memoBase;   
                }
            }
            return null;
        },
        findMemo: function(memoId) {
               
          if(this.Memos.length == 0) {
              return null;
          }
            for(var i=0;i<this.Memos.length;i++) {
                var memo = this.Memos[i];
                if(memo.memoId == memoId) {
                    return memo;   
                }
            }
            return null;
            
        },
        
        MemoBaseId: null,
        MemoBases: [],
        MemoBase: null,
        MemoId: null,
        Memos: [],
        Memo: null,
        Action: "details",
        Languages: languageList
    };

});

app.run(function($rootScope, viewModel, dataContext) {
    $rootScope.TemplateBase = TemplateBase;
})

app.config(function($routeProvider) {

    $routeProvider
    .when("/", {
        templateUrl: TemplateBase + "main.html",
        controller: 'ViewCtrl',
        resolve: {
            facebookUserId: appCtrl.loadFacebookUserId
        },
        reloadOnSearch: false
    })
    .when("/logout", {
        templateUrl: TemplateBase + "logout.html"
    })
    .otherwise({
        templateUrl: TemplateBase + "404.html"
    })

})


var appCtrl = app.controller("AppCtrl", function($scope, $q) {

    appCtrl.facebookUserDefer = $q.defer();
    if(appCtrl.facebookUserId != null) {
        appCtrl.facebookUserDefer.resolve(appCtrl.facebookUserId);
    }


})

appCtrl.setFacebookUser = function(facebookUserId) {
    appCtrl.facebookUserId = facebookUserId;
    if(appCtrl.facebookUserDefer != null) {
        appCtrl.facebookUserDefer.resolve(facebookUserId);
    }
}

appCtrl.loadFacebookUserId = function($q) {
    return appCtrl.facebookUserDefer.promise
}


var viewCtrl = app.controller("ViewCtrl", function($scope,$routeParams,$location,viewModel,dataContext) {

    $scope.ViewModel = viewModel;
    
    viewModel.MemoBaseId = $routeParams.memoBaseId;
    viewModel.MemoId = $routeParams.memoId;
    
    // Load MemoBases    
    var getMemoBases = function() {
        dataContext.getMemoBases()
        .then(function(memoBases) {
            viewModel.MemoBases = memoBases;
        });
    };
    
    var getMemoBase = function(memoBaseId) {
        viewModel.MemoBaseId = memoBaseId;
        dataContext.getMemoBase(memoBaseId)
        .then(function(memoBase) {
            viewModel.MemoBase = memoBase; 
            return dataContext.getMemos(memoBaseId);
        })
        .then(function(memos) {
            viewModel.Memos = memos; 
        });
    };
    
    getMemoBases();
    
    $scope.onSelectMemoBase = function(memoBaseId) {
        getMemoBase(memoBaseId);
        $location.search({"memoBaseId":viewModel.MemoBaseId});
        
    };
    
    $scope.onSelectMemo = function(memoId) {
        viewModel.MemoId = memoId;
        viewModel.Memo = viewModel.findMemo(memoId);
        viewModel.Action = "details";
        $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId});
    };
    
    $scope.onNewMemo = function(memoBaseId) {
        $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId,"action":"new"});
    }
    
    if(viewModel.MemoBaseId != null) {
        getMemoBase(viewModel.MemoBaseId);
    }

})

var memoBaseCtrl = app.controller("MemoBaseCtrl", function($scope, $http, $location,viewModel,dataContext) {

    $scope.Model = viewModel;
    
    $scope.onNew = function() {
        viewModel.Action = "new";
        viewModel.MemoBase = new MemoBase();
        viewModel.MemoBaseId = viewModel.MemoBase.memoBaseId;
       
        viewModel.Action = "new"; $location.search({"memoBaseId":viewModel.MemoBaseId,"action":"new"});
    };
    
    $scope.onNewMemo = function() {
        viewModel.Action = "new";
        viewModel.Memo = new Memo();
        viewModel.MemoId = viewModel.Memo.memoId;
        $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId,"action":"new"});
    }
        
    $scope.onEdit = function() {
        viewModel.Action = "edit"; $location.search({"memoBaseId":viewModel.MemoBaseId,"action":"edit"});
    };
    
    $scope.onBack = function() {
        viewModel.MemoBase = null;
        viewModel.MemoBaseId = null;
        viewModel.Action = "details";
        
        dataContext.getMemoBases()
        .then(function(memoBases) {
            viewModel.MemoBases = memoBases;
        });
        
        $location.search({});
    };
    
    
    $scope.onSaveEdit = function(memoBase) {
        $http({
            method: 'POST',
            url: 'index.php?controller=memoBase&action=update',
            data: 'model='+JSON.stringify(memoBase),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function(data, status) {
            if(data == 'true') {
    	        viewModel.Action = "details";
                makeToast("Success", "Expression has been saved");
            } else {
                makeToast("Error", "Failed to save data");
            }
        }).error(function() {
            makeToast("Error", "Error occurred");
        });
    };
    
    
    $scope.onSaveNew = function(memoBase) {
        $http({
            method: 'POST',
            url: 'index.php?controller=memoBase&action=insert',
            data: 'model='+JSON.stringify(memoBase),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function(data, status) {
            if(data == 'true') {
    	        viewModel.Action = "details";
                makeToast("Success", "Expression has been saved");
            } else {
                makeToast("Error", "Failed to save data");
                console.log(data);
            }
        }).error(function() {
            makeToast("Error", "Error occurred");
        });
    };
    
    $scope.onDelete = function(memoBaseId) {
         var memoBase = viewModel.findMemoBase(memoBaseId);
        
        $http({
            method: 'POST',
            url: 'index.php?controller=memoBase&action=delete',
            data: 'id='+memoBase.memoBaseId,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function(data, status) {
            if(data == 'true') {
               viewModel.MemoBaseId = null;
               viewModel.MemoBase = null;
               viewModel.Action = "details";
                
                viewModel.MemoBases =
                   deleteFromArray(viewModel.MemoBases, memoBase);
                makeToast("Success", "Library has been deleted");
            } else {
                makeToast("Error", "Failed to delete");
            }
        }).error(function() {
            makeToast("Error", "Error occurred");
        });
    };
    
    $scope.onCancel = function() {
        viewModel.Action = "details";   
    };
    
});


var memoCtrl = app.controller("MemoCtrl", function($scope, $http, $location,viewModel) {

    $scope.Model = viewModel;
    
    $scope.onNew = function() {
        viewModel.Action = "new";
        viewModel.Memo = new Memo();
        viewModel.MemoId = viewModel.Memo.memoId;
        $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId,"action":"new"});
    };
        
    $scope.onEdit = function() {
        viewModel.Action = "edit"; $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId,"action":"edit"});
    };
    
    $scope.onBack = function() {
        viewModel.MemoId = null;
        viewModel.Memo = null;
        viewModel.Action = "details";   
        
        $location.search({"memoBaseId":viewModel.MemoBaseId,"action":"details"});    
    };
    
    $scope.onSaveEdit = function(memo) {
        $http({
            method: 'POST',
            url: 'index.php?controller=memo&action=update',
            data: 'model='+JSON.stringify(memo),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function(data, status) {
            if(data == 'true') {
    	        viewModel.Action = "details";
                makeToast("Success", "Expression has been saved");
            } else {
                makeToast("Error", "Failed to save data");
            }
        }).error(function() {
            makeToast("Error", "Error occurred");
        });
    };
    
   $scope.onSaveNew = function(memo) {
       memo.memoBaseId = viewModel.MemoBaseId;
       
       alert(JSON.stringify(memo));
       
        $http({
            method: 'POST',
            url: 'index.php?controller=memo&action=insert',
            data: 'model='+JSON.stringify(memo),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function(data, status) {
            if(data != 'true') {
                makeToast("Error", "Failed to save expression");
            } else {
                viewModel.Memos.push(memo);
                viewModel.Memo = memo;
                viewModel.MemoId = memo.memoId;
                viewModel.Action = "details";
                makeToast("Success", "New Expression Created");
            }
            
        }).error(function(data) {
           makeToast("Error", "Error occurred"); 
        });

    };
    
    $scope.onDelete = function(memoId) {
         var memo = viewModel.findMemo(memoId);
        
        $http({
            method: 'POST',
            url: 'index.php?controller=memo&action=delete',
            data: 'id='+viewModel.MemoId,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function(data, status) {
            if(data == 'true') {
               viewModel.MemoId = null;
               viewModel.Memo = null;
               viewModel.Action = "details";
                
                viewModel.Memos =
                   deleteFromArray(viewModel.Memos, memo);
                makeToast("Success", "Expression has been deleted");
            } else {
                makeToast("Error", "Failed to delete");
            }
        }).error(function() {
            makeToast("Error", "Error occurred");
        });
    };
    
    $scope.onCancel = function() {
        viewModel.Action = "details";   
    };
    
});
