//
app.controller('baseController', function ($scope) {
    //分页参数配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadPage();//重新加载
        }
    };

    //封装刷新方法
    $scope.reloadPage = function () {
        $scope.findByPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //获取所勾选的删除条目ids
    $scope.ids = [];

    //checkbox绑定事件
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.ids.push(id);
        } else {
            var index = $scope.ids.indexOf(id);
            $scope.ids.slice(index, 1);
        }
    };

    $scope.jsonToSting = function (jsonString, key) {
        if (jsonString) {
            var jsonObj = JSON.parse(jsonString);
            var res = '';
            for (var i = 0; i < jsonObj.length; i++) {
                if (i > 0) {
                    res += ',';
                }
                res += jsonObj[i][key];
            }
            return res;
        }
    };

    $scope.searchObjectKey = function (list, key, value) {
        if (list && list.length > 0) {
            for (var i = 0; i < list.length; i++) {
                if (list[i][key] == value) {
                    return list[i];
                }
            }
        }
        return null;
    };
});