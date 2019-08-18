app.service('itemCatService', function ($http) {

    this.findByParentId = function (parentId) {
        return $http.post('../itemCat/findByParentId.do?parentId=' + parentId);
    };

    this.findById = function (id) {
        return $http.get('../itemCat/findById.do?id=' + id);
    };

    this.findAll = function () {
        return $http.get('../itemCat/findAll.do');
    };
});