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

    this.updateStatus = function (ids, status) {
        return $http.post('../goods/updateStatus/' + status + '.do?ids=' + ids)
    };

    this.del = function (ids) {
        return $http.get('../goods/del.do?ids=' + ids);
    };
});