/**
 *
 */
package com.vipkid.http.service;

import com.alibaba.fastjson.JSONObject;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.rest.portal.vo.StudentCommentPageVo;
import com.vipkid.rest.portal.vo.StudentCommentTotalVo;
import com.vipkid.rest.portal.vo.StudentCommentVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * @author yangchao
 * @date 2017年1月11日  下午17:36:00
 *
 */

public class ManageGatewayService extends HttpBaseService {

	private static final Logger logger = LoggerFactory.getLogger(ManageGatewayService.class);

	private static final Integer HALF_PAGE_SIZE = 10;//可调整缓存大小

	private static final String  GATEWAY_STUDENT_COMMENT_BATCH_API = "/service/student_comment/comments/by/classes?ids=%s";

	private static final String  GATEWAY_STUDENT_COMMENT_BY_TEACHER_API = "/service/student_comment/teacher/%s/comments?start=%s&limit=%s&rating_level=%s";

	private static final String  GATEWAY_STUDENT_COMMENT_TOTAL_BY_TEACHER_API = "/service/student_comment/teacher/%d/comments/total";

	private static final String  GATEWAY_STUDENT_COMMENT_TRANSLATION_API = "/service/student_comment/comment/%s/translation";


	public List<StudentCommentVo> getStudentCommentListByBatch(String idsStr) {

		List<StudentCommentVo> studentCommentApiList = null;

		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_BATCH_API ,idsStr));
			if (data!=null) {
				studentCommentApiList = JsonUtils.toBeanList(data, StudentCommentVo.class);
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getStudentCommentListByBatch】调用失败，idsStr：{}", e, idsStr);
		}

		return studentCommentApiList;
	}

	public StudentCommentPageVo getStudentCommentListByTeacherId(Integer teacher,Integer start,Integer limit,String ratingLevel){

		StudentCommentPageVo studentCommentPageApi = null;
		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_BY_TEACHER_API,
															teacher!=null ? String.valueOf(teacher) : "",
															start!=null ? String.valueOf(start) : "",
															limit!=null ? String.valueOf(limit) : "",
															ratingLevel!=null ? String.valueOf(ratingLevel) : ""));
			if (data!=null) {
				studentCommentPageApi = JSONObject.parseObject(data, StudentCommentPageVo.class);
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getStudentCommentListByTeacherId】调用失败，teacherId：{},start:{},limit:{},ratingLevel:{},exception:{}", teacher, start, limit, ratingLevel,e);
		}

		return studentCommentPageApi;
	}

	public StudentCommentTotalVo getStudentCommentTotalByTeacherId(Integer teacher){

		StudentCommentTotalVo studentCommentTotalApi = null;

		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_TOTAL_BY_TEACHER_API,teacher));
			if (data!=null) {
				studentCommentTotalApi = JsonUtils.toBean(data, StudentCommentTotalVo.class);
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getStudentCommentTotalByTeacherId】调用失败，teacherId：{},exception:{}", teacher,e);
		}

		return studentCommentTotalApi;
	}

	public Boolean saveTranslation(Long id,String text){
		String ret = null;
		try {
			JSONObject input = new JSONObject();
			input.put("translation",text);
			String data = WebUtils.postJSON(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_TRANSLATION_API, id),input);
			if (data!=null) {
				JSONObject jb = JSONObject.parseObject(data);
				if(jb.get("status").equals("OK")){
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.saveTranslation】调用失败，id：{},text:{},exception:{}", id, text,e);
			throw e;
		}
		return false;
	}

	public String getTranslation(Long id){
		String ret = null;
		try {
			String data = WebUtils.simpleGet(String.format(super.serverAddress + GATEWAY_STUDENT_COMMENT_TRANSLATION_API,id));
			if (data!=null) {
				JSONObject jb = JSONObject.parseObject(data);
				ret = (String)jb.get("translation");
			}
		} catch (Exception e) {
			logger.error("【ManageGatewayService.getTranslation】调用失败，id：{},exception:{}", id,e);
		}
		return ret;
	}
	/**
	 * 计算双向分页
	 * 默认单边的窗口大小为 10
	 * 页数 = 左页数 + 1 + 右页数
	*/

	public Integer[] calculateOffsetAndLimit(StudentCommentPageVo allCommentOfTeacher,Long onlineClassId){
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
