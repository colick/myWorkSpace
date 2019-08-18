app.service('userService', function ($http) {

    this.register = function (entity, verifyCode) {
        return $http.post('../user/register.do?verifyCode=' + verifyCode, entity);
    };

    this.sendVerifyCode = function (phone) {
        return $http.post('../user/sendVerifyCode.do?phone=' + phone);
    };
});