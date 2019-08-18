app.service('contentService', function ($http) {

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../content/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.get('../content/findById.do?id=' + id);
    };

    this.save = function (entity) {
        return $http.post('../content/save.do', entity);
    };

    this.del = function (ids) {
        return $http.get('../content/del.do?ids=' + ids);
    };

    this.updateStatus = function (ids, status) {
        return $http.post('../content/updateStatus/' + status + '.do?ids=' + ids)
    };
});