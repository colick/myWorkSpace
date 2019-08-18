//控制器
app.controller('userController', function ($scope, $controller, userService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    $scope.register = function () {
        if ($scope.entity.password != $scope.password || $scope.password == '') {
            alert("两次密码输入不一致,请重新输入!");
            $scope.password = '';
            $scope.entity.password = '';
            return;
        }
        userService.register($scope.entity, $scope.verifyCode).success(function (data) {
            alert(data.message);
        });
    };

    $scope.sendVerifyCode = function () {
        if ($scope.entity.phone == null || $scope.entity.phone == '') {
            alert("请输入手机号!");
            return;
        }
        userService.sendVerifyCode($scope.entity.phone).success(function (data) {
            alert(data.message);
        });
    }
});