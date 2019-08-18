//控制器
app.controller('indexController', function ($scope, $controller, loginService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    $scope.getLoginName = function () {
        loginService.getLoginName().success(function (data) {
            $scope.loginName = data.loginName;
        });
    }
});