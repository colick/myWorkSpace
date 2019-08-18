//控制器
app.controller('cartController', function ($scope, $controller, cartService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    $scope.findCartList = function () {
        cartService.findCartList().success(function (data) {
            $scope.cartList = data;
            //计算合计
            $scope.totalValue = cartService.sum($scope.cartList);
        });
    };

    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(function (data) {
            if (data.success) {
                $scope.findCartList();//刷新列表
            } else {
                alert(data.message);//弹出错误提示
            }
        });
    };

    $scope.findAddressList = function () {
        cartService.findAddressList().success(function (data) {
            $scope.addressList = data;
            for (var i = 0; i < $scope.addressList.length; i++) {
                if ($scope.addressList[i].isDefault == '1') {
                    $scope.address = $scope.addressList[i];
                    break;
                }
            }
        });
    };

    $scope.selectAddress = function (address) {
        $scope.address = address;
    };

    $scope.isSelectedAddress = function (address) {
        return address == $scope.address;
    };

    $scope.saveAddress = function () {
        cartService.saveAddress($scope.addressEntity).success(function (data) {
            if (data.success) {
                alert(data.message);
                $scope.findAddressList();//刷新地址列表
            } else {
                alert(data.message);//弹出错误提示
            }
        });
    };

    //修改回显数据
    $scope.findAddressById = function (id) {
        cartService.findAddressById(id).success(function (data) {
            $scope.addressEntity = data;
        })
    };

    $scope.deleteAddress = function (id) {
        if (confirm('确定要删除吗？')) {
            cartService.deleteAddress(id).success(function (data) {
                if (data.success) {
                    alert(data.message);
                    $scope.findAddressList();
                } else {
                    alert(data.message);
                }
            })
        }
    };

    //ng-repeat加载完成后执行js事件
    $scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
        $(".address").hover(function () {
            $(this).addClass("address-hover");
        }, function () {
            $(this).removeClass("address-hover");
        });
    });

    //ng-repeat加载之前执行js事件
    /*$scope.$on('ngRepeatStarted', function (ngRepeatStartedEvent) {
        console.log('44444');
    });*/

    //默认支付方式为微信
    $scope.order = {paymentType: '1'};

    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };

    $scope.submitOrder = function () {
        //填充收件人地址相关
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机
        $scope.order.receiver = $scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(function (data) {
            if (data.success) {
                alert(data.message);
                if ($scope.order.paymentType == '1') {//如果是微信支付，跳转到支付页面
                    location.href = "pay.html";
                } else {//如果货到付款，跳转到提示页面
                    location.href = "paysuccess.html";
                }
            } else {
                alert(data.message);//弹出错误提示
                location.href = "payfail.html";
            }
        });
    };
});