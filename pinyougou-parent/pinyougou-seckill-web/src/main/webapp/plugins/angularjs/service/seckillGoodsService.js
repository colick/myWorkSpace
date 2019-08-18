app.service('seckillGoodsService', function ($http) {

    this.findList = function () {
        return $http.get('../seckillGoods/findList.do');
    };

    this.findById = function (id) {
        return $http.get('../seckillGoods/findByIdFromRedis.do?id=' + id);
    };

    this.submitOrder = function (seckillGoodsId) {
        return $http.get('../seckillOrder/submitOrder.do?seckillGoodsId=' + seckillGoodsId);
    };
});