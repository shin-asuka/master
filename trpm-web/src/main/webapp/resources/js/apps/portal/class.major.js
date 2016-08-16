define(["function","jquery-bootstrap","jquery-load","countdown" ], function() {

	var _timeout = 60 * 1000;

	var ajaxErrorfunction = function(reponse, status, info) {
		Portal.loading("close");
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
	var init = function(serverTime, scheduleTime,createDateTime) {

		initInfoMenu();

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
	var confirmExitClassroom = function(isDemoReport, onlineClassId, studentId) {
		var isShow = false;

		/* 判断feedback是否为空 */
		var url = webPath + "/isEmpty.json";
		var backsuccess = function(datas, isShow) {
			if (datas.empty) {
				isShow = true;
			}
			showExitClassroomTips(isShow, onlineClassId);
		};

		/* 判断demoReport是否为空 */
		if (isDemoReport) {
			url = webPath + "/getLifeCycle.json";
			backsuccess = function(datas, isShow) {
				if (datas.lifeCycle === "UNFINISHED" || !datas.lifeCycle) {
					isShow = true;
				}
				showExitClassroomTips(isShow, onlineClassId);
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
	var showExitClassroomTips = function(isShow, onlineClassId) {
		Portal.loading("close");
		if (isShow) {
			$.alert("confirm", {
				title : "Prompt",
				content : "Please remember to fill in the class feedback!",
				button : "OK",
				callback : function() {
					Portal.loading("open");
					window.location.href = webPath + "/exitClassroom.shtml?onlineClassId="+ onlineClassId;
				}
			});
		} else {
			Portal.loading("open");
			window.location.href = webPath + "/exitClassroom.shtml?onlineClassId=" + onlineClassId;
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
	
	return {
		init : init,
		openInfo:openInfo,
		sendTeacherInClassroom : sendTeacherInClassroom,
		sendHelp : sendHelp,
		confirmExitClassroom : confirmExitClassroom,
		clickHelp:clickHelp,
		clickCancelHelp:clickCancelHelp,
		clickFAQ:clickFAQ
	};

});
