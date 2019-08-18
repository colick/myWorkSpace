app.service('goodsService', function ($http) {

    this.add = function (entity) {
        return $http.post('../goods/add.do', entity);
    };

    this.update = function (entity) {
        return $http.post('../goods/update.do', entity);
    };

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../goods/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.post('../goods/findByGoodsId.do?goodsId=' + id);
    };

    this.updateMarketable = function (ids, isMarketable) {
        return $http.post('../goods/updateMarketable/' + isMarketable + '.do?ids=' + ids)
    };
});