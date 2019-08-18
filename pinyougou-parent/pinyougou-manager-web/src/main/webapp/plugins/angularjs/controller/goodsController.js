//控制器
app.controller('goodsController', function ($scope, $controller, $location, goodsService,
                                            itemCatService, typeTemplateService) {

    //继承baseController
    $controller('baseController', {$scope: $scope});

    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.addImageRow = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
    };

    $scope.delImageRow = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    $scope.findItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (data) {
            $scope.itemCat1List = data;
        });
    };

    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        if (newValue) {
            itemCatService.findByParentId(newValue).success(function (data) {
                $scope.itemCat2List = data;
                $scope.itemCat3List = [];
            });
        }
    });

    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        if (newValue) {
            itemCatService.findByParentId(newValue).success(function (data) {
                $scope.itemCat3List = data;
            });
        }
    });

    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        if (newValue) {
            itemCatService.findById(newValue).success(function (data) {
                $scope.entity.goods.typeTemplateId = data.typeId;
            });
        }
    });

    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        var id = $location.search()['id'];
        if (newValue) {
            typeTemplateService.findById(newValue).success(function (data) {
                $scope.typeTemplate = data;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                if (!id) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }
            });
            //加载规格列表
            typeTemplateService.findSpecListByTypeId(newValue).success(function (data) {
                $scope.specList = data;
            });
        }
    });

    //勾选后生成specAttribute参数
    $scope.updateSpecAttribute = function ($event, name, value) {
        var items = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectKey(items, 'attributeName', name);
        if (obj) {//已存在该attributeName时
            if ($event.target.checked) {//勾选时
                obj['attributeValue'].push(value);
            } else {//取消勾选时删除对应数组值
                obj['attributeValue'].splice(obj['attributeValue'].indexOf(value), 1);
                if (obj['attributeValue'].length === 0) {//若数组已无数据，将key值也删除
                    items.splice(items.indexOf(obj), 1);
                }
            }
        } else {//首次添加该attributeName下的值
            items.push({'attributeName': name, 'attributeValue': [value]});
        }
    };

    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i]['attributeName'], items[i]['attributeValue']);
        }
    };

    addColumn = function (list, columnName, conlumnValues) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < conlumnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[columnName] = conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    };

    //查询参数初始化
    $scope.searchEntity = {};

    //分页请求方法
    $scope.findByPage = function (page, size) {
        goodsService.findByPage(page, size, $scope.searchEntity).success(function (data) {
            $scope.list = data.rows;
            $scope.paginationConf.totalItems = data.total;
        })
    };

    $scope.auditStatus = ['未审核', '已审核', '未通过', '已关闭'];

    $scope.itemCatList = [];
    //加载分类列表
    $scope.findAllItemCatList = function () {
        itemCatService.findAll().success(function (data) {
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    $scope.itemCatList[data[i].id] = data[i].name;
                }
            }
        });
    };

    //修改回显数据
    $scope.findById = function () {
        var id = $location.search()['id'];
        if (id) {
            goodsService.findById(id).success(function (data) {
                $scope.entity = data;
                editor.html($scope.entity.goodsDesc.introduction);
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //转换每个tbItem表集合数据中的spec属性到对象
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            });
        }
    };

    //规格checkbox勾选
    $scope.checkAttributeValue = function (specName, optionValue) {
        var specItems = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectKey(specItems, 'attributeName', specName);
        if (obj) {
            for (var i = 0; i < obj.attributeValue.length; i++) {
                if (optionValue === obj.attributeValue[i]) {
                    return true;
                }
            }
        }
        return false;
    };

    //审核通过/不通过方法
    $scope.updateStatus = function (status) {
        var msg = '';
        if (status === '1') {
            msg = '审核通过';
        } else if (status === '2') {
            msg = '审核不通过';
        } else {
            alert('数据错误');
            return;
        }
        if ($scope.ids == null || $scope.ids.length === 0) {
            alert('未勾选数据');
            return;
        }
        if (confirm('确定要' + msg + '这些数据吗？')) {
            goodsService.updateStatus($scope.ids, status).success(function (data) {
                if (data.success) {
                    $scope.reloadPage();
                    $scope.selectIds=[];
                } else {
                    alert(data.message);
                }
            })
        }
    };

    //删除方法
    $scope.del = function () {
        if (confirm('确定要删除吗？')) {
            goodsService.del($scope.ids).success(function (data) {
                if (data.success) {
                    $scope.reloadPage();
                } else {
                    alert(data.message);
                }
            })
        }
    }
});