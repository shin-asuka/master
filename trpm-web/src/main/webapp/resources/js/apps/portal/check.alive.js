define(["jquery"],function() {
	
	var checkIsAlive = function(classroom, teacherId, microserviceURL) {
		if(!microserviceURL || !classroom || !teacherId){
			return;
		}
		var url = microserviceURL + "/classroom/ping";
		var interval = 20 * 1000;
		var params = {
			"classroom" : classroom,
			"userId" : teacherId
		};
		setInterval(function() {
			$.ajax({
				url : url,
				type : "GET",
				data : params
			});
		}, interval);
	};

	var sendSysInfo = function(onlineClassId, classroom, teacherId,sysInfoURL) {
		if(!onlineClassId || !classroom || !teacherId){
			return;
		}
		var url = sysInfoURL;
		var params = {
			"onlineClassId" : onlineClassId,
			"classroom" : classroom,
			"userId" : teacherId
		};
		$.ajax({
			url : url,
			type : "GET",
			data : params,

			success : function() {
			}
		});
	};
	
	var changeroom = function(roomId,serialNumber,onlineClassId,microserviceURL){
		if(!roomId || !serialNumber || !onlineClassId){
			return;
		}
		$.ajax({
			url : microserviceURL+"/classroom/onlineClassSupplierCode",
			data : {onlineClassId:roomId},
			type : "GET",
			cache : false,
			dataType : "json",
			success : function(data){
				ajaxSuccess(roomId,serialNumber,onlineClassId,data,microserviceURL);
			},
			error : function(){
				setTimeout(function(){changeroom(roomId,serialNumber,onlineClassId,microserviceURL)}, 3000);
			}
		});
	};
	
	var ajaxSuccess = function(roomId,serialNumber,onlineClassId,data,microserviceURL){
		/**refresh curr page*/
		if(data.supplierCode != undefined && data.supplierCode > 0){
			/**practicum课程*/
			if(window.sessionStorage){
				if(window.sessionStorage.getItem(onlineClassId) != data.supplierCode){
					window.sessionStorage.setItem(onlineClassId,data.supplierCode);
					if(serialNumber.indexOf("P") == 0){
						if($("#applicationComments").val() != undefined){
							window.sessionStorage.setItem("applicationComments"+onlineClassId,$("#applicationComments").val());
						}
					}else{
						/**open课程*/
						if(serialNumber.indexOf("OPEN") == 0){
							/**什么都不做*/
						}else{
							/**一般课程*/
							if($("#teacherFeedbackText").val() != undefined){
								window.sessionStorage.setItem("teacherFeedbackText"+onlineClassId,$("#teacherFeedbackText").val());
							}
							if($("textarea[name='tipsForOtherTeachers']").val() != undefined){
								window.sessionStorage.setItem("tipsForOtherTeachers"+onlineClassId,$("textarea[name='tipsForOtherTeachers']").val());
							}
							/**新生体验课程*/
							if(serialNumber.indexOf("T") == 0){
								if($("input[type='radio']:checked").val() != undefined){
									window.sessionStorage.setItem("trialLevelResult"+onlineClassId,$("input[type='radio']:checked").val());
								}
							}
						}
					}
					window.location.reload();
				}
			}
		}
		/**3 seconds **/
		setTimeout(function(){changeroom(roomId,serialNumber,onlineClassId,microserviceURL)}, 3000);
	};
	
	return {
		checkIsAlive : checkIsAlive,
		sendSysInfo:sendSysInfo,
		changeroom:changeroom
	};
});
