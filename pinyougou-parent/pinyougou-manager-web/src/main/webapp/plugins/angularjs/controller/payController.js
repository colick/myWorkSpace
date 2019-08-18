//控制器
app.controller('payController', function ($scope, $controller, payService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //查询参数初始化
    $scope.searchEntity = {};

    //分页请求方法
    $scope.findByPage = function (page, size) {
        payService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    $scope.payStatus = ['未支付', '已支付'];
    $scope.payType = ['', '微信'];
});