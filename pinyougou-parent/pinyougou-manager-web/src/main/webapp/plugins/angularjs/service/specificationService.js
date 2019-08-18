app.service('specificationService', function ($http) {

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../specification/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.get('../specification/findById.do?id=' + id);
    };

    this.save = function (entity) {
        return $http.post('../specification/save.do', entity);
    };

    this.del = function (ids) {
        return $http.get('../specification/del.do?ids=' + ids);
    };

    this.getOptionList = function () {
        return $http.get('../specification/getOptionList.do');
    };
});