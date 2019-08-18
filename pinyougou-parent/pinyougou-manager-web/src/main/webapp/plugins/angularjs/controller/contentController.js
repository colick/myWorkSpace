//控制器
app.controller('contentController', function ($scope, $controller, contentService,
                                              contentCategoryService, uploadService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //查询参数初始化
    $scope.searchEntity = {};

    //分页请求方法
    $scope.findByPage = function (page, size) {
        contentService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    //修改回显数据
    $scope.findById = function (id) {
        contentService.findById(id).success(function (data) {
            $scope.entity = data;
        })
    };

    //新增/修改保存数据
    $scope.save = function () {
        contentService.save($scope.entity).success(function (data) {
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
            contentService.del($scope.ids).success(function (data) {
                if (data.success) {
                    $scope.reloadPage();
                } else {
                    alert(data.message);
                }
            })
        }
    };

    $scope.getContentCategoryList = function () {
        contentCategoryService.findAll().success(function (data) {
            $scope.contentCategoryList = data;
        })
    };

    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (data) {
            if (data.success) {
                $scope.entity.pic = data.message;
            } else {
                alert(data.message);
            }
        });
    };

    $scope.showStatus = ['无效', '有效'];

    //有效/无效方法
    $scope.updateStatus = function (status) {
        var msg = '';
        if (status === '1') {
            msg = '置有效';
        } else if (status === '0') {
            msg = '置无效';
        } else {
            alert('数据错误');
            return;
        }
        if ($scope.ids == null || $scope.ids.length === 0) {
            alert('未勾选数据');
            return;
        }
        if (confirm('确定要' + msg + '这些数据吗？')) {
            contentService.updateStatus($scope.ids, status).success(function (data) {
                if (data.success) {
                    $scope.reloadPage();
                    $scope.selectIds=[];
                } else {
                    alert(data.message);
                }
            });
        }
    };
});