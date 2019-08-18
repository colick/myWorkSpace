//控制器
app.controller('contentController', function ($scope, $controller, $location, contentService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    $scope.contentList = [];
    //加载分类列表
    $scope.findByCategoryId = function (id) {
        contentService.findByCategoryId(id).success(function (data) {
            if (data) {
                $scope.contentList[id] = data;
            }
        });
    };

    $scope.search = function () {
        location.href = "http://localhost:9104/search.html#?keywords=" + $scope.keywords;
    }
});