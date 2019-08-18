app.service('brandService', function ($http) {

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../brand/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.get('../brand/findById.do?id=' + id);
    };

    this.save = function (entity) {
        return $http.post('../brand/save.do', entity);
    };

    this.del = function (ids) {
        return $http.get('../brand/del.do?ids=' + ids);
    };

    this.getOptionList = function () {
        return $http.get('../brand/getOptionList.do');
    };
});