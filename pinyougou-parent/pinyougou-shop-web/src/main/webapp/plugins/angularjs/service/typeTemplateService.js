app.service('typeTemplateService', function ($http) {

    this.findById = function (id) {
        return $http.get('../typeTemplate/findById.do?id=' + id);
    };

    this.findSpecListByTypeId = function (id) {
        return $http.get('../typeTemplate/findSpecListByTypeId.do?id=' + id);
    };
});