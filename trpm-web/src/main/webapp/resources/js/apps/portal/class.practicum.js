define([ "function", "jquery-form", "jquery-bootstrap", "jquery-load","countdown" ], function() {

	var _timeout = 60 * 1000;
	
	var _PESupervisor = false;

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
	var init = function(serverTime, scheduleTime,result,practicum2,action,PESupervisor) {
		
		if(result != "" || action != ""){
			$(".practicumClassroom input").addClass("disabled");
			$(".practicumClassroom textarea").attr("disabled","disabled");
		}
		
		if(practicum2){ 
			$("#Practicum2").addClass("disabled");
		}
		
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

		_PESupervisor = PESupervisor;
		$(".practicum-table").find("input[type='radio']").click(function(){
			var _score = 0;
			$(".practicum-table").find("input[type='radio']:checked").each(function(){
				_score += parseInt($(this).val());
			});	
			
			if(PESupervisor){
				if(practicum2){
					if(_score >= 30){
						$("#Pass").removeClass("disabled");
						$("#Tbd-Fail").addClass("disabled");
					}else{
						$("#Tbd-Fail").removeClass("disabled");
						$("#Pass").addClass("disabled");
					}
				} else {
					if(_score >= 32){
						$("#Pass").removeClass("disabled");
						$("#Tbd-Fail").addClass("disabled");
						$("#Practicum2").addClass("disabled");
					}else if(_score < 26){
						$("#Tbd-Fail").removeClass("disabled");
						$("#Pass").addClass("disabled");
						$("#Practicum2").addClass("disabled");
					}else{
						$("#Practicum2").removeClass("disabled");
						$("#Tbd-Fail").addClass("disabled");
						$("#Pass").addClass("disabled");
					}
				}
			} else {
				if(practicum2){
					if(_score >= 38){
						$("#Pass").removeClass("disabled");
						$("#Tbd-Fail").addClass("disabled");
					}else{
						$("#Tbd-Fail").removeClass("disabled");
						$("#Pass").addClass("disabled");
					}
				} else {
					if(_score >= 40){
						$("#Pass").removeClass("disabled");
						$("#Tbd-Fail").addClass("disabled");
						$("#Practicum2").addClass("disabled");
					}else if(_score < 31){
						$("#Tbd-Fail").removeClass("disabled");
						$("#Pass").addClass("disabled");
						$("#Practicum2").addClass("disabled");
					}else{
						$("#Practicum2").removeClass("disabled");
						$("#Tbd-Fail").addClass("disabled");
						$("#Pass").addClass("disabled");
					}
				}
			}
			
			$("#score").html(_score);
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

	/** 隐藏practicum feedback* */
	var closeFeedBackForm = function() {
		$(".feedBackForm").animate({
			right : '-340px',
			opacity : 'hide'
		}, 100);
	};
	/*
	var getTime = function(str) {
		str = str.replace(/-/g, "/");
		var dates = new Date(str);
		var times = parseInt(dates.getTime());
		return times;
	};
	*/
	/** 显示practicum feedback* */
	var showFeedBackForm = function(classtime) {
		/*
		var classTime = getTime(classtime);
		var timestamp = new Date().getTime();
		if(timestamp > classTime){
		*/
			$(".feedBackForm").animate({
				right : '1px',
				opacity : 'show'
			}, 100);
		/*
		}else{
			$.alert("error", {
				title : "The Class has yet to start can't leave feedback!"
			});
		}
		*/
		var _score = 0;
		$(".practicum-table").find("input[type='radio']:checked").each(function(){
			_score += parseInt($(this).val());
		});	
		$("#score").html(_score);
	};
	
	/**显示finishType*/
	var showFinshType = function() {
		$('#finish-modal').modal('show');
	};
	
	/** practicum 【Pass/Fail/ReSchedule/Practicum2】 课程操作 **/
	var doAudit = function(type){
		//获取提交类型
		if(type == "REAPPLY"){
			var finishType = $("#finishType").val();
			if(finishType == ""){
				$.alert("info", {
					title : "Please select a finish type before clicking reschedule."
				});
				return false;
			}
		} else {
			// 判断用户是否选择了所有的题目
			if(!_PESupervisor && 22 != $(".practicum-table").find("input[type='radio']:checked").size()){
				alert("You can't submit the feedback form without finish it.");
				return false;
			}else if(_PESupervisor && 16 != $(".practicum-table").find("input[type='radio']:checked").size()){
				alert("You can't submit the feedback form without finish it.");
				return false;
			}
		}
		$.alert("confirm", {
			title : "Prompt",
			content : "Are you sure you want to allow this teacher to [ "+type+" ] ？",
			button : " Yes ",
			callback : function() {
				$("#type").val(type);
				submitsAudit();
			}
		});	
		
	};
	
	var submitsAudit = function(){
		//提交
		$("#practicumFeedBackForm").ajaxSubmit({
			dataType : "json",
			beforeSubmit : function() {
				Portal.loading("open");
			},
			success : function(datas) {
				Portal.loading("close");
				if (datas && datas.result) {
					$.alert("info", {
						title : "Saved successfully!"
					});
					$('#finish-modal').modal('hide');
					closeFeedBackForm();
					$(".practicumClassroom input").addClass("disabled");
					$(".practicumClassroom textarea").attr("disabled","disabled");
					$("input[type=radio]").prop("disabled","disabled");
				} else {
					$.alert("error", {
						title : datas.msg
					});
				}
			},
			timeout : _timeout,
			error : function(reponse, status, info) {
				ajaxErrorfunction(reponse, status, info);
			},
			statusCode : $.statusCode
		});
	}

	/** 退出教室需要判断DemoReoprt或者FeedBack是否填写过,没有填写过的要询问教师是否要推出教室 */
	var confirmExitClassroom = function(onlineClassId, studentId) {
		Portal.loading("open");
		//window.location.href = webPath + "/exitClassroom.shtml?onlineClassId=" + onlineClassId;
		exitClassroomToClassrooms(onlineClassId);
		/** TODO
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
	
	var openSessionStorage = function(onlineClassId){
		if(window.sessionStorage){
			var applicationComments = window.sessionStorage.getItem("applicationComments"+onlineClassId);
			if(applicationComments){
				$("#applicationComments").val(applicationComments);
			}
			//清除所有
			window.sessionStorage.removeItem("applicationComments"+onlineClassId);
		}
	};

	return {
		init : init,
		sendTeacherInClassroom : sendTeacherInClassroom,
		confirmExitClassroom : confirmExitClassroom,
		/*feedback*/
		showFinshType:showFinshType,
		doAudit:doAudit,
		showFeedBackForm : showFeedBackForm,
		closeFeedBackForm : closeFeedBackForm,
		openSessionStorage:openSessionStorage
	};

});
