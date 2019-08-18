//控制器
app.controller('seckillGoodsController', function ($scope, $controller, $location, seckillGoodsService,
                                                   uploadService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //查询参数初始化
    $scope.searchEntity = {};

    //分页请求方法
    $scope.findByPage = function (page, size) {
        seckillGoodsService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    $scope.auditStatus = ['未审核', '已审核', '未通过', '已关闭'];

    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (data) {
            if (data.success) {
                $scope.entity.smallPic = data.message;
            } else {
                alert(data.message);
            }
        });
    };

    $scope.restFileUpload = function () {
        var file = document.getElementById('uploadFile');
        file.value = ''; //虽然file的value不能设为有字符的值，但是可以设置为空值
        //或者
        // file.outerHTML = file.outerHTML; //重新初始化了file的html
    };

    //新增数据
    $scope.save = function () {
        $scope.entity.introduction = editor.html();
        seckillGoodsService.save($scope.entity).success(function (data) {
            if (data.success) {
                editor.html('');
                $scope.reloadPage();
            } else {
                alert(data.message);
            }
        });
    };

    //修改回显数据
    $scope.findById = function (id) {
        seckillGoodsService.findById(id).success(function (data) {
            $scope.entity = data;
            editor.html($scope.entity.introduction);
        })
    };

    //审核通过/不通过方法
    /*$scope.updateMarketable = function (isMarketable) {
        var msg = '';
        if (isMarketable === '0') {
            msg = '下架';
        } else if (isMarketable === '1') {
            msg = '上架';
        } else {
            alert('数据错误');
            return;
        }
        if ($scope.ids == null || $scope.ids.length === 0) {
            alert('未勾选数据');
            return;
        }
        if (confirm('确定要' + msg + '这些数据吗？')) {
            goodsService.updateMarketable($scope.ids, isMarketable).success(function (data) {
                if (data.success) {
                    $scope.reloadPage();
                    $scope.selectIds = [];
                } else {
                    alert(data.message);
                }
            })
        }
    };*/
});