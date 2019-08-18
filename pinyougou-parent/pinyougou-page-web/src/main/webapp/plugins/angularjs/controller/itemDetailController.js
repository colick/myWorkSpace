//控制器
app.controller('itemDetailController', function ($scope, $controller, $http, $location, $location) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    $scope.addNum = function (x) {
        $scope.num += x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    $scope.specificationItems = {};

    $scope.selectSpecification = function (key, value) {
        $scope.specificationItems[key] = value;
        searchSku();
    };

    $scope.isSelectedSpec = function (key, value) {
        return $scope.specificationItems[key] === value;
    };

    $scope.loadSku = function () {
        var itemId = $location.search()['itemId'];
        if (itemId) {
            for (var i = 0; i < skuList.length; i++) {
                if (skuList[i].id == itemId) {
                    $scope.sku = skuList[i];
                    $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
                    return;
                }
            }
        } else {
            $scope.sku = skuList[0];
            $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
        }
    };

    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku = {
            "id": 0,
            "title": '',
            "price": 0.00
        }
    };

    matchObject = function (spec, specificationItems) {
        for (var key in spec) {
            if (spec[key] != specificationItems[key]) {
                return false;
            }
        }
        for (var key in specificationItems) {
            if (specificationItems[key] != spec[key]) {
                return false;
            }
        }
        return true;
    };

    $scope.addToCartList = function () {
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
            + $scope.sku.id + '&num=' + $scope.num, {'withCredentials': true}).success(function (data) {
                if (data.success) {
                    location.href = 'http://localhost:9107/cart.html';
                } else {
                    alert(data.message);
                }
            }
        );
    };
});