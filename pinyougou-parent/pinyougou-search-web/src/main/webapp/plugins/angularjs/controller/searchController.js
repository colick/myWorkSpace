//控制器
app.controller('searchController', function ($scope, $controller, $location, searchService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //初始化查询参数对象
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 30,
        'sortField': '',
        'sort': ''
    };

    // $scope.keywordsIsBrand = false;

    $scope.resultMap = [];
    //加载分类列表
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        $scope.searchMap.pageSize = parseInt($scope.searchMap.pageSize);
        searchService.search($scope.searchMap).success(function (data) {
            if (data) {
                $scope.resultMap = data;
                loadPageLabel();
            }
        });
    };

    loadPageLabel = function () {
        $scope.pageLabel = [];
        var firstPage = 1;
        var lastPage = $scope.resultMap.totalPages;
        $scope.firstDot = true;//前面有省略号
        $scope.lastDot = true;//后边有省略号
        if (lastPage > 5) {//大于5页的情况
            if ($scope.searchMap.pageNo - 3 <= 0) {
                lastPage = 5;
                $scope.firstDot = false;//前面无省略号
            } else if ($scope.searchMap.pageNo + 2 >= lastPage) {
                firstPage = lastPage - 4;
                $scope.lastDot = false;//后边无省略号
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {//小于等于5页的情况
            $scope.firstDot = false;//前面无省略号
            $scope.lastDot = false;//后边无省略号
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };

    $scope.addSearchItem = function (key, value) {
        if (key === "category" || key === "brand" || key === "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.searchMap.pageNo = 1;
        $scope.search();
    };

    $scope.removeSearchItem = function (key) {
        if (key === "category" || key === "brand" || key === "price") {
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo = 1;
        $scope.search();
    };

    $scope.keywordsHasBrand = function () {
        if ($scope.resultMap.brandList) {
            for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
                if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) != -1) {
                    // $scope.searchMap.brand = $scope.resultMap.brandList[i].text;
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }

    };

    $scope.queryByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    };

    $scope.isActivePage = function (page) {
        return $scope.searchMap.pageNo === page;
    };

    $scope.isBeginPage = function () {
        return $scope.searchMap.pageNo === 1;
    };

    $scope.isEndPage = function () {
        return $scope.searchMap.pageNo === $scope.resultMap.totalPages;
    };

    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    };

    $scope.loadKeywords = function () {
        var keywords = $location.search()['keywords'];
        $scope.searchMap.keywords = keywords;
        $scope.search();
    };
});