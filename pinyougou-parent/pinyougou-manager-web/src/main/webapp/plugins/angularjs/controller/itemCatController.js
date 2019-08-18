//控制器
app.controller('itemCatController', function ($scope, $controller, itemCatService, typeTemplateService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //查询参数初始化
    $scope.searchEntity = {parentId: 0};

    $scope.grade = 1;

    $scope.setGrade = function (v) {
        $scope.grade = v;
    };

    $scope.findByParentId = function (p_entity) {
        $scope.searchEntity.parentId = p_entity.id;
        if ($scope.grade == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        } else if ($scope.grade == 2) {
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
        } else {
            $scope.entity_2 = p_entity;
        }
        $scope.reloadPage();
    };

    //记录父id,保存时回填
    $scope.parentId = 0;

    //分页请求方法
    $scope.findByPage = function (page, size) {
        $scope.parentId = $scope.searchEntity.parentId;
        itemCatService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //修改回显数据
    $scope.findById = function (id) {
        itemCatService.findById(id).success(function (data) {
            $scope.entity = data;
        })
    };

    //新增/修改保存数据
    $scope.save = function () {
        if ($scope.entity.id == null) {
            $scope.entity.parentId = $scope.parentId;
        }
        itemCatService.save($scope.entity).success(function (data) {
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
            itemCatService.del($scope.ids).success(function (data) {
                if (data.success) {
                    $scope.reloadPage();
                } else {
                    alert(data.message);
                }
            })
        }
    };

    //商品模板下拉框数据
    $scope.typeTemplateList = {data: []};

    $scope.getTypeTemplateList = function () {
        typeTemplateService.getOptionList().success(function (data) {
            $scope.typeTemplateList = {data: data};
        })
    };
});