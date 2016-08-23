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
		data.bankABARoutingNumber = ($("#bankABARoutingNumber").val()=="")? "-1" : $("#bankABARoutingNumber").val();//等于0的话，会被mybatis的mapper里面的if滤掉，所以换成-1
		data.bankACHNumber = ($("#bankACHNumber").val()=="")?"-1":$("#bankACHNumber").val();
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
	
	/////////////////////////以下添加blur事件来校验输入/////////////////////////////////////
	$(".replace").blur(function(){
		var value = $(this).val();
		if(value==""){
			$("#"+$(this).attr("id")+"-tip").html("This field is required.").hide().fadeIn();//刚好class为replace的3个字段都是必填
		}
		else if(value.indexOf(",") > -1 || value.indexOf(".") > -1 || value.indexOf("-") > -1 || value.indexOf("&") > -1){
			$("#"+$(this).attr("id")+"-tip").html("Cannot contain period, comma, dash or ampersand.").hide().fadeIn();
		}else{
			$("#"+$(this).attr("id")+"-tip").html("").hide();
		}
	});
	
	$("#bankAccountName").blur(function(){
		var Regx = /^[A-Za-z\s]*$/;
		if($("#bankAccountName").val()=="" ){
			$("#bankAccountName-tip").html("This field is required.").hide().fadeIn();
		}
		else if(!Regx.test($("#bankAccountName").val())){
			$("#bankAccountName-tip").html("Can only contain letters and spaces.").hide().fadeIn();
		}else{
			$("#bankAccountName-tip").html("").hide();
		}
	});
	$("#bankCardNumber").blur(function(){
		var Regx = /^[0-9]*$/;
		if($("#bankCardNumber").val()=="" ){
			$("#bankCardNumber-tip").html("This field is required.").hide().fadeIn();
		}
		else if(!Regx.test($("#bankCardNumber").val())){
			$("#bankCardNumber-tip").html("Can only contain numbers and letters.").hide().fadeIn();
		}else{
			$("#bankCardNumber-tip").html("").hide();
		}
	});
	$("#bankName").blur(function(){
		var Regx = /^[A-Za-z0-9\s]*$/;
		if($("#bankName").val()=="" ){
			$("#bankName-tip").html("This field is required.").hide().fadeIn();
		}
		else if(!Regx.test($("#bankName").val())){
			$("#bankName-tip").html("Can only contain numbers, letters and spaces.").hide().fadeIn();
		}else{
			$("#bankName-tip").html("").hide();
		}
	});
	$("#bank_zip_code").blur(function(){
		var Regx = /^[0-9]*$/;
		if($("#bank_zip_code").val()=="" ){
			$("#bank_zip_code-tip").html("This field is required.").hide().fadeIn();
		}
		else if(!Regx.test($("#bank_zip_code").val())){
			$("#bank_zip_code-tip").html("Can only contain numbers.").hide().fadeIn();
		}else{
			$("#bank_zip_code-tip").html("").hide();
		}
	});
	$("#beneficiary_zip_code").blur(function(){
		if($("#beneficiary_zip_code").val()=="" ){
			$("#beneficiary_zip_code-tip").html("This field is required.").hide().fadeIn();
		}
		else if(!Regx.test($("#beneficiary_zip_code").val())){
			$("#beneficiary_zip_code-tip").html("Can only contain numbers.").hide().fadeIn();
		}else{
			$("#beneficiary_zip_code-tip").html("").hide();
		}
	});
	$("#bankSwiftCode").blur(function(){
		var Regx = /^[A-Za-z0-9]{9}$/;
		if($("#bankSwiftCode").val()==""&&$("#bankABARoutingNumber").val()==""){
			$("#bankSwiftCode-tip").html("Swift code or ABA routing number is required.").hide().fadeIn();
		}
		else if($("#bankSwiftCode").val()!=""&&!Regx.test($("#bankSwiftCode").val())){
			$("#bankSwiftCode-tip").html("Can only contain 9 numbers or letters.").hide().fadeIn();
		}else{
			$("#bankSwiftCode-tip").html("").hide();
		}
	});
	$("#bankABARoutingNumber").blur(function(){
		var Regx = /^\d{9}$/;
		if($("#bankSwiftCode").val()==""&&$("#bankABARoutingNumber").val()==""){
			$("#bankABARoutingNumber-tip").html("Swift code or ABA routing number is required.").hide().fadeIn();
		}
		else if($("#bankABARoutingNumber").val()!=""&&!Regx.test($("#bankABARoutingNumber").val())){
			$("#bankABARoutingNumber-tip").html("Must be 9 numbers.").hide().fadeIn();
		}
		else if($("#bankABARoutingNumber").val()=="000000000"){
			$("#bankABARoutingNumber-tip").html("Please fill in your real number.").hide().fadeIn();
		}
		else{
			$("#bankABARoutingNumber-tip").html("").hide();
		}
	});
	$("#bankACHNumber").blur(function(){
		var Regx = /^\d{9}$/;
		if($("#bankACHNumber").val()==""){
			$("#bankACHNumber-tip").html("").hide();//此项不是必填项
		}
		else if(!Regx.test($("#bankACHNumber").val())){
			$("#bankACHNumber-tip").html("Must be 9 numbers.").hide().fadeIn();
		}
		else if($("#bankACHNumber").val()=="000000000"){
			$("#bankACHNumber-tip").html("Please fill in your real number.").hide().fadeIn();
		}
		else{
			$("#bankACHNumber-tip").html("").hide();
		}
	});
	$("#identityNumber").blur(function(){
		var Regx = /^[0-9]*$/;
		if($("#identityNumber").val()==""){
			$("#identityNumber-tip").html("This field is required.").hide().fadeIn();
		}
		else if(!Regx.test($("#identityNumber").val())){
			$("#identityNumber-tip").html("Can only contain numbers.").hide().fadeIn();
		}else{
			$("#identityNumber-tip").html("").hide();
		}
	});
	$("#countryOfIssu").blur(function(){
		if($("#countryOfIssu").val()==""){
			$("#countryOfIssu-tip").html("This field is required.").hide().fadeIn();
		}else{
			$("#countryOfIssu-tip").html("").hide();
		}
	});
	/////////////////////////////////////////////////////////////////////////////////////////////////
	var checkSelect = function(postData){
		var flag = true;
		
		$(".replace").each(function(){
			var value = $(this).val();
			if(value==""){
				$("#"+$(this).attr("id")+"-tip").html("This field is required.").hide().fadeIn();//刚好class为replace的3个字段都是必填
				flag = false;
			}
			else if(value.indexOf(",") > -1 || value.indexOf(".") > -1 || value.indexOf("-") > -1 || value.indexOf("&") > -1){
				$("#"+$(this).attr("id")+"-tip").html("Cannot contain period, comma, dash or ampersand.").hide().fadeIn();
				flag = false;
			}else{
				$("#"+$(this).attr("id")+"-tip").html("").hide();
			}
		});
		
		var Regx = /^[A-Za-z\s]*$/;
		if(postData.beneficiaryAccountName=="" ){
			$("#bankAccountName-tip").html("This field is required.").hide().fadeIn();
			flag = false;
		}
		else if(!Regx.test(postData.beneficiaryAccountName)){
			$("#bankAccountName-tip").html("Can only contain letters and spaces.").hide().fadeIn();
			flag = false;
		}else{
			$("#bankAccountName-tip").html("").hide();
		}
		
		Regx = /^[0-9]*$/;
		if(postData.beneficiaryAccountNumber=="" ){
			$("#bankCardNumber-tip").html("This field is required.").hide().fadeIn();
			flag = false;
		}
		else if(!Regx.test(postData.beneficiaryAccountNumber)){
			$("#bankCardNumber-tip").html("Can only contain numbers and letters.").hide().fadeIn();
			flag = false;
		}else{
			$("#bankCardNumber-tip").html("").hide();
		}
		

		Regx = /^[A-Za-z0-9\s]*$/;
		if(postData.beneficiaryBankName=="" ){
			$("#bankName-tip").html("This field is required.").hide().fadeIn();
			flag = false;
		}
		else if(!Regx.test(postData.beneficiaryBankName)){
			$("#bankName-tip").html("Can only contain numbers, letters and spaces.").hide().fadeIn();
			flag = false;
		}else{
			$("#bankName-tip").html("").hide();
		}
		
		Regx = /^[0-9]*$/;
		if(postData.bankZipCode=="" ){
			$("#bank_zip_code-tip").html("This field is required.").hide().fadeIn();
			flag = false;
		}
		else if(!Regx.test(postData.bankZipCode)){
			$("#bank_zip_code-tip").html("Can only contain numbers.").hide().fadeIn();
			flag = false;
		}else{
			$("#bank_zip_code-tip").html("").hide();
		}
		
		if(postData.beneficiaryZipCode=="" ){
			$("#beneficiary_zip_code-tip").html("This field is required.").hide().fadeIn();
			flag = false;
		}
		else if(!Regx.test(postData.beneficiaryZipCode)){
			$("#beneficiary_zip_code-tip").html("Can only contain numbers.").hide().fadeIn();
			flag = false;
		}else{
			$("#beneficiary_zip_code-tip").html("").hide();
		}
		
		Regx = /^[A-Za-z0-9]{9}$/;
		if(postData.swiftCode==""&&postData.bankABARoutingNumber=="-1"){
			$("#bankSwiftCode-tip").html("Swift code or ABA routing number is required.").hide().fadeIn();
			flag = false;
		}
		else if(postData.swiftCode!=""&&!Regx.test(postData.swiftCode)){
			$("#bankSwiftCode-tip").html("Can only contain 9 numbers or letters.").hide().fadeIn();
			flag = false;
		}else{
			$("#bankSwiftCode-tip").html("").hide();
		}
		
		Regx = /^\d{9}$/;
		if(postData.swiftCode==""&&postData.bankABARoutingNumber=="-1"){
			$("#bankABARoutingNumber-tip").html("Swift code or ABA routing number is required.").hide().fadeIn();
			flag = false;
		}
		else if(postData.bankABARoutingNumber!="-1"&&!Regx.test(postData.bankABARoutingNumber)){
			$("#bankABARoutingNumber-tip").html("Must be 9 numbers.").hide().fadeIn();
			flag = false;
		}
		else if(postData.bankABARoutingNumber=="000000000"){
			$("#bankABARoutingNumber-tip").html("Please fill in your real number.").hide().fadeIn();
			flag = false;
		}
		else{
			$("#bankABARoutingNumber-tip").html("").hide();
		}
		
		if(postData.bankACHNumber=="-1"){
			$("#bankACHNumber-tip").html("").hide();//此项不是必填项
		}
		else if(!Regx.test(postData.bankACHNumber)){
			$("#bankACHNumber-tip").html("Must be 9 numbers.").hide().fadeIn();
			flag = false;
		}
		else if(postData.bankACHNumber=="000000000"){
			$("#bankACHNumber-tip").html("Please fill in your real number.").hide().fadeIn();
			flag = false;
		}
		else{
			$("#bankACHNumber-tip").html("").hide();
		}
		
		Regx = /^[0-9]*$/;
		if(postData.idNumber==""){
			$("#identityNumber-tip").html("This field is required.").hide().fadeIn();
			flag = false;
		}
		else if(!Regx.test(postData.idNumber)){
			$("#identityNumber-tip").html("Can only contain numbers.").hide().fadeIn();
			flag = false;
		}else{
			$("#identityNumber-tip").html("").hide();
		}
		
		if(postData.issuanceCountryId==""){
			$("#countryOfIssu-tip").html("This field is required.").hide().fadeIn();
			flag = false;
		}else{
			$("#countryOfIssu-tip").html("").hide();
		}
		/*文件选择*/
		if(postData.passportURL == ""){
			$("#passport-tip").html(message.NOT_FILE).hide().fadeIn();
			flag = false;
		}else{
			$("#passport-tip").html("").hide();
		}
		/*地址1*/
		var a = postData.beneficiaryCountryId != "0";
		var b = postData.beneficiaryStateId != "0";
		var c = postData.beneficiaryCityId != "0";
		if(a&&b || a&&c){
			$("#beneficiary-address-tip").html("").hide();
		}else{
			$("#beneficiary-address-tip").html("At least Country and State/City is mandatory<br/><br/>").hide().fadeIn();
			flag = false;
		}
		
		/*地址2*/
		var a1 = postData.bankCountryId != "0";
		var b1 = postData.bankStateId != "0";
		var c1 = postData.bankCityId != "0";
		if(a1&&b1 || a1&&c1){
			$("#beneficiarybank-address-tip").html("").hide();
		}else{
			$("#beneficiarybank-address-tip").html("At least Country and State/City is mandatory<br/><br/>").hide().fadeIn();
			flag = false;
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
