var depends = ["personal","function", "tools", "jquery-form","jquery-bootstrap","jquery-load","select2"];
define(depends, function(personal) {
	
	var ajaxErrorfunction = function(reponse, status, info) {
		Portal.loading("close");
		if (reponse.statusText == "timeout") {
			$.alert("error", {
				title : "Server request timed out."
			});
		} else {
			$.alert("error", {
				title : "Bank information update failed!"
			});
		}
	};
	
	//设置文件大小限制
	var maxSize = 20*1024*1024;
	
	var filetimeout = 900*1000;//15分钟
	
	var ajaxtimeout = 60*1000; //1分钟
	
	var aliyunroot = "";
	
	var message = {
			
			NOT_SELECT:"Please select your  ",
			
			NOT_FILE:"Please upload your file first!",
			
			FILE_SIZE:"The file size can not exceed 20M!",
			
			UPLOAD_ERROR:"upload error，Please come again ! ",
			
			FILE_FORMAT:"File format only supports:"
		};
	
	var init = function(picroot) {
		
		aliyunroot = picroot;
		
		/*表单回选*/
		$("input[type='radio']").each(function(){
			if($(this).val() == $(this).attr("field")){
				$(this).prop("checked",true);
			}else{
				$(this).prop("checked",false);
			}
		});
		
		/*文件控件监听*/
		filelisten();
		
		//$(".select2").select2();
		
		/*提交表单监听*/
		$("#setBankInfoSubmit").click(function(){
			ajaxSubmits();
		});
		/*		
		 * $(".replace").keydown(function(){
			if (event.keyCode == 55 || event.keyCode == 188 || event.keyCode == 189 || event.keyCode == 190) {
				return false;
			}
		}).blur(function(){
			replaceAll(this);
		});*/
	};
	
	var replaceAll = function(obj){
		var value = $(obj).val();
		if(value.indexOf(",") > -1 || value.indexOf(".") > -1 || value.indexOf("-") > -1 || value.indexOf("&") > -1){
			value = value.replace(/\,/gi," ");
			value = value.replace(/\./gi," ");
			value = value.replace(/\-/gi," ");
			value = value.replace(/\&/gi," and ");
			$(obj).val(value);
		}
	}
	
	var filelisten = function(){
		
		/*触发文件上传*/
		$(".browse").click(function(){
			var submitname  = $(this).attr("id").split("-")[0];
			commonUpload(this, submitname);
		});
		
		/*删除已上传*/
		$(".delete-file").click(function(){
			var submitname  = $(this).attr("id").split("-")[0];
			$("#"+submitname+"-tip").html("").removeAttr("idsuffix");
			$("#"+submitname+"-link").html("").attr("href","");
			$("#"+submitname+",#"+submitname+"-size").val("");
			$("#"+submitname+"-textfield").html("No file attached...");
			$("#"+submitname+"-input").show();
			$("#"+submitname+"-text,#"+submitname+"-uploading").hide();
			var textName = $("#"+submitname+"-link").next();
			if($(textName).attr("class")=="textName"){
				$(textName).remove();
			}
		});
		
		/*文件选择*/
		$(".line_input input[type='file']").change(function(){
			var file = this.files[0];
			var submitname  = $(this).attr("id");
			if(file != undefined){
				var filename = $(this).val();
				var accept = $(this).attr("accept");
				var array = filename.split(".");
				var suffix = array[array.length-1].toLowerCase();
				// 文件大小判断
				if(file.size > this.maxSize){
					$(this).val("");
					$("#"+submitname+"-size").val("");
					$("#"+submitname+"-textfield").html("No file attached...");
					$("#"+submitname+"-tip").html(message.FILE_SIZE).hide().fadeIn();
				// 文件格式判断
				}else if((accept+",").indexOf("."+suffix+",") <= -1){
					$(this).val("");
					$("#"+submitname+"-size").val("");
					$("#"+submitname+"-textfield").html("No file attached...");
					$("#"+submitname+"-tip").html(message.FILE_FORMAT+"["+accept+"]").hide().fadeIn();
				}else{
					$("#"+submitname+"-size").val(file.size);
					var filename = $(this).val();
					filename = filename.substring(filename.lastIndexOf("\\")+1);
					$("#"+submitname+"-textfield").html(filename);
					$("#"+submitname+"-tip").html("");
					//张伯乐修改，选过文件后，自动点击upload
					$("#"+submitname+"-submit").click();
				}
			}else{
				$(this).val("");
				$("#"+submitname+"-size").val("");
				$("#"+submitname+"-textfield").html("No file attached...");
			}
		});
		
		//张伯乐添加，点击attach file 按钮的监听
		$(".attachfile").click(function(){
			var id = $(this).attr("id").split("-")[0];
			$("#"+id).click();
		});
		
		/*上传中ing*/
//		$(".contactUploadStatus").mouseover(function(){
//			$(this).find(".contactUploadStatusWz").show();
//			$(this).find(".contactUploadStatusImg").hide();
//		}).mouseleave(function(){
//			$(this).find(".contactUploadStatusWz").hide();
//			$(this).find(".contactUploadStatusImg").show();
//		});
		
		/*取消正在上传*/
		$(".contactUploadStatusWz").click(function(){
			var id = $(this).parent().attr("id").split("-")[0];
			$("#"+id+",#"+id+"-size").val("");
			$("#"+id+"-textfield").html("No file attached...");
			$("#"+id+"-input").show();
			$("#"+id+"-uploading").hide();
		});
	};

	/*修改银行信息普通上传*/
	var commonUpload = function(obj,submitname){
		var idsuffix = submitname+""+parseInt(1000000*Math.random());
		$("#"+submitname+"-tip").attr("idsuffix",idsuffix);
		
		var data = {type:$(obj).attr("fileType")};// TODO建立
 		$("#"+submitname+"Form").ajaxSubmit({dataType:"json",data:data,
 			beforeSubmit:function(){
 				if($("#"+submitname).val() == ""){
 					//未选择文件上传，提示消息
 					var msg = $("#"+submitname).attr("message");
 					if(msg != undefined){
 						$("#"+submitname+"-tip").html(msg).hide().fadeIn();
 					}else{
 						$("#"+submitname+"-tip").html(message.NOT_SELECT+"file").hide().fadeIn();
 					}
 					return false;
 				}else{
 					//正在上传中
     				$("#"+submitname+"-submit").hide();
	        		$("#"+submitname+"-uploading").show();
     				$("#"+submitname+"-tip").html("");
	        		return true;
 				}
        	}, 
        	uploadProgress: function(event, position, total, percentComplete){
        		if($("#"+submitname+"-tip").attr("idsuffix") == idsuffix){
	        		if(percentComplete == 100){
	        			$("#"+submitname+"-tip").html("");
	        		}else{
	        			$("#"+submitname+"-tip").html(percentComplete+'%');
	        		}
        		}
        	},
        	success : function(result) { 
        		if($("#"+submitname+"-tip").attr("idsuffix") == idsuffix){
	        		if(result.result){
	        			if($("#"+submitname).val() != ""){
		        			//上传成功隐藏上传
			        		$("#"+submitname+"-input").hide();
			        		//显示上传结果
			        		$("#"+submitname+"-text").show();
			        		$("#"+submitname+"-link").html(result.name).attr("href",result.url).hide();
			        		//添加span标签去掉link使用
			        		var textName = $("#"+submitname+"-link").next();
							if($(textName).attr("class")=="textName"){
								$(textName).remove();
							}
			        		$("#"+submitname+"-link").after("<span title='"+result.name+"' class='textName'>"+result.name+"</span>");
	        			}
	        		}else{
	        			//上传失败显示上传，清空上传记录
		        		//$("#"+submitname+"-submit").show();
		        		$("#"+submitname+"-uploading").hide();
						$("#"+submitname+"-tip").html(result.message).hide().fadeIn();
						$("#"+submitname+",#"+submitname+"-size").val("");
						$("#"+submitname+"-textfield").html("No file attached...");
	        		}
        		}
        	},
        	timeout:filetimeout,
        	error : function(request,status,msgException) {
        		if($("#"+submitname+"-tip").attr("idsuffix") == idsuffix){
	        		//上传错误，显示上传和错误消息，不清空上传记录
	        		//$("#"+submitname+"-submit").show();
	        		$("#"+submitname+"-uploading").hide();
	        		$("#"+submitname+"-tip").html(message.UPLOAD_ERROR).hide().fadeIn();
        		}
        	}
         });
	};
	
	/*修改银行信息表单参数*/
	var postDataFun = function(){
		var data = {};
		
		data.beneficiaryAccountName = $("#bankAccountName").val();
		data.beneficiaryAccountNumber = $("#bankCardNumber").val();
		data.beneficiaryBankName = $("#bankName").val();
		
		data.swiftCode = $("#bankSwiftCode").val();
		data.idType = $("input[type='radio']:checked").val();
		var file = $("#passport-link").attr("href").substring($("#passport-link").attr("href").indexOf("file"));
		data.passportURL = file.replace(aliyunroot,"");
		data.idNumber = $("#identityNumber").val();
		data.issuanceCountryId = $("#countryOfIssu").val();
		/* bank address info */
		data.bankCountryId = $("#bank_country_id").val();
		data.bankStateId = $("#bank_state_id").val();
		data.bankCityId = $("#bank_city_id").val();
		data.bankStreetAddress = $("#bank_street").val();
		data.bankZipCode = $("#bank_zip_code").val();
		
		data.beneficiaryCountryId = $("#beneficiary_country_id").val();
		data.beneficiaryStateId = $("#beneficiary_state_id").val();
		data.beneficiaryCityId = $("#beneficiary_city_id").val();
		data.beneficiaryStreetAddress = $("#beneficiary_street").val();
		data.beneficiaryZipCode = $("#beneficiary_zip_code").val();
		
		return data;
	};
	
	/*修改银行信息表单提交*/
	var ajaxSubmits = function(){
		var postData = postDataFun();
		if(checkSelect(postData)){
			Portal.loading("open");
			$.ajaxRequest({
				url:webPath+"/setBankInfoAction.json",
				type:"POST",
				data:postData,
				dataType:"json",
				timeout:ajaxtimeout,
				success:function(datas){
					setBankInfoSucceed(datas);
				},
				error:function(reponse, status, info){
					ajaxErrorfunction(reponse, status, info);
				}
			});
		}
	}
	
	var checkSelect = function(postData){
		var flag = true;
		
		$(".replace").each(function(){
			var value = $(this).val();
			if(value.indexOf(",") > -1 || value.indexOf(".") > -1 || value.indexOf("-") > -1 || value.indexOf("&") > -1){
				$("#"+$(this).attr("id")+"-tip").html("Cannot contain period, comma, dash or ampersand.").hide().fadeIn();
				flag = false;
			}else{
				$("#"+$(this).attr("id")+"-tip").html("").hide();
			}
		});
		
		var Regx = /^[A-Za-z0-9]*$/;
		if(postData.beneficiaryAccountNumber !="" && !Regx.test(postData.beneficiaryAccountNumber)){
			$("#bankCardNumber-tip").html("Can only contain numbers and letters.").hide().fadeIn();
			flag = false;
		}else{
			$("#bankCardNumber-tip").html("").hide();
		}
		/*文件选择*/
		if(postData.passportURL != "" && postData.passportURL.indexOf("file") > 0){
			$("#passport-tip").html(message.NOT_FILE).hide().fadeIn();
			flag = false;
		}else{
			$("#passport-tip").html("").hide();
		}
		/*地址1*/
		var a = postData.beneficiaryCountryId != "0";
		var b = postData.beneficiaryStateId != "0";
		var c = postData.beneficiaryCityId != "0";
		if(a || b || c){
			if(a&&b || a&&c){
				$("#beneficiary-address-tip").html("").hide();
			}else{
				$("#beneficiary-address-tip").html("At least Country and State/City is mandatory<br/><br/>").hide().fadeIn();
				flag = false;
			}
		}
		
		/*地址2*/
		var a1 = postData.bankCountryId != "0";
		var b1 = postData.bankStateId != "0";
		var c1 = postData.bankCityId != "0";
		if(a1 || b1 || c1){
			if(a1&&b1 || a1&&c1){
				$("#beneficiarybank-address-tip").html("").hide();
			}else{
				$("#beneficiarybank-address-tip").html("At least Country and State/City is mandatory<br/><br/>").hide().fadeIn();
				flag = false;
			}
		}
		
		return flag;
	}
	
	/* 修改银行信息成功函数 */
	var setBankInfoSucceed = function(datas) {
		if (datas.action) {
			$.alert("info", {
				title : "Bank information update successful！"
			});
			personal.loadingPage(webPath+"/personal/bankinfo.shtml");
		}else {
			$.alert("error", {
				title : "Bank information update failed！"
			});
			$("#setBankInfoSubmit").removeAttr("disabled");
		}
	};

	return {
		init : init
	}
});
