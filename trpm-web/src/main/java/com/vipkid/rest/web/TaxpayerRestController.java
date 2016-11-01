package com.vipkid.rest.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.file.utils.ActionHelp;
import com.vipkid.file.utils.DateUtils;
import com.vipkid.file.utils.Encodes;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.exception.UserAuthException;
import com.vipkid.rest.utils.UserUtils;
import com.vipkid.trpm.entity.Page;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.entity.TeacherTaxpayerView;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.portal.LocationService;
import com.vipkid.trpm.service.portal.PersonalInfoService;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import com.vipkid.trpm.util.AwsFileUtils;

/**
 * @author zouqinghua
 * @date 2016年10月17日 上午10:45:47
 *
 */
@Controller
@RequestMapping("/personal/taxpayer")
public class TaxpayerRestController {

	private Logger logger = LoggerFactory.getLogger(TaxpayerRestController.class);

	@Autowired
	private AwsFileService awsFileService;

	@Autowired
	private IndexService indexService;

	@Autowired
	private TeacherTaxpayerFormService teacherTaxpayerFormService;

	@Autowired
	private LocationService locationService;
	
	@ResponseBody
	@RequestMapping(value = "/findPage")
	public Page<TeacherTaxpayerView> findPage(TeacherTaxpayerView teacherTaxpayerView, HttpServletRequest request,
			HttpServletResponse response) {

		Page<TeacherTaxpayerView> page = new Page<TeacherTaxpayerView>(request, response);
		if(teacherTaxpayerView!=null && teacherTaxpayerView.getName()!=null){
			String name = teacherTaxpayerView.getName();
			if("".equals(name.trim())){
				name = name.trim();
			}
			teacherTaxpayerView.setName(name);
		}
		logger.info("分页查询 taxpayer page = {}", JsonUtils.toJSONString(page));
		if (teacherTaxpayerView == null) {
			teacherTaxpayerView = new TeacherTaxpayerView();
		}
		page = teacherTaxpayerFormService.findPage(page, teacherTaxpayerView);

		return page;
	}

