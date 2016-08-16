define(["utils"], function(util) {
	var checkutil = {
		notnull:function(id,value,json){
			// value = $.trim(value);
			if(value == "" || value == null){
				util.errorShow(id);
				$("#warnwords-"+id).html(json.notnullMsg).hide().fadeIn();
				$("[name='def-"+id+"']").attr("class","epbgico");
				return false;
			}else{
				$("#warnwords-"+id).html("");
				return true;
			}
		},
		minlength:function(id,value,json){
			// value = $.trim(value);
			if(value.length < json.minlength){
				util.errorShow(id);
				return false;
			}else{
				return true;
			}
		},
		maxlength:function(id,value,json){
			// value = $.trim(value);
			if(value.length > json.maxlength){
				util.errorShow(id);
				return false;
			}else{
				return true;
			}
		},
		length:function(id,value,min,max){
			// value = $.trim(value);
			if( value.length < min || max <= value.length){
				util.errorShow(id);
				return false;
			}else{
				return true;
			}
		},
		pattern:function(id,value,json){
			var pattern = new RegExp(json.pattern);
			if(!pattern.test(value)){
				util.errorShow(id);
				$("#warnwords-"+id).html(json.patternMsg).hide().fadeIn();
				return false;
			}else{
				if(checkutil.isemail(id)){
					if(value.match(new RegExp("@","g")).length != 1){
						util.errorShow(id);
						$("#warnwords-"+id).html(json.patternMsg).hide().fadeIn();
						return false;
					}else if(value.indexOf("..") > 0){
						util.errorShow(id);
						$("#warnwords-"+id).html(json.patternMsg).hide().fadeIn();
						return false;
					}else if(value.indexOf(" ") > 0){
						util.errorShow(id);
						$("#warnwords-"+id).html(json.patternMsg).hide().fadeIn();
						return false;
					}
				}
				$("#warnwords-"+id).html("");
				return true;
			}
		},
		contain:function(id,value,json){
			var pattern = new RegExp(json.contain);
			if(!pattern.test(value)){
				util.errorShow(id);
				return false;
			}else{
				return true;
			}
		},
		equal:function(id,value,json){
			var equalTo = $("#"+json.equalTo).val();
			if(value != equalTo){
				util.errorShow(id);
				$("#warnwords-"+id).html(json.equalToMsg).hide().fadeIn();
				return false;
			}else{
				$("#warnwords-"+id).html("");
				return true;	
			}
		},
		ispassword:function(id){
			if($("#"+id).hasClass("password")){
				return true;
			}
			return false;
		},
		isemail:function(id){
			if($("#"+id).hasClass("email")){
				return true;
			}
			return false;
		}
	};
	
	var core = function(element){
			var id = $(element).attr("id");
			var validator = $(element).attr("validator");
			/**判断是否有验证*/
			var flag = true;
			if(validator){
				var value = $(element).val();
				var json = eval("("+validator+")");	
				/*是否进行空验证*/
				if(json.notnull){
					flag = flag && checkutil.notnull(id,value,json);
				}
				if(!flag){
					return false;
				}
				/*是否进行最小长度验证*/
				if(json.minlength){
					flag = flag && checkutil.minlength(id,value,json);					
				}
				/*是否进行最大长度验证*/
				if(json.maxlength){
					flag = flag &&  checkutil.maxlength(id,value,json);					
				}
				/*是否进行特殊正则表达式验证*/
				if(json.pattern){
					var a = checkutil.pattern(id,value,json);
					if(checkutil.ispassword(id)){
						if(a){
							$("#password1").attr("class","hookbgico");
						}else{
							$("#password1").attr("class","errorbgico");
						}
					}
					flag = flag && a;
				}
				/*值对比，用于再次输入密码*/
				if(json.equalTo){
					flag = flag &&  checkutil.equal(id,value,json);
				}
				/*长度范围*/
				if(json.lengths){
					var min = json.lengths.split("-")[0];
					var max = json.lengths.split("-")[1];
					var b = checkutil.length(id,value,min,max);
					if(checkutil.ispassword(id)){
						if(b){
							$("#password2").attr("class","hookbgico");
						}else{
							$("#password2").attr("class","errorbgico");
						}
					}
					flag = flag && b;
				}
				/*验证必须包含正则表达式*/
				if(json.contain){
					var c = checkutil.contain(id,value,json);
					if(checkutil.ispassword(id)){
						if(c){
							$("#password3").attr("class","hookbgico");
						}else{
							$("#password3").attr("class","errorbgico");
						}
					}
					flag = flag && c;
				}
			}
			if(flag){
				/*验证通过*/
				util.passShow(id);
			}
			return flag;
	};
	
	return {
		core:core
	}
	
});