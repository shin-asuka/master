var depends = [ "function", "jquery-pagin", "avalon", "moment-timezone","jquery-form", "jquery-load",
		"jquery-bootstrap", "tools" ];
define(depends, function() {

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
	
	/* 视图模型 */
	var _model = null;

	/* 呈现模板 */
	var render = function(id, datas) {
		if (null === _model) {
			_model = avalon.define({
				$id : id,
				dataList : datas.dataList
			});
		} else {
			_model.dataList = datas.dataList;
		}
		/* 模板渲染 */
		avalon.scan();
	};
	
	var feedback = function(action,practicum2){
		$("#addFeedback").click(function(){
			showFeedBackForm();
		});
		$(".closeBtn").click(function(){
			closeFeedBackForm();
		});
		
		if(action != "0"){
			$(".practicumClassroom input").addClass("disabled");
			$(".practicumClassroom textarea").attr("disabled","disabled");
		}
		
		$(".practicum-table").find("input[type='radio']").click(function(){
			var _score = 0;
			$(".practicum-table").find("input[type='radio']:checked").each(function(){
				_score += parseInt($(this).val());
			});	
			
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
			
			$("#score").html(_score);
		});	
	}

	/* 格式化时间毫秒 */
	avalon.filters.formatDate = function(val) {
		var moment = require('moment');
		return moment(val).tz("Asia/Shanghai").format('MMM DD YYYY, hh:mmA');
	};
	
	/* 格式化status显示 */
	var status = ['Unfinished','Pass','Fail','ReSchedule','Practicum2'];
	avalon.filters.formatStatus = function(val) {
		return status[val];
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
			if(16 != $(".practicum-table").find("input[type='radio']:checked").size()){
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
	
	var submitsAudit = function(data){
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
	};

	var classList = function(page) {
		Portal.loading("open");

		var url = webPath + "/pe/classList.json";
		var data = Tools.newMap();
		data.put("curPage", page);

		$.ajaxRequest({
			url : url,
			dataType : "json",
			data : data.toParam(),
			success : function(datas) {
				Portal.loading("close");
				render("pe-list-controller", datas);
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
	
	/** 显示practicum feedback* */
	var showFeedBackForm = function() {
		$(".feedBackForm").animate({
			right : '1px',
			opacity : 'show'
		}, 100);
		
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
	
	return {
		classList : classList,
		feedback:feedback,
		/*feedback*/
		showFinshType:showFinshType,
		doAudit:doAudit
	};

});