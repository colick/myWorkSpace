//控制器
app.controller('seckillGoodsController', function ($scope, $controller, $location,
                                                   $interval, seckillGoodsService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    //查询列表
    $scope.findList = function () {
        seckillGoodsService.findList().success(function (data) {
            $scope.list = data;
        })
    };

    var allSecond = 0;
    //查询列表
    $scope.findById = function () {
        seckillGoodsService.findById($location.search()['id']).success(function (data) {
            $scope.entity = data;
            allSecond = Math.floor((new Date($scope.entity.endTime).getTime() - (new Date().getTime())) / 1000);
            time = $interval(function () {
                if (allSecond > 0) {
                    allSecond = allSecond - 1;
                    $scope.timeString = convertTimeString(allSecond);//转换时间字符串
                } else {
                    $interval.cancel(time);
                    alert("秒杀服务已结束");
                }
            }, 1000);

        });
    };

    convertTimeString = function (allsecond) {
        var days = Math.floor(allsecond / (60 * 60 * 24));//天数
        var hours = Math.floor((allsecond - days * 60 * 60 * 24) / (60 * 60));//小时数
        var minutes = Math.floor((allsecond - days * 60 * 60 * 24 - hours * 60 * 60) / 60);//分钟数
        var seconds = allsecond - days * 60 * 60 * 24 - hours * 60 * 60 - minutes * 60; //秒数
        var timeString = "";
        if (days > 0) {
            timeString = days + "天 ";
        }
        return timeString
            + (hours < 10 ? "0" + hours : hours) + ":"
            + (minutes < 10 ? "0" + minutes : minutes) + ":"
            + (seconds < 10 ? "0" + seconds : seconds);
    };

    $scope.submitOrder = function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(function (data) {
            if (data.success) {
                alert("下单成功，请在1分钟内完成支付");
                location.href = "pay.html";
            } else {
                alert(response.message);
            }
        });
    };
});