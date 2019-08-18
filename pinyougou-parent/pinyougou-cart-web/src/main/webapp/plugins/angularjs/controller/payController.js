//控制器
app.controller('payController', function ($scope, $controller, $location, payService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    $scope.createNative = function () {
        payService.createNative().success(function (data) {
            $scope.orderPayNo = data.out_trade_no;
            $scope.money = (data.total_fee / 100).toFixed(2);
            var qr = new QRious({
                element: document.getElementById('qrious'),
                size: 250,
                level: 'H',
                value: data.code_url
            });
            queryPayStatus(data.out_trade_no);
        });
    };

    queryPayStatus = function (orderPayNo) {
        payService.queryPayStatus(orderPayNo).success(function (data) {
            if (data.success) {
                location.href = "paysuccess.html#?money=" + $scope.money;
            } else {
                if (data.message == '二维码超时') {
                    $scope.createNative();//重新生成二维码
                } else {
                    location.href = "payfail.html";
                }
            }
        });
    };

    $scope.getMoney = function () {
        return $location.search()['money'];
    };
});