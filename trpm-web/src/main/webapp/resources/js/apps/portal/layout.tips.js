define(["jquery-bootstrap"], function() {
	
	/* 提示层函数 */
	var tipsLayer = function(showAdminQuiz,showPracticum,showEvaluation) {
		if(showAdminQuiz){
			showAdminQuizFun(showPracticum,showEvaluation);
		}else{
			if(showPracticum){
				showPracticumFun(showEvaluation);
			}else{
				if(showEvaluation){
					showEvaluationFun();
				}
			}
		}
	};
	
	var showAdminQuizFun = function(showPracticum,showEvaluation){		
		$("div#admin-quiz-tips").show();
		$("body").addClass("modal-open");
		
		var left = $("#admin-quiz-tips").find(".light").width()/2 - $("#quiz-tips").offset().left - 70;
		var top = $("#admin-quiz-tips").find(".light").width()/2 - $("#quiz-tips").offset().top-10;
		$("#admin-quiz-tips").find(".light").css({"left":(0-left)+"px","top":(0-top)+"px"});
		
		$(window).resize(function(){
			var left = $("#admin-quiz-tips").find(".light").width()/2 - $("#quiz-tips").offset().left - 70;
			var top = $("#admin-quiz-tips").find(".light").width()/2 - $("#quiz-tips").offset().top-10;
			$("#admin-quiz-tips").find(".light").css({"left":(0-left)+"px","top":(0-top)+"px"});
		});
		
		$("#admin-quiz-tips div.sure-btn,#admin-quiz-tips div.close-btn").click(function() {
			$.ajaxRequest({
				url : webPath + "/disableLayer.json",
				dataType : "json",
				data : {
					"loginType" : 2
				},
				success : function(datas) {
					$("div#admin-quiz-tips").hide();
					$("body").removeClass("modal-open");
					if(showPracticum){
						showPracticumFun(showEvaluation);
					}else{
						if(showEvaluation){
							showEvaluationFun();
						}
					}
					if (undefined !== console) {
						console.log("disable adminquiz layer.");
					}
				}
			});
		});
	};
	
	var showPracticumFun = function(showEvaluation){
		$("div.schedule-guide").show();
		$("body").addClass("modal-open");
		$("div.schedule-guide-img > a").click(function() {
			$.ajaxRequest({
				url : webPath + "/disableLayer.json",
				dataType : "json",
				data : {
					"loginType" : 1
				},
				success : function(datas) {
					$("div.schedule-guide").hide();
					$("body").removeClass("modal-open");
					if(showEvaluation){
						showEvaluationFun();
					}
					if (undefined !== console) {
						console.log("disable schedule layer.");
					}
				}
			});
		});	
	};
	
	var showEvaluationFun = function(){
		$("div#evaluation-tips").show();
		$("body").addClass("modal-open");
		
		var left = $("#evaluation-tips").find(".light").width()/2 - $("#quiz-tips").offset().left - 390;
		var top = $("#evaluation-tips").find(".light").width()/2 - $("#quiz-tips").offset().top-10;
		$("#evaluation-tips").find(".light").css({"left":(0-left)+"px","top":(0-top)+"px"});
		
		$(window).resize(function(){
			var left = $("#evaluation-tips").find(".light").width()/2 - $("#quiz-tips").offset().left - 390;
			var top = $("#evaluation-tips").find(".light").width()/2 - $("#quiz-tips").offset().top-10;
			$("#evaluation-tips").find(".light").css({"left":(0-left)+"px","top":(0-top)+"px"});
		});
		
		$("#evaluation-tips div.sure-btn,#evaluation-tips div.close-btn").click(function() {
			$.ajaxRequest({
				url : webPath + "/disableLayer.json",
				dataType : "json",
				data : {
					"loginType" : 3
				},
				success : function(datas) {
					$("div#evaluation-tips").hide();
					$("body").removeClass("modal-open");
					if (undefined !== console) {
						console.log("disable evaluation layer.");
					}
				}
			});
		});
	};
	
	return {
		tipsLayer : tipsLayer
	};
});