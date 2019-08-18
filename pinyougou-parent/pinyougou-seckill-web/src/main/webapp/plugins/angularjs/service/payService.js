app.service('payService', function ($http) {

    this.createNative = function () {
        return $http.post('../pay/createNative.do');
    };

    this.queryPayStatus = function (orderPayNo) {
        return $http.get('../pay/queryPayStatus.do?orderPayNo=' + orderPayNo);
    };
});