	@ResponseBody
	@RequestMapping(value = "/updateStatus")
	public Map<String,Object> updateStatus(Long taxpayerId, Long taxpayerDetailId, Integer isNew,HttpServletRequest request,
			HttpServletResponse response){
		
		User user = UserUtils.getUser(request);
		logger.info("user id = {} username = {}",user.getId(),user.getUsername() );
		if(user.getId() == 0){
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			logger.info("用户身份认证失败!");
			return null;
		}
		
		logger.info("updateStatus taxpayerId = {},taxpayerDetailId = {}, isNew = {}",taxpayerId,taxpayerDetailId,isNew);
		Preconditions.checkArgument(taxpayerDetailId!=null, "taxpayerDetailId 不能为空!");
		Preconditions.checkArgument(isNew!=null , "isNew 不能为空!");
		Map<String,Object> map = Maps.newHashMap();
		try {
			teacherTaxpayerFormService.updateTaxpayerStatus(taxpayerId,taxpayerDetailId, isNew);
			map.put("status", 1);
		} catch (Exception e) {
			logger.error("updateStatus fail",e);
			map.put("status", 0);
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/saveTaxpayerForm")
	public TeacherTaxpayerForm saveTaxpayerForm(TeacherTaxpayerForm teacherTaxpayerForm,HttpServletRequest request,HttpServletResponse response){
		logger.info("saveTaxpayerForm form = {}",JsonUtils.toJSONString(teacherTaxpayerForm));
		
		User user = UserUtils.getUser(request);
		logger.info("user id = {} username = {}",user.getId(),user.getUsername() );
		if(user.getId() == 0){
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			logger.info("用户身份认证失败!");
			return null;
		}
		
		Preconditions.checkArgument(teacherTaxpayerForm!=null, "数据不能为空！");
		Preconditions.checkArgument(teacherTaxpayerForm.getTeacherId()!=null, "teacherId不能为空!");
		Preconditions.checkArgument(StringUtils.isNotBlank(teacherTaxpayerForm.getTeacherName()), "teacherName 不能为空!");
		Preconditions.checkArgument(StringUtils.isNotBlank(teacherTaxpayerForm.getUrl()), "url 不能为空!");
		Preconditions.checkArgument(teacherTaxpayerForm.getFormType()!=null,"formType 不能为空!");
		
		Long uploader = user.getId();
		String uploaderName = user.getUsername();
		
		Date date = new Date();
		teacherTaxpayerForm.setUploader(uploader);
		teacherTaxpayerForm.setUploadTime(date);
		teacherTaxpayerForm.setUploaded(TeacherEnum.UploadStatus.UPLOADED.val());
		teacherTaxpayerForm.setIsNew(1);
		teacherTaxpayerForm.setCreateBy(uploader);
		teacherTaxpayerForm.setUpdateBy(uploader);
		
		TeacherTaxpayerFormDetail detail = new TeacherTaxpayerFormDetail();
		detail.setUploaderName(uploaderName);
		
		
		teacherTaxpayerForm.setTeacherTaxpayerFormDetail(detail);
		teacherTaxpayerFormService.saveTeacherTaxpayerForm(teacherTaxpayerForm );
		return teacherTaxpayerForm;
	}
	
	//@ResponseBody
	@RequestMapping("/uploadFile")
	public void taxpayerUpload(@RequestParam("file") MultipartFile file,Long teacherId,HttpServletRequest request, HttpServletResponse response, Model model){
		logger.info("upload taxpayer file = {}",file);
		
		User user = UserUtils.getUser(request);
		logger.info("user id = {} username = {}",user.getId(),user.getUsername() );
		if(user.getId() == 0){
			logger.info("用户身份认证失败!");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return ;
		}
		
		FileVo fileVo = null;
		if(file!=null){
			String name = file.getOriginalFilename();
			String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
			
			teacherId = teacherId == null?0:teacherId;
			String key = AwsFileUtils.getTaxpayerkey(teacherId+"-"+name);
			Long size = file.getSize();
			
			Preconditions.checkArgument(AwsFileUtils.checkFileType(name), "文件类型不正确，支持类型为"+AwsFileUtils.TAPXPAYER_FILE_TYPE);
			Preconditions.checkArgument(AwsFileUtils.checkFileSize(size), "文件太大，maxSize = "+AwsFileUtils.TAPXPAYER_FILE_MAX_SIZE);
			
			try {
				fileVo = awsFileService.upload(bucketName , key , file.getInputStream(), file.getSize());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(fileVo!=null){
				String url = "http://"+bucketName+"/"+key;
				fileVo.setUrl(url);
			}
		}
		ActionHelp.WriteStrToOut(response, fileVo); //解决中文乱码问题
		//return fileVo;
	}
	
	@RequestMapping(value = { "/batchDown" })
	public void batchDown(String taxpayerDetailIds, HttpServletRequest request, HttpServletResponse response) {

		if (StringUtils.isNotBlank(taxpayerDetailIds)) {
			List<Long> idList = Lists.transform(Arrays.asList(taxpayerDetailIds.split(",")), p -> Long.valueOf(p));
			Preconditions.checkArgument(idList.size() <= 20, "下载文件数目不能超过20");

			List<TeacherTaxpayerFormDetail> list = teacherTaxpayerFormService.findDetailListByIds(idList);

			List<FileVo> fileVos = Lists.newArrayList();
			for (TeacherTaxpayerFormDetail taxpayer : list) {
				String url = taxpayer.getUrl();
				Long taxpayerDetailId = taxpayer.getId();
				Long taxpayerFormId = taxpayer.getTaxpayerFormId();
				
				if (StringUtils.isNotBlank(url)) {
					String path = url;
					logger.info("获取文件  url = {}",url);
					if (path.startsWith("http://")) {
						path = path.substring(7);
					}
					String bucketName = null;
					String key = null;
					if(path.contains("/")){
						Integer index = path.indexOf("/");
						bucketName = path.substring(0, index);
						key = path.substring(index + 1);
					}
					if(StringUtils.isBlank(bucketName) || StringUtils.isBlank(key)){
						logger.info("文件信息为空  bucketName = {}, key = {}",bucketName,key);
						continue;
					}
					 
					FileVo fileVo = awsFileService.down(bucketName, key);
					if(fileVo == null){
						logger.info("文件不存在  bucketName = {}, key = {}",bucketName,key);
						continue;
					}
					String uploaderName = taxpayer.getUploaderName();
					Long teacherId = taxpayer.getTeacherId();
					teacherId = teacherId == null?0:teacherId;
					String fileName = fileVo.getName();
					String formType = TeacherEnum.getFormTypeById(taxpayer.getFormType()).name();
					String name = StringUtils.join(new String[]{formType,uploaderName,taxpayerDetailId.toString(),fileName}, "-");
					fileVo.setName(name);
					fileVos.add(fileVo);
				}
				
				if(TeacherEnum.ISNew.NEW.val().equals(taxpayer.getIsNew())){ //更新状态
					teacherTaxpayerFormService.updateTaxpayerStatus(taxpayerFormId, taxpayerDetailId, TeacherEnum.ISNew.OLD.val());
				}
			}
			String fileName = "taxpayerform" + DateUtils.formatDateByFormat(new Date(), "yyyyMMddHHmmss") + ".zip";

			down(fileName, fileVos, response); //下载文件
		}

	}

	public void down(String fileName, List<FileVo> fileVos, HttpServletResponse response) {
		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));

		ZipOutputStream zos = null;
		BufferedInputStream bis = null;
		byte[] bufs = new byte[1024 * 10];
		try {
			zos = new ZipOutputStream(response.getOutputStream());
			
			for (FileVo fileVo : fileVos) {
				String name = fileVo.getName();
				InputStream inputStream = null;
				try {
					inputStream = fileVo.getInputStream();
					if(inputStream == null){
						continue;
					}
					ZipEntry zipEntry = new ZipEntry(name);
					zos.putNextEntry(zipEntry);
					bis = new BufferedInputStream(inputStream, 1024 * 10);
					int read = 0;
					while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
						zos.write(bufs, 0, read);
					}
				} catch (Exception e) {
					logger.error("压缩文件失败", e);
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(bis!=null){
					bis.close();
				}
				if (zos != null) {
					zos.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}
	
	
	@ResponseBody
	@RequestMapping(value = "/findCountyList")
	public List<TeacherLocation> findCountyList(){
		Integer parentId = 0;
		Integer level = 1;
		logger.info("findCountyList parentId = {}, level = {}",parentId,level);
		List<TeacherLocation> list = locationService.getLocationList(parentId,level);
		logger.info("countyList parentId = {}, level = {} list = {}",parentId,level,list.size());
		List<TeacherLocation> countyList = Lists.newArrayList();
		int usaId = 2497273; //美国
		int canadaId = 230742;	//加拿大
		TeacherLocation usa = null;
		TeacherLocation canada = null;
		if(CollectionUtils.isNotEmpty(list)){ //美国加拿大置顶
			for (TeacherLocation teacherLocation : list) {
				int counryId = teacherLocation.getId();
				if(usaId == counryId){
					usa = teacherLocation;
				}
				if(canadaId == counryId ){
					canada = teacherLocation;
				}
			}
			countyList.add(usa);
			countyList.add(canada);
			
			for (TeacherLocation teacherLocation : list) {
				int counryId = teacherLocation.getId();
				if(usaId != counryId && canadaId != counryId ){
					countyList.add(teacherLocation);
				}
			}
		}
		return countyList;
	}

	@ResponseBody
	@RequestMapping(value = "/findStateList")
	public List<TeacherLocation> findStateList( Integer parentId ){
		logger.info("findStateList parentId = {}",parentId);
		Preconditions.checkArgument(parentId != null , "parentId 不能为空");

		Integer level = 2;
		List<TeacherLocation> list = locationService.getLocationList(parentId,level);
		logger.info("stateList parentId = {}, level = {} list = {}",parentId,level,list.size());
		
		return list;
	}
	
	@ResponseBody
	@RequestMapping(value = "/findCityList")
	public List<TeacherLocation> findCityList( Integer parentId ){
		logger.info("findCityList parentId = {}",parentId);
		Preconditions.checkArgument(parentId != null , "parentId 不能为空");

		Integer level = 3;
		List<TeacherLocation> list = locationService.getLocationList(parentId,level);
		logger.info("cityList parentId = {}, level = {} list = {}",parentId,level,list.size());
		
		return list;
	}
	
	
	
	
}
