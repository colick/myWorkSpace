/*$(function () {
    $(".address").hover(function () {
        console.log(this);
        $(this).addClass("address-hover");
    }, function () {
        $(this).removeClass("address-hover");
    });
});*/

$(function () {
    $(".addr-item .name").click(function () {
        $(this).toggleClass("selected").siblings().removeClass("selected");
    });
    $(".payType li").click(function () {
        $(this).toggleClass("selected").siblings().removeClass("selected");
    });
});
