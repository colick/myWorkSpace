app.service('cartService', function ($http) {

    this.findCartList = function () {
        return $http.post('../cart/findCartList.do');
    };

    this.addGoodsToCartList = function (itemId, num) {
        return $http.post('../cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num);
    };

    this.sum = function (cartList) {
        var totalValue = {totalNum: 0, totalMoney: 0.00};
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.itemList.length; j++) {
                totalValue.totalNum += cart.itemList[j].num;
                totalValue.totalMoney += cart.itemList[j].totalFee;
            }
        }
        return totalValue;
    };

    this.findAddressList = function () {
        return $http.get('../address/findListByUserId.do');
    };

    this.findAddressById = function (id) {
        return $http.get('../address/findById.do?id=' + id);
    };

    this.saveAddress = function (addressEntity) {
        return $http.post('../address/save.do', addressEntity);
    };

    this.deleteAddress = function (id) {
        return $http.post('../address/deleteById.do?id=' + id);
    };

    this.submitOrder = function (order) {
        return $http.post('../order/save.do', order);
    };
});