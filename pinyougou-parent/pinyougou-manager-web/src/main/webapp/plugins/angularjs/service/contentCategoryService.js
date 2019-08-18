app.service('contentCategoryService', function ($http) {

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../contentCategory/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.get('../contentCategory/findById.do?id=' + id);
    };

    this.save = function (entity) {
        return $http.post('../contentCategory/save.do', entity);
    };

    this.del = function (ids) {
        return $http.get('../contentCategory/del.do?ids=' + ids);
    };

    this.findAll = function () {
        return $http.get('../contentCategory/findAll.do');
    };
});