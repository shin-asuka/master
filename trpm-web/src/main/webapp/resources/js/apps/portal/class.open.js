define(["function", "jquery-bootstrap","jquery-load","countdown" ], function() {

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
	var init = function(serverTime, scheduleTime) {
		
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

	/** 结束公开课函数 */
	var endThisClass = function(onlineClassId) {
		Portal.loading("close");
		$.alert("confirm", {
			title : "Prompt",
			content : "Do you want to end this open-class? ",
			button : " Yes ",
			callback : function() {
				Portal.loading("open");
				window.location.href = webPath + "/endThisClass.shtml?onlineClassId="
						+ onlineClassId;
			}
		});
	};

	return {
		init : init,
		sendTeacherInClassroom:sendTeacherInClassroom,
		endThisClass : endThisClass
	};});
