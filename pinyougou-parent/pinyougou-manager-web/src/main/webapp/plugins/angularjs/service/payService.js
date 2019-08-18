app.service('payService', function ($http) {

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../pay/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };
});