//控制器
app.controller('typeTemplateController', function ($scope, $controller, typeTemplateService,
                                                   brandService, specificationService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //查询参数初始化
    $scope.searchEntity = {};

    //分页请求方法
    $scope.findByPage = function (page, size) {
        typeTemplateService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //修改回显数据
    $scope.findById = function (id) {
        typeTemplateService.findById(id).success(function (data) {
            $scope.entity = data;
            $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
            $scope.entity.specIds = JSON.parse($scope.entity.specIds);
            $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
        })
    };

    //品牌下拉框数据
    $scope.brandList = {data: []};

    $scope.getBrandList = function () {
        brandService.getOptionList().success(function (data) {
            $scope.brandList = {data: data};
        })
    };

    //规格下拉框数据
    $scope.specList = {data: []};

    $scope.getSpecList = function () {
        specificationService.getOptionList().success(function (data) {
            $scope.specList = {data: data};
        })
    };

    //新增行绑定对象
    $scope.addTableRow = function () {
        $scope.entity.customAttributeItems.push({});
    };

    //删除行绑定对象
    $scope.delTableRow = function (index) {
        $scope.entity.customAttributeItems.splice(index, 1);
    };

    //新增/修改保存数据
    $scope.save = function () {
        typeTemplateService.save($scope.entity).success(function (data) {
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
            typeTemplateService.del($scope.ids).success(function (data) {
                if (data.success) {
                    $scope.reloadPage();
                } else {
                    alert(data.message);
                }
            })
        }
    }
});