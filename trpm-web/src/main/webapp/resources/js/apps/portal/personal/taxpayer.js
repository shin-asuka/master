var depends = ["personal","function", "tools", "jquery-form","jquery-bootstrap","jquery-load","select2"];
define(depends, function(personal) {
	
	//设置文件大小限制
	var maxSize = 20*1024*1024; //20M
	
	var filetimeout = 5*60*1000;//5分钟
	
	var ajaxtimeout = 60*1000; //1分钟
	var fileTypes = ".pdf,.jpg,.png,.jpeg";
	var webRootPath = '/personal/taxpayer';
	var deleteUrl = '/personal/taxpayer/delete.shtml';
	var uploadUrl = '/personal/taxpayer/upload.shtml';
	
	var messageTypes = {
			server_error : 'Upload failed.',
			fileType_error : 'Wrong file type (file type must be .PDF/.PNG/.JPG/.JPEG)',
			size_error : 'File size too large (file cannot exceed 20mb)'
	}
	
	var init = function() {
		
		jQuery('input[formType]').attr('accept',fileTypes);
		jQuery('input[formType]').change(function(){ 
			startUpload(this);
		});
		
		jQuery('[name="deleteFile"]').click(function(){
			showUploading(this,0);
			showUploadArea(this,0);
		});
		
	};
	
	var refesh = function(){
		var url = "/personal/taxpayer.shtml";
		var obj = jQuery('[url="'+url+'"]').click();
		loadPage(url,obj);
	}

	var uploadPage = function(){
		var saveUrl = webRootPath + '/uploadPage.shtml';
		var data = {};
	    jQuery.ajax({
			url: saveUrl, async : true,type: "POST",
			data : data,
			success: function(response) {
				info = response;
				if(info!=null){
					jQuery('#taxpayer').parent().html(info);
				}
			},
			error: function(e){
				console.error(e);
			}
		});
	}
	var startFileUpload = function(obj,url,callback){
		var file = obj.files[0];
		var name = file.name;
		var size = file.size;
		var type = name.substr(name.lastIndexOf(".")+1).toLowerCase();
		
		if(fileTypes.indexOf(type) == -1){
			$.alert("error", {
				title : messageTypes.fileType_error
			});
			uploadPage();
			return ;
		}
		
		if(maxSize < size){
			$.alert("error", {
				title : messageTypes.size_error
			});
			uploadPage();
			return ;
		}
		
		showUploading(obj,1);
		$.ajaxFileUpload ({
	        url: url, // 用于文件上传的服务器端请求地址
	        secureuri: false,           // 一般设置为false
	        fileElementId: $(obj).attr("id"),
	        dataType: 'json',// 返回值类型 一般设置为json
	        complete: function () { },// 只要完成即执行，最后执行
	        success: function (data, status)  // 服务器成功响应处理函数
	        {            
	        	if(data !=null){
	        		callback(data);
	        	}
	        },
	        error: function (data, status, e) {// 服务器响应失败处理函数
	            console.info(data,status ,e);
	            $.alert("error", {title : messageTypes.server_error});
	            uploadPage(); 
	        }
		 });
		
	}
	var startUpload = function(obj){
		
		var formType = jQuery(obj).attr('formType');
		var	url = webRootPath+'/upload.shtml';
		if(obj.value!=null || obj.value!=''){
			url +='?formType='+formType;
			startFileUpload(obj,url,function(data){
				if(data != null){
					var url = data.url;
					saveFile(obj,url,formType);
				}
			});
		}
	}
	
	var saveFile = function(obj,url,formType){
		//showUploading(obj,0);
		var saveUrl = webRootPath + '/save.shtml';
		
		var divObj = jQuery(obj).parents('[name="fileInputArea"]')[0];
		var data = {url : url, formType : formType};
	    jQuery.ajax({
			url: saveUrl, async : true,type: "POST",
			data : data,
			success: function(response) {
				info = response;
				if(info!=null){
					jQuery('#taxpayer').parent().html(info);
				}
			},
			error: function(e){
				console.error(e);
				$.alert("error", {
					title : messageTypes.server_error
				});
				uploadPage();
			}
		});
	}
	var showUploading = function(obj,status){
		var divObj = jQuery(obj).parents('[name="fileInputArea"]')[0];
		divObj = jQuery(divObj);
		var fileUploading = divObj.find('[name="fileUploading"]');
		var uploadButton = divObj.find('[name="uploadButton"]');
		if(status == 1){
			fileUploading.show();
			uploadButton.hide();
		}else{
			fileUploading.hide();
			//uploadButton.show();
		}
	}
	
	var showUploadArea = function(obj,status){
		if(obj == null || status == null){
			return ;
		}
		var divObj = jQuery(obj).parents('[name="fileInputArea"]')[0];
		divObj = jQuery(divObj);
		if(status == 1){ //显示文件
			divObj.find('[name="spanDiv"]').show();
			divObj.find('[name="inputDiv"]').hide();
		}else{ //显示上传按钮
			divObj.find('[name="spanDiv"]').hide();
			divObj.find('[name="inputDiv"]').show();
		}
	}
	
	var loadPage = function(url,obj){
		$.ajaxRequest({
			url:url,
			success:function(data){
				if(data != "" && $.trim(data).indexOf("<div") == 0){
					$("#persion-info").html(data);
					initButton();
					//new code
					var index = $(obj).index();
					console.info("index:" + index);
					$(obj).addClass("active").siblings().removeClass("active");
				}else{
					location.href=webPath + "/index.shtml";
				}
			},	
			error : function(reponse, status, info) {
				ajaxErrorfunction(reponse, status, info);
			},
			timeout:_timeout,
			complete:function(){
				Portal.loading("close");
			}
		});
	}
	
	return {
		init : init
	}
});
