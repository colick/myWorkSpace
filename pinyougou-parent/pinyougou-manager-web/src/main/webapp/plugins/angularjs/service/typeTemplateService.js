app.service('typeTemplateService', function ($http) {

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../typeTemplate/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.get('../typeTemplate/findById.do?id=' + id);
    };

    this.save = function (entity) {
        return $http.post('../typeTemplate/save.do', entity);
    };

    this.del = function (ids) {
        return $http.get('../typeTemplate/del.do?ids=' + ids);
    };

    this.getOptionList = function () {
        return $http.get('../typeTemplate/getOptionList.do');
    };

    this.findSpecListByTypeId = function (id) {
        return $http.get('../typeTemplate/findSpecListByTypeId.do?id=' + id);
    };
});