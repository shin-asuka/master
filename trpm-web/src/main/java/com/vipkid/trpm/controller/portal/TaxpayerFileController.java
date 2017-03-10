package com.vipkid.trpm.controller.portal;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import com.google.common.base.Preconditions;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.FormType;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.file.utils.ActionHelp;
import com.vipkid.file.utils.Encodes;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.rest.exception.ServiceException;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.entity.personal.TaxpayerView;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import com.vipkid.trpm.util.AwsFileUtils;

/**
 * @author zouqinghua
 * @date 2016年10月17日  上午1:48:01
 *
 */
@Controller
@RequestMapping("/personal/taxpayer")
public class TaxpayerFileController extends AbstractPortalController{

	private Logger logger = LoggerFactory.getLogger(TaxpayerFileController.class);
	
	@Autowired
	private AwsFileService awsFileService;
	
/*	@Autowired
	private IndexService indexService;*/
	
	@Autowired
	private TeacherTaxpayerFormService teacherTaxpayerFormService;
	
	@Autowired
    private LoginService loginService;
	
	/**
	 * 老师的Taxpayer页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("")
	public String taxpayer(HttpServletRequest request, HttpServletResponse response, Model model) {
		Teacher teacher = loginService.getTeacher();
		Long teacherId = teacher.getId();
		
		logger.info("进入 Taxpayer 页面  teacherId = {} , realName = {}",teacherId, teacher.getRealName());
		TaxpayerView taxpayerView = teacherTaxpayerFormService.getTeacherTaxpayerView(teacherId);
		model.addAttribute("taxpayerView", taxpayerView);
		return view("personal/personal_taxpayer");
	}
	
	@RequestMapping("uploadPage")
	public String uploadPage(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("进入 Taxpayer 页面");
		return view("personal/personal_taxpayer");
	}
	
	//@ResponseBody
	@RequestMapping("/upload")
	public void taxpayerUpload(Integer formType,Long id, @RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response, Model model){
		
		Teacher teacher = loginService.getTeacher();
		Long teacherId = teacher.getId();
		String teacherName = teacher.getRealName();
		
		logger.info("upload taxpayer  teacherId = {}, teacherName = {}, formType = {},file = {}",teacherId,teacherName,TeacherEnum.getFormTypeById(formType),file);
		FileVo fileVo = null;
		if(file!=null){
			
			String name = file.getOriginalFilename();
			String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
			String fileName = AwsFileUtils.reNewFileName(name); //处理文件名
			FormType formTypeEnum = TeacherEnum.getFormTypeById(formType);
			String formTypeName = formTypeEnum.name();
			String awsName = teacherId+"-"+fileName;
			if(!name.equals(fileName)){
				awsName = teacherId+"-"+formTypeName+"-"+fileName;
			}
			String key = AwsFileUtils.getTaxpayerkey(teacherId,awsName);
			Long size = file.getSize();
			
			Preconditions.checkArgument(AwsFileUtils.checkTaxPayerFileType(name), "文件类型不正确，支持类型为"+AwsFileUtils.TAPXPAYER_FILE_TYPE);
			Preconditions.checkArgument(AwsFileUtils.checkTaxPayerFileSize(size), "文件太大，maxSize = "+AwsFileUtils.TAPXPAYER_FILE_MAX_SIZE);
			
			try {
				//Thread.sleep(1000*50);
				fileVo = awsFileService.upload(bucketName , key , file.getInputStream(), size);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/*fileVo = new FileVo();
			fileVo.setUid(key);
			fileVo.setName(name);*/
			//int k =1/0;
			if(fileVo!=null){
				String url = "https://"+bucketName+"/"+key;
				fileVo.setUrl(url);
			}
		}
		ActionHelp.WriteStrToOut(response, fileVo); //解决中文乱码问题
	}
	
	@RequestMapping(value = "/save")
	public String save(Integer formType,Long id,String url,
			HttpServletRequest request, HttpServletResponse response, Model model){
		logger.info("save taxpayer formType = {} url={} ",formType,url);



		try {

			Preconditions.checkArgument(StringUtils.isNotBlank(url), "url 不能为空!");
			Preconditions.checkArgument(formType!=null,"formType 不能为空!");

			TeacherTaxpayerForm teacherTaxpayerForm = new TeacherTaxpayerForm();
			teacherTaxpayerForm.setId(id);
			teacherTaxpayerForm.setUrl(url);
			teacherTaxpayerForm.setFormType(formType);
			setTeacherTaxpayerFormInfo(teacherTaxpayerForm, request);
			teacherTaxpayerFormService.saveTeacherTaxpayerForm(teacherTaxpayerForm );
			
			TaxpayerView taxpayerView = teacherTaxpayerFormService.getTeacherTaxpayerView(teacherTaxpayerForm.getTeacherId());
			model.addAttribute("taxpayerView", taxpayerView);
			
			model.addAttribute("status", 1);
			//int k =1/0;
		} catch(IllegalArgumentException e){
			logger.warn("上传文件发生异常，参数不合法，errorMsg=" + e.getMessage(), e);
			model.addAttribute("status", 0);
			throw new ServiceException("上传失败");
		} catch (Exception e) {
			logger.error("上传失败！",e);
			model.addAttribute("status", 0);
			throw new ServiceException("上传失败");
		}
		
		return view("personal/personal_taxpayer");
	}

	public void setTeacherTaxpayerFormInfo(TeacherTaxpayerForm teacherTaxpayerForm,HttpServletRequest request){
		Teacher teacher = loginService.getTeacher();
		Long teacherId = teacher.getId();
		String teacherName = teacher.getRealName();
		
		teacherTaxpayerForm.setTeacherId(teacherId);
		teacherTaxpayerForm.setTeacherName(teacherName);
		teacherTaxpayerForm.setUploader(teacherId);
		teacherTaxpayerForm.setUploadTime(new Date());
		
		teacherTaxpayerForm.setIsNew(TeacherEnum.ISNew.NEW.val());
		teacherTaxpayerForm.setUploaded(TeacherEnum.UploadStatus.UPLOADED.val());
		teacherTaxpayerForm.setCreateBy(teacherId);
		teacherTaxpayerForm.setUpdateBy(teacherId);
		
		TeacherTaxpayerFormDetail detail = new TeacherTaxpayerFormDetail();
		teacherTaxpayerForm.setTeacherTaxpayerFormDetail(detail);
		
		detail.setUploaderName(teacherName);
		
	}
	

}
