define(["duobeiyun","jquery-bootstrap"], function() {

  var initChannel = function(uid, roomId, debug, stars) {
    Duobeiyun.init({
      "uid": uid,
      "roomId": roomId,
      "debug": debug
    });

    /* 发送星星 */
    $("#futureStars > div").click(function() {
      if ('ownedStar' == $(this).prop('class')) {
        removeStar($(this));
      } else {
        sendStar($(this));
      }
    });

    var i = 0;
    $("#futureStars  > div").each(function() {
      if (i < stars) {
        $(this).removeClass('futureStar').addClass('ownedStar');
        i++
      }
    });
  };

  /* 发送星星函数 */
  var sendStar = function(curObj) {
      /* 记录发送星星时的日志 */
      var url = webPath + "/sendStarLogs.json";

      $.ajaxRequest({
        data: {
          "onlineClassId": $(curObj).attr("onlineClassId"),
          "studentId": $(curObj).attr("student"),
          "teacherId" : $(curObj).attr("teacher"),
          "send": true
        },
        url: url,
        dataType: 'json',
        success: function(datas) {
          if (undefined !== console) {
            console.log("send star logs.");
          }
          if(200 == datas.code){
            $.alert("info", {
              title : "Star sent successfully!"
            });
            Duobeiyun.trigger("sendStar", {
              'name': 'sendStar'
            });
            $(curObj).unbind('click');
            $(curObj).click(function() {
              removeStar($(this));
            });
            $(curObj).removeClass('futureStar').addClass('ownedStar');
            // 增加发星星效果
            showStar();
          }else if(602 == datas.code){
            $.alert("error", {
              title :"Maximum stars (5) for this class reached!"
            });
          }else{
            $.alert("error", {
              title :"Unsuccessful, please re-send stars!"
            });
          }
        }
      });
  };

  /* 移除星星函数 */
  var removeStar = function(curObj) {
      /* 记录移除星星时的日志 */
      var url = webPath + "/sendStarLogs.json";

      $.ajaxRequest({
        data: {
          "onlineClassId": $(curObj).attr("onlineClassId"),
          "studentId": $(curObj).attr("student"),
          "teacherId" : $(curObj).attr("teacher"),
          "send": false
        },
        url: url,
        dataType: 'json',
        success: function(datas) {
          if (undefined !== console) {
            console.log("remove star logs.");
          }
          if(200 == datas.code){
            $.alert("info", {
              title :"Star removed successfully!"
            });
            $(curObj).unbind('click');
            $(curObj).click(function() {
              sendStar($(this));
            });
            $(curObj).removeClass('ownedStar').addClass('futureStar');
            Duobeiyun.trigger("removeStar", {
              'name': 'removeStar'
            });
          }else{
            $.alert("error", {
              title :"Unsuccessful, please re-remove stars!"
            });
          }
        }
      });
  };

  /* 发送星星特效函数 */
  var showStar = function() {
    $('#bigStar').stop();
    var h = $(window).height();
    var w = $(window).width();

    var odiv = document.getElementById('futureStars');
    var divLeft = odiv.getBoundingClientRect().left;
    var divTop = odiv.getBoundingClientRect().top;

    $('#bigStar').css({
      width: '50px'
    }).css({
      height: '50px'
    });
    $('#bigStar').css({
      left: w / 2 - 200
    }).css({
      top: h / 2
    });
    $('#bigStar').css({
      opacity: 0
    });
    $('#bigStar').animate({
      width: '250px',
      height: '250px',
      opacity: '1'
    }, 400, 'swing', function() {
      $('#bigStar').animate({
        top: divTop + 'px',
        left: divLeft + 'px',
        width: '20px',
        height: '20px',
        opacity: '0'
      }, 800);
    });

    player.src = 'http://resource.vipkid.com.cn/static/mp3/chord.mp3';
    player.play();
  };

  return {
    initChannel: initChannel
  };

});
