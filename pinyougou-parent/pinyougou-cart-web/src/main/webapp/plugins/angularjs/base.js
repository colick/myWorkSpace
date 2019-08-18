var app = angular.module('pinyougou', []);

app.filter('trustHtml', ['$sce', function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);

//注册ng-repeat加载完成事件
app.directive('onFinishRender', ['$timeout', '$parse', function ($timeout, $parse) {
    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            if (scope.$last === true) {
                $timeout(function () {
                    scope.$emit('ngRepeatFinished');
                    if (!!attr.onFinishRender) {
                        $parse(attr.onFinishRender)(scope);
                    }
                });
            }
            if (!!attr.onStartRender) {
                if (scope.$first === true) {
                    $timeout(function () {
                        scope.$emit('ngRepeatStarted');
                        if (!!attr.onStartRender) {
                            $parse(attr.onStartRender)(scope);
                        }
                    });
                }
            }
        }
    }
}]);