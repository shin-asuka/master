var depends = [ "function", "jquery-pagin", "avalon", "moment-timezone","jquery-form", "jquery-load",
		"jquery-bootstrap", "tools" ];
define(depends, function() {

	var _timeout = 60 * 1000;

	var _Practicum2 = false;

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
		}else{
			setTags();
		}

		_Practicum2 = practicum2;
		$(".practicum-table").find("input[type='radio']").click(function(){
			var _score = 0;
			$(".practicum-table").find("input[type='radio']:checked").each(function(){
				_score += parseInt($(this).val());
			});
			$("#score").html(_score);
		});	
	};

	var setTags = function(){
		$("div.btn-tags").click(function(){
			if($(this).hasClass("chn-tags")){
				$(this).removeClass("chn-tags");
				$(this).find("input").prop("name", "");
			}else{
				if($("div.chn-tags").size()>=5){
					alert("Tags 5 for most!");
					return;
				}
				$(this).addClass("chn-tags");
				$(this).find("input").prop("name","tags");
			}
		});
	};

	var clearTags = function(){
		$("div.btn-tags").removeClass("chn-tags");
	};

	var doSubmit = function(type){
		if(16 != $(".practicum-table").find("input[type='radio']:checked").size()){
			alert("You can't submit the feedback form without finish it.");
			return;
		}else{
			$("input[name='totalScore']").val($("#score").html());
		}

		if(0==$("div.chn-tags").size()){
			alert("The tags is required!");
			return;
		}

		if(Tools.isEmpty($("textarea[name='things']").val())){
			alert("The comment box is required!");
			return;
		}else if($("textarea[name='things']").val()>1000 || $("textarea[name='things']").val()<200){
			alert("The comment box content length must between 200 and 1000!");
			return;
		}

		if(Tools.isEmpty($("textarea[name='areas']").val())){
			alert("The comment box is required!");
			return;
		}else if(length($("textarea[name='areas']").val())>1000 || length($("textarea[name='areas']").val())<200){
			alert("The comment box content length must between 200 and 1000!");
			return;
		}

		if(0==$("input[name='level']:checked").size()){
			alert("The suggested teaching level is required!");
			return;
		}

		$("input[name='submitType']").val(type);
		var resultType = getMockClassResultType($("#score").html());
		doAudit(resultType);
	};

	var length = function(str){
		return str.replace(/[^\x00-\xff]/g, "**").length;
	};

	var getMockClassResultType = function(_score){
		if(_Practicum2){
			if(_score >= 30){
				return "PASS";
			}else{
				return "FAIL";
			}
		} else {
			if(_score >= 32){
				return "PASS";
			}else if(_score < 26){
				return "FAIL";
			}else{
				return "PRACTICUM2";
			}
		}
	};

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
		}

		$("#type").val(type);
		submitsAudit();
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
					if(datas.submitType=="SAVE"){
						$.alert("info", {
							title : "Saved successfully!"
						});
					}else{
						$.alert("info", {
							title : "Submitted successfully!"
						});

						$(".practicumClassroom input").addClass("disabled");
						$(".practicumClassroom textarea").attr("disabled","disabled");
						$("input[type=radio]").prop("disabled","disabled");
						$(".practicum-tags input").prop("disabled","disabled");
						$("div.btn-tags").unbind("click");
					}

					$('#finish-modal').modal('hide');
					closeFeedBackForm();
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
		doAudit:doAudit,
		clearTags: clearTags,
		doSubmit: doSubmit
	};

});