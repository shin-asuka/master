package com.vipkid.touch.referral.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.portal.personal.model.ReferralTeacherVo;
import com.vipkid.rest.dto.ReferralTeacherDto;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.touch.referral.service.TeacherReferralService;
import com.vipkid.trpm.entity.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/***
 * 老师推荐接口Controller-供h5调用
 */
@Controller
@RequestMapping("/h5/teacher/referral")
public class ReferralController{
    private final Logger logger = LoggerFactory.getLogger(ReferralController.class);
    @Autowired
    private TeacherReferralService teacherReferralService;

    /***
     * 查询推荐成功的老师
     * @param request
     * @param response
     * @param bean
     * @return
     */
    @RequestMapping(value = "/querySucceedReferrals", method = RequestMethod.POST)
    public Map<String, Object> querySucceedReferrals(HttpServletRequest request, HttpServletResponse response, @RequestBody ReferralTeacherVo bean) {
        try{
            Long teacherId = bean.getTeacherId();
            Preconditions.checkArgument(teacherId != null, "teacherId不能为空");

            Map<String, Object> resultMap = Maps.newHashMap();
            Page<ReferralTeacherDto> page = teacherReferralService.findReferralSucceedTeachersPage(bean);
            resultMap.put("page", bean.getPage());//当前页
            resultMap.put("list", page.getList());
            resultMap.put("count", page.getCount());
            resultMap.put("totalPage", page.getTotalPage());
            return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            logger.warn("ReferralController.querySucceedReferrals, 参数不合法，teacherId="+bean.getTeacherId());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ApiResponseUtils.buildErrorResp(2001, e.getMessage(), e);
        } catch (Exception e) {
            logger.warn("ReferralController.querySucceedReferrals, 发生异常，teacherId="+bean.getTeacherId());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(2002, e.getMessage(), e);
        }
    }

    /***
     * 查询推荐成功的老师总数
     * @param request
     * @param response
     * @param bean
     * @return
     */
    @RequestMapping(value = "/querySucceedCount", method = RequestMethod.POST)
    public Map<String, Object> querySucceedCount(HttpServletRequest request, HttpServletResponse response, @RequestBody ReferralTeacherVo bean) {
        try{
            Long teacherId = bean.getTeacherId();
            Preconditions.checkArgument(teacherId != null, "teacherId不能为空");

            Map<String, Object> resultMap = Maps.newHashMap();
            Integer count = teacherReferralService.countReferralSucceedTeachers(bean);
            resultMap.put("count", count);
            return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.warn("ReferralController.querySucceedCount, 参数不合法，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2001, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.warn("ReferralController.querySucceedCount, 发生异常，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2002, e.getMessage(), e);
        }
    }

    /***
     * 查询各个类型的记录条数
     * @param request
     * @param response
     * @param bean
     * @return
     */
    @RequestMapping(value = "/queryCount", method = RequestMethod.POST)
    public Map<String, Object> queryCount(HttpServletRequest request, HttpServletResponse response, @RequestBody ReferralTeacherVo bean) {
        try{
            Long teacherId = bean.getTeacherId();
            Preconditions.checkArgument(teacherId != null, "teacherId不能为空");

            Map<String, Object> resultMap = Maps.newHashMap();
            Integer succeedCount = teacherReferralService.countReferralSucceedTeachers(bean);
            Integer processingCount = teacherReferralService.countReferralProcessingTeachers(bean);
            Integer failedCount = teacherReferralService.countReferralFailedTeachers(bean);

            resultMap.put("succeedCount", succeedCount);
            resultMap.put("processingCount", processingCount);
            resultMap.put("failedCount", failedCount);
            return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.warn("ReferralController.queryCount, 参数不合法，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2001, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.warn("ReferralController.queryCount, 发生异常，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2002, e.getMessage(), e);
        }
    }

    /***
     * 查询推荐进行中的老师
     * @param request
     * @param response
     * @param bean
     * @return
     */
    @RequestMapping(value = "/queryProcessingReferrals", method = RequestMethod.POST)
    public Map<String, Object> queryProcessingReferrals(HttpServletRequest request, HttpServletResponse response, @RequestBody ReferralTeacherVo bean) {
        try{
            Long teacherId = bean.getTeacherId();
            Preconditions.checkArgument(teacherId != null, "teacherId不能为空");

            Map<String, Object> resultMap = Maps.newHashMap();
            Page<ReferralTeacherDto> page = teacherReferralService.findReferralProcessingTeachersPage(bean);
            resultMap.put("page", bean.getPage());//当前页
            resultMap.put("list", page.getList());
            resultMap.put("count", page.getCount());
            resultMap.put("totalPage", page.getTotalPage());
            return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.warn("ReferralController.queryProcessingReferrals, 参数不合法，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2001, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.warn("ReferralController.queryProcessingReferrals, 发生异常，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2002, e.getMessage(), e);
        }
    }

    /***
     * 查询推荐失败的老师
     * @param request
     * @param response
     * @param bean
     * @return
     */
    @RequestMapping(value = "/queryFailedReferrals", method = RequestMethod.POST)
    public Map<String, Object> queryFailedReferrals(HttpServletRequest request, HttpServletResponse response, @RequestBody ReferralTeacherVo bean) {
        try{
            Long teacherId = bean.getTeacherId();
            Preconditions.checkArgument(teacherId != null, "teacherId不能为空");

            Map<String, Object> resultMap = Maps.newHashMap();
            Page<ReferralTeacherDto> page = teacherReferralService.findReferralFailedTeachersPage(bean);
            resultMap.put("page", bean.getPage());//当前页
            resultMap.put("list", page.getList());
            resultMap.put("count", page.getCount());
            resultMap.put("totalPage", page.getTotalPage());
            return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.warn("ReferralController.queryFailedReferrals, 参数不合法，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2001, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.warn("ReferralController.queryFailedReferrals, 发生异常，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2002, e.getMessage(), e);
        }
    }

    /***
     * 查询推荐老师详情
     * @param request
     * @param response
     * @param bean
     * @return
     */
    @RequestMapping(value = "/getReferralDetail", method = RequestMethod.POST)
    public Map<String, Object> getReferralDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody ReferralTeacherVo bean) {
        try{
            Long teacherId = bean.getTeacherId();
            Preconditions.checkArgument(teacherId != null, "teacherId不能为空");

            Map<String, Object> resultMap = Maps.newHashMap();
            ReferralTeacherDto teacher = teacherReferralService.getReferralDetail(bean);
            resultMap.put("detail", teacher);//当前页
            return ApiResponseUtils.buildSuccessDataResp(resultMap);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.warn("ReferralController.getReferralDetail, 参数不合法，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2001, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.warn("ReferralController.getReferralDetail, 发生异常，teacherId="+bean.getTeacherId());
            return ApiResponseUtils.buildErrorResp(2002, e.getMessage(), e);
        }
    }
}