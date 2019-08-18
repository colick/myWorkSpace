//控制器
app.controller('indexController', function ($scope, loginService) {

    //获取登录名
    $scope.getLoginName = function () {
        loginService.getLoginName().success(function (data) {
            $scope.loginName = data.loginName;
        })
    };
});