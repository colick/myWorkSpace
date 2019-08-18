//控制器
app.controller('sellerController', function ($scope, $controller, sellerService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    /*//查询参数初始化
    $scope.searchEntity = {};

    //分页请求方法
    $scope.findByPage = function (page, size) {
        brandService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //修改回显数据
    $scope.findById = function (id) {
        brandService.findById(id).success(function (data) {
            $scope.entity = data;
        })
    };*/

    //新增数据
    $scope.add = function () {
        sellerService.add($scope.entity).success(function (data) {
            if (data.success) {
                location.href = "shoplogin.html";
            } else {
                alert(data.message);
            }
        })
    };
});