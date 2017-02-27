define(['messenger',"jquery-bootstrap"], function() {

  var param = {};

  var messenger = new Messenger('parent', 'MessengerChannel', 'vipkid_onlineclass');

  /* 初始化学点云消息通道函数 */
  var initChannel = function(uid, roomId, debug, stars) {
    param = {
      'uid': uid,
      'roomId': roomId,
      'name': 'sendStar',
      'value': '1',
      'fun': 's'
    };

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

    jQuery(document).ready(function() {
      messenger.addTarget(document.getElementById('xuedianyun').contentWindow, 'vipkid_onlineclass');
    });

  };

  var JsonToString = function(o) {
    var arr = [];
    var fmt = function(s) {
      if (typeof s == 'object' && s != null)
        return JsonToString(s);
      return /^(string|number)$/.test(typeof s) ? "\"" + s + "\"" : s;
    };

    if (o instanceof Array) {
      for (var i in o) {
        arr.push(fmt(o[i]));
      }
      return '[' + arr.join(',') + ']';

    } else {
      for (var i in o) {
        arr.push("\"" + i + "\":" + fmt(o[i]));
      }
      return '{' + arr.join(',') + '}';
    }
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
      param.name = "sendStar";
      messenger.send(JsonToString(param)); // 用信使来发送数据

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

      param.name = "removeStar";
      messenger.send(JsonToString(param)); // 用信使来发送数据

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
