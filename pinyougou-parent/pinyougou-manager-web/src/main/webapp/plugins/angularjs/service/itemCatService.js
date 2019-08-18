app.service('itemCatService', function ($http) {

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../itemCat/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.get('../itemCat/findById.do?id=' + id);
    };

    this.save = function (entity) {
        return $http.post('../itemCat/save.do', entity);
    };

    this.del = function (ids) {
        return $http.get('../itemCat/del.do?ids=' + ids);
    };

    this.findByParentId = function (parentId) {
        return $http.post('../itemCat/findByParentId.do?parentId=' + parentId);
    };

    this.findAll = function () {
        return $http.get('../itemCat/findAll.do');
    };
});