/**
 *
 */
package com.vipkid.http.service;

import com.alibaba.fastjson.JSONObject;
import com.vipkid.http.constant.HttpUrlConstant;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.rest.portal.vo.StudentCommentApi;
import com.vipkid.rest.portal.vo.StudentCommentPageApi;
import com.vipkid.rest.portal.vo.StudentCommentTotalApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author yangchao
 * @date 2017年1月11日  下午17:36:00
 *
 */

public class GatewayAppService extends HttpBaseService {

	private static final Logger logger = LoggerFactory.getLogger(GatewayAppService.class);

	private static final Integer HALF_PAGE_SIZE = 10;//可调整缓存大小

	private static final String  GATEWAY_STUDENT_COMMENT_BATCH_API = "service/student_comment/comments/by/classes?ids=%s";

	private static final String  GATEWAY_STUDENT_COMMENT_BY_TEACHER_API = "service/student_comment/teacher/%s/comments?start=%s&limit=%s&rating_level=%s";

	private static final String  GATEWAY_STUDENT_COMMENT_TOTAL_BY_TEACHER_API = "service/student_comment/teacher/%d/comments/total";

	public List<StudentCommentApi> getStudentCommentListByBatch(String idsStr) {

		List<StudentCommentApi> studentCommentApiList = null;

		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_BATCH_API ,idsStr));
			if (data!=null) {
				studentCommentApiList = JsonUtils.toBeanList(data, StudentCommentApi.class);
			}
		} catch (Exception e) {
			logger.error("获取未提交课程的UA报告失败！",e);
			e.printStackTrace();
		}

		return studentCommentApiList;
	}

	public StudentCommentPageApi getStudentCommentListByTeacherId(Integer teacher,Integer start,Integer limit,String ratingLevel){

		StudentCommentPageApi studentCommentPageApi = null;
		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_BY_TEACHER_API,
															teacher!=null ? String.valueOf(teacher) : "",
															start!=null ? String.valueOf(start) : "",
															limit!=null ? String.valueOf(limit) : "",
															ratingLevel!=null ? String.valueOf(ratingLevel) : ""));
			if (data!=null) {
				studentCommentPageApi = JSONObject.parseObject(data, StudentCommentPageApi.class);
			}
		} catch (Exception e) {
			logger.error("获取未提交课程的UA报告失败！",e);
			e.printStackTrace();
		}

		return studentCommentPageApi;
	}

	public StudentCommentTotalApi getStudentCommentTotalByTeacherId(Integer teacher){

		StudentCommentTotalApi studentCommentTotalApi = null;

		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_TOTAL_BY_TEACHER_API,teacher));
			if (data!=null) {
				studentCommentTotalApi = JsonUtils.toBean(data, StudentCommentTotalApi.class);
			}
		} catch (Exception e) {
			logger.error("获取未提交课程的UA报告失败！",e);
			e.printStackTrace();
		}

		return studentCommentTotalApi;
	}

	/**
	 * 计算双向分页
	 * 默认单边的窗口大小为 10
	 * 页数 = 左页数 + 1 + 右页数
	*/

	public Integer[] calculateOffsetAndLimit(StudentCommentPageApi allCommentOfTeacher,Long onlineClassId){
		Integer position = 0;
		Integer[] offsetAndLimit = new Integer[2];
		for(Integer i=0;i<allCommentOfTeacher.getTotal();i++){
			if(allCommentOfTeacher.getData().get(i).getClass_id().equals(onlineClassId)){
				position = i;
				break;
			}
		}

		if(position <= HALF_PAGE_SIZE){
			offsetAndLimit[0] = 0;
			offsetAndLimit[1] = position + 1 + HALF_PAGE_SIZE;
		} else {
			offsetAndLimit[0] = position - HALF_PAGE_SIZE;
			offsetAndLimit[1] = HALF_PAGE_SIZE + 1 + HALF_PAGE_SIZE;
		}
		return offsetAndLimit;
	}
}
