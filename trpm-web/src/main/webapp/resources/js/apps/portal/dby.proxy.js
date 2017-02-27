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
    if (!$(curObj).prop('disabled')) {
      $(curObj).removeClass('futureStar').addClass('ownedStar');

      $(curObj).prop('disabled', true);
      // 增加发星星效果
      showStar();
      $(curObj).prop('disabled', false);
      $(curObj).unbind('click');
      $(curObj).click(function() {
        removeStar($(this));
      });

      Duobeiyun.trigger("sendStar", {
        'name': 'sendStar'
      });

      /* 记录发送星星时的日志 */
      var url = webPath + "/sendStarLogs.json";

      $.ajaxRequest({
        data: {
          "onlineClassId": $(curObj).attr("onlineClassId"),
          "studentId": $(curObj).attr("student"),
          "send": true
        },
        url: url,
        dataType: 'json',
        success: function(datas) {
          if (undefined !== console) {
            console.log("send star logs.");
          }
        }
      });
    }
  };

  /* 移除星星函数 */
  var removeStar = function(curObj) {
    if (!$(curObj).prop('disabled')) {
      $(curObj).removeClass('ownedStar').addClass('futureStar');
      $(curObj).prop('disabled', false);
      $(curObj).unbind('click');
      $(curObj).click(function() {
        sendStar($(this));
      });

      Duobeiyun.trigger("removeStar", {
        'name': 'removeStar'
      });

      /* 记录移除星星时的日志 */
      var url = webPath + "/sendStarLogs.json";

      $.ajaxRequest({
        data: {
          "onlineClassId": $(curObj).attr("onlineClassId"),
          "studentId": $(curObj).attr("student"),
          "send": false
        },
        url: url,
        dataType: 'json',
        success: function(datas) {
          if (undefined !== console) {
            console.log("remove star logs.");
          }
        }
      });
    }
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
