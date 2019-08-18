app.service('uploadService', function ($http) {

    this.uploadFile = function () {
        var formData = new FormData();
        formData.append('uploadFile', uploadFile.files[0]);//第一upload代表上传时的参数名称，与后台对应，第二个代表页面file文件框的id名称对应
        return $http({
            method: 'POST',
            url: "../upload.do",
            data: formData,
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity
        });
    };
});