define(["function", "jquery-form", "jquery-bootstrap", "jquery-load", "countdown", "tools"], function () {

    var _timeout = 60 * 1000;

    var _PESupervisor = false, _Practicum2 = false;

    var ajaxErrorfunction = function (reponse, status, info) {
        Portal.loading("close");
        if (reponse.statusText == "timeout") {
            $.alert("error", {
                title: "Server request timed out."
            });
        } else {
            $.alert("error", {
                title: "Server request error!"
            });
        }
    };

    /** 初始化 */
    var init = function (serverTime, scheduleTime, result, practicum2, action, PESupervisor) {

        if (result != "" || action != "") {
            $(".practicumClassroom input").addClass("disabled");
            $(".practicum-tags input").prop("disabled", "disabled");
            $(".practicumClassroom textarea").prop("disabled", "disabled");
        } else {
            setTags();
        }

        /** 教师上课倒计时 */
        $("#countTimeDiv").countdown({
            staticTime: scheduleTime,
            serverTime: serverTime,
            server: true,
            longtime: 60 * 60,
            before: 30 * 60,
            url: webPath + "/findServerTime.json",
            format: true
        });

        _PESupervisor = PESupervisor;
        _Practicum2 = practicum2;
        $(".practicum-table").find("input[type='radio']").click(function () {
            var _score = 0;
            $(".practicum-table").find("input[type='radio']:checked").each(function () {
                _score += parseInt($(this).val());
            });
            $("#score").html(_score);
        });
    };

    var setTags = function () {
        $("div.btn-tags").click(function () {
            if ($(this).hasClass("chn-tags")) {
                $(this).removeClass("chn-tags");
                $(this).find("input").prop("name", "");
            } else {
                if ($("div.chn-tags").size() >= 5) {
                    alert("Tags 5 for most!");
                    return;
                }
                $(this).addClass("chn-tags");
                $(this).find("input").prop("name", "tags");
            }
        });
    };

    var clearTags = function () {
        $("div.btn-tags").removeClass("chn-tags");
    };

    var doSubmit = function (type) {
        if (type !== "SAVE") {
            if (!_PESupervisor && 22 != $(".practicum-table").find("input[type='radio']:checked").size()) {
                alert("You can't submit the feedback form without finish it.");
                return;
            } else if (_PESupervisor && 16 != $(".practicum-table").find("input[type='radio']:checked").size()) {
                alert("You can't submit the feedback form without finish it.");
                return;
            } else {
                $("input[name='totalScore']").val($("#score").html());
            }

            if (0 == $("div.chn-tags").size()) {
                alert("The tags is required!");
                return;
            }

            if (Tools.isEmpty($("textarea[name='things']").val())) {
                alert("The comment box is required!");
                return;
            } else if (length($("textarea[name='things']").val()) > 1000 || length($("textarea[name='things']").val()) < 200) {
                alert("The comment box content length must between 200 and 1000!");
                return;
            }

            if (Tools.isEmpty($("textarea[name='areas']").val())) {
                alert("The comment box is required!");
                return;
            } else if (length($("textarea[name='areas']").val()) > 1000 || length($("textarea[name='areas']").val()) < 200) {
                alert("The comment box content length must between 200 and 1000!");
                return;
            }

            if (0 == $("input[name='level']:checked").size()) {
                alert("The suggested teaching level is required!");
                return;
            }
        } else {
            $("input[name='totalScore']").val($("#score").html());

            if (!Tools.isEmpty($("textarea[name='things']").val())) {
                if (length($("textarea[name='things']").val()) > 1000 || length($("textarea[name='things']").val()) < 200) {
                    alert("The comment box content length must between 200 and 1000!");
                    return;
                }
            }
            if (!Tools.isEmpty($("textarea[name='areas']").val())) {
                if (length($("textarea[name='areas']").val()) > 1000 || length($("textarea[name='areas']").val()) < 200) {
                    alert("The comment box content length must between 200 and 1000!");
                    return;
                }
            }
        }

        $("input[name='submitType']").val(type);
        var resultType = getMockClassResultType($("#score").html());
        doAudit(resultType);
    };

    var length = function (str) {
        return str.replace(/[\u0391-\uFFE5]/g, "aa").length;
    };

    var getMockClassResultType = function (_score) {
        if (_PESupervisor) {
            if (_Practicum2) {
                if (_score >= 30) {
                    return "PASS";
                } else {
                    return "TBD_FAIL";
                }
            } else {
                if (_score >= 32) {
                    return "PASS";
                } else if (_score < 26) {
                    return "TBD_FAIL";
                } else {
                    return "PRACTICUM2";
                }
            }
        } else {
            if (_Practicum2) {
                if (_score >= 38) {
                    return "PASS";
                } else {
                    return "TBD_FAIL";
                }
            } else {
                if (_score >= 40) {
                    return "PASS";
                } else if (_score < 31) {
                    return "TBD_FAIL";
                } else {
                    return "PRACTICUM2";
                }
            }
        }
    };

    /** 向后台声明老师已经进教室 */
    var sendTeacherInClassroom = function (onlineClassId) {
        var url = webPath + "/sendTeacherInClassroom.json";
        $.ajaxRequest({
            url: url,
            data: {
                "onlineClassId": onlineClassId
            },
            dataType: 'json',
            success: function (datas) {
            },
            timeout: _timeout,
            error: function (reponse, status, info) {
                ajaxErrorfunction(reponse, status, info);
            }
        });
    };

    /** 隐藏practicum feedback* */
    var closeFeedBackForm = function () {
        $(".feedBackForm").animate({
            right: '-340px',
            opacity: 'hide'
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
    var showFeedBackForm = function (classtime) {
        /*
         var classTime = getTime(classtime);
         var timestamp = new Date().getTime();
         if(timestamp > classTime){
         */
        $(".feedBackForm").animate({
            right: '1px',
            opacity: 'show'
        }, 100);
        /*
         }else{
         $.alert("error", {
         title : "The Class has yet to start can't leave feedback!"
         });
         }
         */
        var _score = 0;
        $(".practicum-table").find("input[type='radio']:checked").each(function () {
            _score += parseInt($(this).val());
        });
        $("#score").html(_score);
    };

    /**显示finishType*/
    var showFinshType = function () {
        $('#finish-modal').modal('show');
    };

    /** practicum 【Pass/Fail/ReSchedule/Practicum2】 课程操作 **/
    var doAudit = function (type) {
        //获取提交类型
        if (type == "REAPPLY") {
            var finishType = $("#finishType").val();
            if (finishType == "") {
                $.alert("info", {
                    title: "Please select a finish type before clicking reschedule."
                });
                return false;
            }
            $("input[name='submitType']").val("SUBMIT");
        }

        $("#type").val(type);
        submitsAudit();
    };

    var submitsAudit = function () {
        //提交
        $("#practicumFeedBackForm").ajaxSubmit({
            dataType: "json",
            beforeSubmit: function () {
                Portal.loading("open");
            },
            success: function (datas) {
                Portal.loading("close");
                if (datas && datas.result) {
                    if (datas.submitType == "SAVE") {
                        $.alert("info", {
                            title: "Saved successfully!"
                        });
                    } else {
                        $.alert("info", {
                            title: "Submitted successfully!"
                        });

                        $(".practicumClassroom input").addClass("disabled");
                        $(".practicum-tags input").prop("disabled", "disabled");
                        $("div.btn-tags").unbind("click");
                        $(".practicumClassroom textarea").attr("disabled", "disabled");
                        $("input[type=radio]").prop("disabled", "disabled");
                    }

                    $('#finish-modal').modal('hide');
                    closeFeedBackForm();
                } else {
                    $.alert("error", {
                        title: datas.msg
                    });
                }
            },
            timeout: _timeout,
            error: function (reponse, status, info) {
                ajaxErrorfunction(reponse, status, info);
            },
            statusCode: $.statusCode
        });
    };

    /** 退出教室需要判断DemoReoprt或者FeedBack是否填写过,没有填写过的要询问教师是否要推出教室 */
    var confirmExitClassroom = function (onlineClassId, studentId) {
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

    var exitClassroomToClassrooms = function (onlineClassId) {
        var url = webPath + "/exitClassroomPage.json";
        $.ajaxRequest({
            url: url,
            dataType: 'json',
            data: {
                "onlineClassId": onlineClassId
            },
            success: function (data) {
                //Portal.loading("close");
                if (data != null && data.status == 1) {
                    window.location.href = webPath + "/classrooms";
                } else {
                    Portal.loading("close");
                }
            },
            timeout: _timeout,
            error: function (reponse, status, info) {
                if (reponse != null) {
                    if (reponse.status == 401) { //无权限进入登录界面
                        window.location.href = webPath + "/index.shtml";
                        return;
                    }
                }
                ajaxErrorfunction(reponse, status, info);
            }
        });
    };

    var openSessionStorage = function (onlineClassId) {
        if (window.sessionStorage) {
            var applicationComments = window.sessionStorage.getItem("applicationComments" + onlineClassId);
            if (applicationComments) {
                $("#applicationComments").val(applicationComments);
            }
            //清除所有
            window.sessionStorage.removeItem("applicationComments" + onlineClassId);
        }
    };

    return {
        init: init,
        sendTeacherInClassroom: sendTeacherInClassroom,
        confirmExitClassroom: confirmExitClassroom,
        /*feedback*/
        showFinshType: showFinshType,
        doAudit: doAudit,
        showFeedBackForm: showFeedBackForm,
        closeFeedBackForm: closeFeedBackForm,
        openSessionStorage: openSessionStorage,
        clearTags: clearTags,
        doSubmit: doSubmit
    };

});
