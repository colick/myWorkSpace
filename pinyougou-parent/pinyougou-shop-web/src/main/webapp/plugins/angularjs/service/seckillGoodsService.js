app.service('seckillGoodsService', function ($http) {

    this.save = function (entity) {
        return $http.post('../seckillGoods/save.do', entity);
    };

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../seckillGoods/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.post('../seckillGoods/findById.do?id=' + id);
    };
});