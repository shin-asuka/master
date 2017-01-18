define(["function","jquery-bootstrap","jquery-load","countdown" ], function() {

	var _timeout = 60 * 1000;

	var ajaxErrorfunction = function(reponse, status, info) {
		Portal.loading("close");
		/*if(reponse!=null){
			if(reponse.status == 401){
				window.location.href = webPath + "/index.shtml";
				return ;
			}
		}*/
		if (reponse.statusText == "timeout") {
			$.alert("error", {
				title : "Server request timed out."
			});
		} else {
			$.alert("error", {
				title : "Server request error!"
			});
		}
	};

	/** 初始化 */
	var init = function(serverTime, scheduleTime,createDateTime, teacherId,scheduledDateTime,serialNumber,oldStatus) {
		initInfoMenu();
		changeClassRoom(scheduledDateTime,serialNumber,oldStatus);
		setTimeout(function() {
			collectForOnlineClassroom(teacherId);
		}, 1000*60*2)

		/** 教师上课倒计时 */
		$("#countTimeDiv").countdown({
			staticTime : scheduleTime,
			serverTime : serverTime,
			server : true,
			longtime : 60 * 60,
			before : 30 * 60,
			url : webPath + "/findServerTime.json",
			format : true
		});

		/** 判断学生是否是新学生，一个月以内的新学生需要创建勋章 */
		if (createDateTime) {
			isNewStudent(serverTime, createDateTime);
		}

	};

	var initInfoMenu = function(level) {
		/** 教室页面顶部信息展示效果 */
		$("div.lessonInfoRow > div.info").hover(function() {
			$(this).addClass("infoColumn3Hover");
			$("div.lessonInfoRow").css({
				"overflow" : "visible"
			});
		}, function() {
			$(this).removeClass("infoColumn3Hover");
			$("div.lessonInfoRow").css({
				"overflow" : "hidden"
			});
		});
	};

	/** 新学生需要判断是否显示勋章，小于一个月的学生会显示勋章 */
	var isNewStudent = function(serverTime, createTime) {
		var count = getTime(serverTime) - getTime(createTime);
		if (count < 30 * 24 * 60 * 60 * 1000) {
			$("#isNewStudent").show();
		} else {
			$("#isNewStudent").hide();
		}
	};

	/** 将日期字符串2012-01-01 11:11:11转化为毫秒 */
	var getTime = function(str) {
		str = str.replace(/-/g, "/");
		var dates = new Date(str);
		var times = dates.getTime();
		return times;
	};

	/** 向后台声明老师已经进教室 */
	var sendTeacherInClassroom = function(onlineClassId) {
		var url = webPath + "/sendTeacherInClassroom.json";
		$.ajaxRequest({
			url : url,
			data : {
				"onlineClassId" : onlineClassId
			},
			dataType : 'json',
			success : function(datas) {
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				ajaxErrorfunction(reponse, status, info);
			}
		});
	};


	/** 退出教室需要判断DemoReoprt或者FeedBack是否填写过,没有填写过的要询问教师是否要推出教室 */
	var confirmExitClassroom = function(isDemoReport, onlineClassId, studentId,isUa) {
		var isShow = false;
		/* 判断feedback是否为空 */
		var url = webPath + "/isEmpty.json";
		var backsuccess = function(datas, isShow) {
			if (datas.empty) {
				isShow = true;
			}
			showExitClassroomTips(isShow, onlineClassId,isUa);
		};

		/* 判断demoReport是否为空 */
		if (isDemoReport) {
			url = webPath + "/getLifeCycle.json";
			backsuccess = function(datas, isShow) {
				if (datas.lifeCycle === "UNFINISHED" || !datas.lifeCycle) {
					isShow = true;
				}
				showExitClassroomTips(isShow, onlineClassId,isUa);
			};
		}

		Portal.loading("open");
		/* 请求后台取值判断处理，是否以前填写过 */
		$.ajaxRequest({
			data : {
				"onlineClassId" : onlineClassId,
				"studentId" : studentId
			},
			url : url,
			dataType : 'json',
			success : function(datas) {
				backsuccess(datas, isShow);
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				ajaxErrorfunction(reponse, status, info);
			}
		});
	};

	/** 退出教室提示信息函数 */
	var showExitClassroomTips = function(isShow, onlineClassId,isUa) {
		Portal.loading("close");
		var str = $("#time").attr("value") + ""; // 日期字符串
		var temp = str.split('.')[0];
		str = temp.replace(/-/g,'/'); // 将-替换成/，因为下面这个构造函数只支持/分隔的日期字符串
		var date = new Date(str); // 构造一个日期型数据，值为传入的字符串
		console.log(date> new Date().getTime() - 2*86400000);
		console.log(date);
		console.log(new Date().getTime() - 2*86400000);
		if (isShow && date.getTime() > new Date().getTime() - 2*86400000) {
			$.alert("confirm", {
				title : "Prompt",
				content : isUa==1?"Please make sure you have saved your form.":"Please remember to fill in the class feedback!",
				button : "OK",
				callback : function() {
					Portal.loading("open");
					//window.location.href = webPath + "/exitClassroom.shtml?onlineClassId="+ onlineClassId;
					exitClassroomToClassrooms(onlineClassId);
				}
			});
		} else {
			Portal.loading("open");
			//window.location.href = webPath + "/exitClassroom.shtml?onlineClassId=" + onlineClassId;
			exitClassroomToClassrooms(onlineClassId);
			/**TODO
			 $.alert("confirm", {
				title : "Prompt",
				content : "Do you want exit this classroom? ",
				button : " Yes ",
				callback : function() {
					Portal.loading("open");
					window.location.href = webPath + "/exitClassroom.shtml?onlineClassId=" + onlineClassId;
				}
			});
			 */
		}
	};

	var exitClassroomToClassrooms = function(onlineClassId){
		var url = webPath + "/exitClassroomPage.json";
		$.ajaxRequest({
			url : url,
			dataType : 'json',
			data : {
				"onlineClassId" : onlineClassId
			},
			success : function(data) {
				//Portal.loading("close");
				if(data!=null && data.status == 1){
					window.location.href = webPath + "/classrooms";
				}else{
					Portal.loading("close");
				}
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				if(reponse!=null ){
					if(reponse.status == 401){ //无权限进入登录界面
						window.location.href = webPath + "/index.shtml";
						return ;
					}
				}
				ajaxErrorfunction(reponse, status, info);
			}
		});
	}

	/** 发送在线帮助 */
	var sendHelp = function(scheduleTime, onlineClassId) {
		var url = webPath + "/sendHelp.json";
		Portal.loading("open");
		$.ajaxRequest({
			url : url,
			dataType : 'json',
			data : {
				"scheduleTime" : scheduleTime,
				"onlineClassId" : onlineClassId
			},
			success : function(datas) {
				Portal.loading("close");
				if (datas.status) {
					$.alert("info", {
						title : "Currently resolving an IT problem.",
					});
				} else {
					var content = "You can only ask for help during the class.";
					if (datas.msg) {
						content = datas.msg;
					}
					$.alert("error", {
						title : content
					});
				}
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				ajaxErrorfunction(reponse, status, info);
			}
		});
	};

	/** 点击contactFireman按钮 */
	var  clickHelp = function(scheduleTime, onlineClassId) {
//		var num = Math.round(30+Math.random()*30);

		var url = webPath + "/sendHelp.json";
		Portal.loading("open");
		$.ajaxRequest({
			url : url,
			dataType : 'json',
			data : {
				"scheduleTime" : scheduleTime,
				"onlineClassId" : onlineClassId
			},
			success : function(datas) {
				Portal.loading("close");
				if (datas.status) {
					//成功不弹窗提示,故注释掉
//						$.alert("info", {
//							title : "Currently resolving an IT problem.",
//						});

					//contactFireman置消失
					var contactFireman=document.getElementById("contactFireman");
					contactFireman.style.display="none";

					//”取消help按钮“和“排队情况提示”可见
					document.getElementById("contactTheFireman").style.display="";
					document.getElementById("cancelHelpBtn").style.display="";
				} else {
					var content = "You can only ask for help during the class.";
					if (datas.msg) {
						content = datas.msg;
					}
					$.alert("error", {
						title : content
					});
				}
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				ajaxErrorfunction(reponse, status, info);
			}
		});

//			//contactFireman置消失
//			var contactFireman=document.getElementById("contactFireman");
//			contactFireman.style.display="none";
//
//			//”取消help按钮“和“排队情况提示”可见
//			document.getElementById("contactTheFireman").style.display="";
//			document.getElementById("cancelHelpBtn").style.display="";
	};

	/** 点击取消help按钮 */
	var  clickCancelHelp = function(scheduleTime, onlineClassId) {
		//helpButton置可用，图片灰色变红色
//		var helpBtn=document.getElementById("helpBtn");
//		helpBtn.disabled=false;
//		helpBtn.style.background = helpBtn.style.background.replace("grey","red");
		//”取消help按钮“和“排队情况提示”消失
		document.getElementById("contactTheFireman").style.display="none";
		document.getElementById("cancelHelpBtn").style.display="none";
		//contactFireman按钮可见
		var contactFireman=document.getElementById("contactFireman");
		contactFireman.style.display="";
	};

	var  clickFAQ = function() {
		window.open(webPath+"/faq.shtml");
	};

	/** public 查看INFO */
	var openInfo = function(studentId,serialNum) {
		var url = webPath + "/openInfo.shtml";
		Portal.loading("open");
		$.ajaxRequest({
			url : url,
			data : {
				"studentId" : studentId,
				"serialNum":serialNum
			},
			success : function(datas) {
				Portal.loading("close");
				if ($("#info-Modal").html()) {
					$("#info-Modal").remove();
				}
				$("body").append(datas);
				$('#info-Modal').modal('show');
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				feedbackError(reponse, status, info);
			}
		});

	};

	/**
	 * 统计网络状态
	 */
	var collectForOnlineClassroom = function(userid) {
	  try {
	    collect(userid, 1)
	  } catch (err) {
	    console.log(err)
	  }

	  //收集用户连接在线教室最优节点的连接时间
	  function collect(userid, role) {
	    //EP列表
	    var ep_list = [];
	    //每个EP连接时长
	    var ep_connecting_times = {};

	    //从中央dispatcher获取每个EP节点
	    var getEpList = function() {
	      $.ajax({
	        url: 'http://virgo.online-class.vipkid.com.cn/ping/DisCover.php',
	        dataType: 'jsonp'
	      })
	      .done(function(res) {
	        if (res.info && res.info.length) {
	          ep_list = res.info;
	          getConnectingTimes();
	        }
	      })
	      .fail(function(error) {})
	    }

	    //获取每个节点的链接时间
	    //最多获取七次 某一个节点某一次连不通 认为是故障节点 不再统计这个节点
	    var getConnectingTimes = function() {
	        /**
	         * range:每次连接的连接时长
	         * average_times:连接的平均时长
	         * isDone 是否完成7次遍历
	         **/
	        $.each(ep_list, function(index, ep) {
	          ep_connecting_times[ep] = {
	            ranges: [],
	            average_times: 0,
	            isDone: false
	          }
	        })

	        $.each(ep_list, function(index, ep) {

	          //连接次数
	          var times = 0;

	          var connect = function() {
	              //最多链接7次
	              if (times == 7) {
	                ep_connecting_times[ep]['isDone'] = true;
	                //计算最短链接时间
	                computeBestNode()
	                return;
	              }
	              var start_time = new Date().getTime();
	              $.ajax({
	                url: "http://" + ep + "/dispatcher/Ping.php",
	                dataType: 'jsonp'
	              })
	              .done(function() {
	                var end_time = new Date().getTime();
	                var range = end_time - start_time;
	                ep_connecting_times[ep]['ranges'].push(range);
	                times += 1;
	                //继续下一次连接
	                connect();
	              })
	              .fail(function() {
	                //删除当前EP节点
	                delete ep_connecting_times[ep];
	                //开始计算最优节点
	                computeBestNode()
	              })
	            }
	            //开始连接
	          connect();
	        })
	      }
	    //计算最短连接时间
	    var computeBestNode = function() {

	      //是否所有的EP节点都遍历完成了
	      var isDoneAll = true;

	      $.each(ep_connecting_times, function(ep, item) {
	        isDoneAll = item.isDone;
	        if (!isDoneAll) {
	          return false;
	        }
	      })

	      if (!isDoneAll) {
	        return;
	      }


	      var min_times = 9999; //默认最短链接时间是9999
	      var best_ep = '';

	      //删除每个节点链接的一个最长时间和最短时间（防止网络毛刺） 然后取平均值
	      $.each(ep_connecting_times, function(ep, item) {
	        var min_time = Math.min.apply(null, item['ranges']);
	        var min_index = $.inArray(min_time, item['ranges']);
	        item['ranges'].splice(min_index, 1);

	        var max_time = Math.max.apply(null, item['ranges']);
	        var max_index = $.inArray(max_time, item['ranges']);
	        item['ranges'].splice(max_index, 1);

	        var total_times = 0;
	        $.each(item['ranges'], function(index, range) {
	          total_times += range;
	        })

	        item['average_times'] = total_times / item['ranges']['length']

	        if (item['average_times'] < min_times) {
	          min_times = item['average_times']
	          best_ep = ep;
	        }
	      })

	      //如果没有取到最优节点 return
	      if (!best_ep) {
	        return;
	      }
	      var time = ep_connecting_times[best_ep]['average_times'];
	      //将最优节点上报给服务器
	      $.ajax({
	        url: 'http://virgo.online-class.vipkid.com.cn/ping/PingResult.php',
	        data: {
	          userid: userid,
	          role: role,
	          ep: best_ep,
	          time: ep_connecting_times[best_ep]['average_times']
	        },
	        dataType: 'jsonp'
	      })

	    }
	    //开始执行
	    getEpList();
	  }
	}

	var changeClassRoom = function(scheduledDateTime,serialNumber,oldStatus,is24Hour) {
		if(!scheduledDateTime || !serialNumber || !oldStatus){
			return;
		}
		if (oldStatus == "FINISHED" && is24Hour){
			var url = webPath + "/changeClassroom.json";
			var interval = 5 * 1000;
			var params = {
				"scheduledDateTime" : scheduledDateTime
			};
			setInterval(function() {
				$.ajax({
					url : url,
					type : "POST",
					data : params,
					success : function(data){
						if(data.data!="No newClassRoom"){
							window.location.href = data.data;
						}
					},
					error : function(){
						setTimeout(function(){changeClassRoom(scheduledDateTime,serialNumber,oldStatus)}, 3000);
					}
				});
			}, interval);
		}
	};

	return {
		init : init,
		openInfo:openInfo,
		sendTeacherInClassroom : sendTeacherInClassroom,
		sendHelp : sendHelp,
		confirmExitClassroom : confirmExitClassroom,
		clickHelp:clickHelp,
		clickCancelHelp:clickCancelHelp,
		clickFAQ:clickFAQ,
		changeClassRoom:changeClassRoom
	};

});
