define([ "function", "tools", "jquery-bootstrap", "personal-basicinfo","select2" ], function() {

	var getState = function(countryLabel, stateLabel) {
		var url = webPath + "/location/getWithParent.shtml";
		var parentId = $(countryLabel).children("select option:selected").val();

		if (parentId) {
			Portal.loading("open");

			$.ajaxRequest({
				url : url,
				data : {
					"parentId" : parentId,
					"title" : "State",
					"level" : 2
				},
				success : function(html) {
					Portal.loading("close");
					$(stateLabel).html(html);
					var cityId = $(stateLabel).attr("onchange");
					cityId = cityId.substring(cityId.lastIndexOf("#"),cityId.lastIndexOf("'"));
					if($.trim(html) != ""){
						$(cityId).html("<option value='0'>City</option>");
					}else{
						$(stateLabel).html("<option value='0'>State</option>");
						getCity(stateLabel,cityId);
					}
				},
				error : function(reponse, status, info) {
					Portal.loading("close");
					if ("timeout" === reponse.status) {
						$.alert("error", {
							title : "Server request timed out！"
						});
					} else {
						$.alert("error", {
							title : "Server request error！"
						});
					}
				}
			});
		}
	};

	var getCity = function(stateLabel, cityLabel) {
		var url = webPath + "/location/getWithParent.shtml";
		var parentId = $(stateLabel).children("select option:selected").val();

		if (parentId) {
			Portal.loading("open");

			$.ajaxRequest({
				url : url,
				data : {
					"parentId" : parentId,
					"title" : "City",
					"level" : 3
				},
				success : function(html) {
					Portal.loading("close");
					if($.trim(html) != ""){
						$(cityLabel).html(html);
					}else{
						$(cityLabel).html("<option value='0'>City</option>");
					}
				},
				error : function(reponse, status, info) {
					Portal.loading("close");

					if ("timeout" === reponse.status) {
						$.alert("error", {
							title : "Server request timed out！"
						});
					} else {
						$.alert("error", {
							title : "Server request error！"
						});
					}
				}
			});
		}
	};

	return {
		getState : getState,
		getCity : getCity
	}

});