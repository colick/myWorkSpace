app.service('sellerService', function ($http) {

    this.findByPage = function (page, size, searchEntity) {
        return $http.post('../seller/findByPage.do?page=' + page + '&size=' + size, searchEntity);
    };

    this.findById = function (id) {
        return $http.get('../seller/findById.do?id=' + id);
    };

    this.updateStatus = function (ids, status) {
        return $http.get('../seller/updateStatus.do?ids=' + ids + '&status=' + status);
    };
});