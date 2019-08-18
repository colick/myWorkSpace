//控制器
app.controller('specificationController', function ($scope, $controller, specificationService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //查询参数初始化
    $scope.searchEntity = {};

    //分页请求方法
    $scope.findByPage = function (page, size) {
        specificationService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //修改回显数据
    $scope.findById = function (id) {
        specificationService.findById(id).success(function (data) {
            $scope.entity = data;
        })
    };

    //新增行绑定对象
    $scope.addTableRow = function () {
      $scope.entity.specificationOptionList.push({});
    };

    //删除行绑定对象
    $scope.delTableRow = function (index) {
        $scope.entity.specificationOptionList.splice(index, 1);
    };

    //新增/修改保存数据
    $scope.save = function () {
        specificationService.save($scope.entity).success(function (data) {
            if (data.success) {
                $scope.reloadPage();
            } else {
                alert(data.message);
            }
        })
    };

    //删除方法
    $scope.del = function () {
        if (confirm('确定要删除吗？')) {
            specificationService.del($scope.ids).success(function (data) {
                if (data.success) {
                    $scope.reloadPage();
                } else {
                    alert(data.message);
                }
            })
        }
    }
});