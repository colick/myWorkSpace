//控制器
app.controller('sellerController', function ($scope, $controller, sellerService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //查询参数初始化
    $scope.searchEntity = {};

    //分页请求方法
    $scope.findByPage = function (page, size) {
        sellerService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //修改回显数据
    $scope.findById = function (id) {
        sellerService.findById(id).success(function (data) {
            $scope.entity = data;
        })
    };

    //修改数据状态数据
    $scope.updateStatus = function (id, status) {
        sellerService.updateStatus(id, status).success(function (data) {
            if (data.success) {
                $scope.reloadPage();
            } else {
                alert(data.message);
            }
        })
    };
});