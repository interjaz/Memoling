var app = angular.module('app', ['ngRoute', 'app.directive', 'app.service', 'angucomplete'])
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
    
    // Load MemoBases    
    var getMemoBases = function() {
        return dataContext.getMemoBases()
        .then(function(memoBases) {
            viewModel.MemoBases = memoBases;
        });
    };
    
    var getMemoBase = function(memoBaseId) {
        viewModel.MemoBaseId = memoBaseId;
        return dataContext.getMemoBase(memoBaseId)
        .then(function(memoBase) {
            viewModel.MemoBase = memoBase; 
            return dataContext.getMemos(memoBaseId);
        })
        .then(function(memos) {
            viewModel.Memos = memos; 
        });
    };
    
    var getMemo = function(memoId) {
        viewModel.MemoId = memoId;
        viewModel.Memo = viewModel.findMemo(memoId);
        viewModel.Action = "details";
    };
    
    getMemoBases();
    
    $scope.onSelectMemoBase = function(memoBaseId) {
        getMemoBase(memoBaseId);
        $location.search({"memoBaseId":viewModel.MemoBaseId});
        
    };
    
    $scope.onSelectMemo = function(memoId) {
       getMemo(memoId); $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId});
    };
    
    $scope.onNewMemo = function(memoBaseId) {
        $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId,"action":"new"});
    }
    
    if($routeParams.memoBaseId != null) {
        var defer = getMemoBase(viewModel.MemoBaseId);
    
        defer.then(function(result) {
    
            if($routeParams.memoId != null) {
                viewModel.MemoId = $routeParams.memoId;
                getMemo(viewModel.MemoId);
            }

            if($routeParams.action != null) {
                viewModel.Action = $routeParams.action;
            }
            
        });
    
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
            url: 'index.php?controller=MemoBase&action=update',
            data: 'model='+ encodeURIComponent(JSON.stringify(memoBase)),
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
            url: 'index.php?controller=MemoBase&action=insert',
            data: 'model='+encodeURIComponent(JSON.stringify(memoBase)),
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
            url: 'index.php?controller=MemoBase&action=delete',
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
        
    $scope.buildWordListUrl = function(language) {
        return "http://memoling.com/index.php?controller=WordList&action=get&lang=" + language + "&word="
    };
    
    $scope.wordListFromUrl = $scope.buildWordListUrl("EN");
    $scope.wordListToUrl = $scope.buildWordListUrl("EN");
    
    $scope.updateWordListFromUrl = function() {
        $scope.wordListFromUrl = $scope.buildWordListUrl($scope.Model.Memo.wordA.languageIso639);
    };
    
    $scope.updateWordListToUrl = function() {
        $scope.wordListFromUrl = $scope.buildWordListUrl($scope.Model.Memo.wordB.languageIso639);
    };
    
    $scope.onNew = function() {
        viewModel.Action = "new";
        viewModel.Memo = new Memo();
        viewModel.MemoId = viewModel.Memo.memoId;
        $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId,"action":"new"});
    };
        
    $scope.onEdit = function() {
        viewModel.Action = "edit";
        $location.search({"memoBaseId":viewModel.MemoBaseId,"memoId":viewModel.MemoId,"action":"edit"});
    };
    
    $scope.onBack = function() {
        viewModel.MemoId = null;
        viewModel.Memo = null;
        viewModel.Action = "details";   
        
        $location.search({"memoBaseId":viewModel.MemoBaseId,"action":"details"});    
    };
    
    $scope.onSaveEdit = function(memo, wordA, wordB) {
        memo.wordA.word = wordA;
        memo.wordB.word = wordB;
        
        $http({
            method: 'POST',
            url: 'index.php?controller=Memo&action=update',
            data: 'model='+encodeURIComponent(JSON.stringify(memo)),
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
    
   $scope.onSaveNew = function(memo, wordA, wordB) {
       memo.memoBaseId = viewModel.MemoBaseId;
       memo.wordA.word = wordA;
       memo.wordB.word = wordB;
       
        $http({
            method: 'POST',
            url: 'index.php?controller=Memo&action=insert',
            data: 'model='+encodeURIComponent(JSON.stringify(memo)),
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
            url: 'index.php?controller=Memo&action=delete',
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
