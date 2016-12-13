/**
 * 
 */
package com.vipkid.trpm.email;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.util.json.Jackson;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Maps;
import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.EmailUtils;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.recruitment.common.service.AuditEmailService;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.entity.Teacher;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.alibaba.fastjson.JSON.parseArray;

/**
 * @author zouqinghua
 * @date 2016年8月19日  下午4:27:29
 *
 */
public class SendEmailTest {

	@Autowired
	private AuditEmailService auditEmailService;
	
    public static void main(String[] args) {
		Map<String, String> paramsMap = Maps.newHashMap();
			paramsMap.put("teacherName", "Bel1");
			String titleTemplate = "reminderApplicantBefore24HoursEmailSubjectTemplate.html";
			String contentTemplate = "reminderApplicantBefore24HoursEmailContentTemplate.html";
			Map<String, String> emailMap = TemplateUtils.readTemplate(contentTemplate, paramsMap, titleTemplate);
		    new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap1 = Maps.newHashMap();
		paramsMap1.put("teacherName", "Bel2");
		String titleTemplate1 = "InterviewPassTitle.html";
		String contentTemplate1 = "InterviewPass.html";
		Map<String, String> emailMap1 = TemplateUtils.readTemplate(contentTemplate1, paramsMap1, titleTemplate1);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap1, EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap2 = Maps.newHashMap();
		paramsMap2.put("teacherName", "Bel3");
		String titleTemplate2 = "InterviewPassContinueReminderTitle.html";
		String contentTemplate2 = "InterviewPassContinueReminder.html";
		Map<String, String> emailMap2 = TemplateUtils.readTemplate(contentTemplate2, paramsMap2, titleTemplate2);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap2 , EmailFormEnum.TEACHVIP);


		Map<String, String> paramsMap3 = Maps.newHashMap();
		paramsMap3.put("teacherName", "Bel4");
		String titleTemplate3 = "TrainingQuizReminderTitle.html";
		String contentTemplate3 = "TrainingQuizReminder.html";
		Map<String, String> emailMap3 = TemplateUtils.readTemplate(contentTemplate3, paramsMap3, titleTemplate3);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap3 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap4 = Maps.newHashMap();
		paramsMap4.put("teacherName", "Bel5");
		String titleTemplate4 = "TrainingPassTitle.html";
		String contentTemplate4 = "TrainingPass.html";
		Map<String, String> emailMap4 = TemplateUtils.readTemplate(contentTemplate4, paramsMap4, titleTemplate4);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap4 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap5 = Maps.newHashMap();
		paramsMap5.put("teacherName", "Bel6");
		String titleTemplate5 = "PracticumNoBookTitle.html";
		String contentTemplate5 = "PracticumNoBook.html";
		Map<String, String> emailMap5 = TemplateUtils.readTemplate(contentTemplate5, paramsMap5, titleTemplate5);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap5 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap6 = Maps.newHashMap();
		paramsMap6.put("teacherName", "Bel7");
		String titleTemplate6 = "PracticumReminderJobTitle.html";
		String contentTemplate6 = "PracticumReminderJob.html";
		Map<String, String> emailMap6 = TemplateUtils.readTemplate(contentTemplate6, paramsMap6, titleTemplate6);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap6 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap7 = Maps.newHashMap();
		paramsMap7.put("teacherName", "Bel8");
		String titleTemplate7 = "PracticumReapplyTitle.html";
		String contentTemplate7 = "PracticumReapply.html";
		Map<String, String> emailMap7 = TemplateUtils.readTemplate(contentTemplate7, paramsMap7, titleTemplate7);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap7 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap8 = Maps.newHashMap();
		paramsMap8.put("teacherName", "Bel9");
		String titleTemplate8 = "PracticumBookTitle.html";
		String contentTemplate8 = "PracticumBook.html";
		Map<String, String> emailMap8 = TemplateUtils.readTemplate(contentTemplate8, paramsMap8, titleTemplate8);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap8 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap9 = Maps.newHashMap();
		paramsMap9.put("teacherName", "Bel10");
		String titleTemplate9 = "PracticumPassTitle.html";
		String contentTemplate9= "PracticumPass.html";
		Map<String, String> emailMap9 = TemplateUtils.readTemplate(contentTemplate9, paramsMap9, titleTemplate9);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap9 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap10 = Maps.newHashMap();
		paramsMap10.put("teacherName", "Bel11");
		String titleTemplate10 = "PracticumFailTitle.html";
		String contentTemplate10 = "PracticumFail.html";
		Map<String, String> emailMap10 = TemplateUtils.readTemplate(contentTemplate10, paramsMap10, titleTemplate10);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap10 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap11 = Maps.newHashMap();
		paramsMap11.put("teacherName", "Bel12");
		String titleTemplate11 = "Practicum2StartTitle.html";
		String contentTemplate11 = "Practicum2Start.html";
		Map<String, String> emailMap11 = TemplateUtils.readTemplate(contentTemplate11, paramsMap11, titleTemplate11);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap11 , EmailFormEnum.TEACHVIP);



		Map<String, String> paramsMap13 = Maps.newHashMap();
		paramsMap13.put("teacherName", "Bel14");
		String titleTemplate13 = "ContractInfoFailAndUploadReminderJobTitle.html";
		String contentTemplate13 = "ContractInfoFailAndUploadReminderJob.html";
		Map<String, String> emailMap13 = TemplateUtils.readTemplate(contentTemplate13, paramsMap13, titleTemplate13);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap13 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap14 = Maps.newHashMap();
		paramsMap14.put("teacherName", "Bel15");
		String titleTemplate14 = "ContractInfoReapplyTitle.html";
		String contentTemplate14= "ContractInfoReapply.html";
		Map<String, String> emailMap14 = TemplateUtils.readTemplate(contentTemplate14, paramsMap14, titleTemplate14);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap14 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap15 = Maps.newHashMap();
		paramsMap15.put("teacherName", "Bel16");
		String titleTemplate15 = "ContractInfoUploadReminderTitle.html";
		String contentTemplate15= "ContractInfoUploadReminder.html";
		Map<String, String> emailMap15 = TemplateUtils.readTemplate(contentTemplate15, paramsMap15, titleTemplate15);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap15 , EmailFormEnum.TEACHVIP);

		Map<String, String> paramsMap16 = Maps.newHashMap();
		paramsMap16.put("teacherName", "Bel17");
		String titleTemplate16 = "ContractInfoPassTitle.html";
		String contentTemplate16= "ContractInfoPass.html";
		Map<String, String> emailMap16 = TemplateUtils.readTemplate(contentTemplate16, paramsMap16, titleTemplate16);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap16 , EmailFormEnum.TEACHVIP);


		Map<String, String> paramsMap12 = Maps.newHashMap();
		paramsMap12.put("teacherName", "Bel13");
		String titleTemplate12 = "Practicum2NoBookTitle.html";
		String contentTemplate12 = "Practicum2NoBook.html";
		Map<String, String> emailMap12 = TemplateUtils.readTemplate(contentTemplate12, paramsMap12, titleTemplate12);
		new EmailEngine().addMailPool("zhaojia3@vipkid.com.cn ", emailMap12 , EmailFormEnum.TEACHVIP);

	}

	//@Test
	public void dateTest(){
		//Date date = UADateUtils.getToday(20);
		Calendar calendar = Calendar.getInstance();
		//calendar.add(Calendar.DATE, 0);
		 int hour = calendar.get(Calendar.HOUR_OF_DAY)-8;
		calendar.set(Calendar.HOUR_OF_DAY, hour);

		String dateStr = UADateUtils.format(calendar.getTime(), null);
		System.out.println(dateStr);
	}

	@Test
	public void findOnlineClass(){
		StringBuilder stringBuilder = new StringBuilder();
		String s = " [{\"id\":\"avatar\",\"name\":\"avatar\",\"result\":\"FAIL\",\"failReason\":\"\",\"url\":\"http://ogmdlv3io.bkt.clouddn.com/teacher/avatar/2069783/98bfc1142f4f4de0ad6734a7bbdfbc56/avatar_medium/teacher-avatar-2069783.png\"},{\"id\":\"lifePictures_157\",\"name\":\"lifePictures\",\"result\":\"FAIL\",\"failReason\":\"\",\"url\":\"http://ogmdlv3io.bkt.clouddn.com/teacher/lifePicture/2069783/33fa8db95c29425184b038cedb4f550e/life_picture_medium/teacher-lifephoto-2069783.png\"},{\"id\":\"shortVideo\",\"name\":\"shortVideo\",\"result\":\"FAIL\",\"failReason\":\"\",\"url\":\"http://ogmdlv3io.bkt.clouddn.com/teacher/shortVideo/2069783/ae59fe5e5ded4fa5b754f1469d542591/short_video_medium/20161208-191858.mp4\"},{\"id\":\"seftIntroduction\",\"name\":\"seftIntroduction\",\"result\":\"FAIL\",\"failReason\":\"\",\"url\":\"dfasfasf\"}]";
		JSONArray array =  JSON.parseArray(s);
		List<TeacherContractFile> teacherContractFiles = new ArrayList<TeacherContractFile>();
		TeacherContractFile teacherContractFile =new TeacherContractFile();
		teacherContractFile.setResult("FAIL");
		teacherContractFile.setFileType(1);
		teacherContractFiles.add(teacherContractFile);

		TeacherContractFile teacherContractFile2 =new TeacherContractFile();
		teacherContractFile2.setResult("FAIL");
		teacherContractFile2.setFileType(2);
		teacherContractFiles.add(teacherContractFile2);

		TeacherContractFile teacherContractFile3 =new TeacherContractFile();
		teacherContractFile3.setResult("FAIL");
		teacherContractFile3.setFileType(3);
		teacherContractFiles.add(teacherContractFile3);

		TeacherContractFile teacherContractFile4 =new TeacherContractFile();
		teacherContractFile4.setResult("FAIL");
		teacherContractFile4.setFileType(4);
		teacherContractFiles.add(teacherContractFile4);

		TeacherContractFile teacherContractFile5 =new TeacherContractFile();
		teacherContractFile5.setResult("FAIL");
		teacherContractFile5.setFileType(5);
		teacherContractFiles.add(teacherContractFile5);

		TeacherContractFile teacherContractFile6 =new TeacherContractFile();
		teacherContractFile6.setResult("FAIL");
		teacherContractFile6.setFileType(6);
		teacherContractFiles.add(teacherContractFile6);


		teacherContractFiles.forEach(obj -> {
			if(StringUtils.equals(obj.getResult(), TeacherApplicationEnum.Result.FAIL.toString())) {
				if(StringUtils.isNotBlank(fileType(obj.getFileType()))){
					stringBuilder.append(fileType(obj.getFileType()));
					stringBuilder.append(",   ");
				}
			}
		});

			for (int i=0;i<array.size();i++) {
				JSONObject ob = (JSONObject) array.get(i);
				if (StringUtils.equals(ob.getString("result"), TeacherApplicationEnum.Result.FAIL.toString())) {
					if(StringUtils.isNotBlank(changeName(ob.getString("name")))) {
						if (i == array.size() - 1) {
							stringBuilder.append(changeName(ob.getString("name")));
						} else {
							stringBuilder.append(changeName(ob.getString("name")));
							stringBuilder.append(",   ");
						}
					}
				}
			}
		System.out.print(stringBuilder.toString());
	}

	private String fileType(int TypeNum){
		if (TypeNum == TeacherApplicationEnum.ContractFileType.OTHER_DEGREES.val()) {
			return "Other degrees";
		}
		if (TypeNum == TeacherApplicationEnum.ContractFileType.CERTIFICATIONFILES.val()) {
			return "Teaching certificates";
		}
		if (TypeNum == TeacherApplicationEnum.ContractFileType.IDENTIFICATION.val()) {
			return "Identity card";
		}
		if (TypeNum == TeacherApplicationEnum.ContractFileType.PASSPORT.val()) {
			return "Passport picture page";

		}
		if (TypeNum == TeacherApplicationEnum.ContractFileType.DRIVER.val()) {
			return "Driver's license";
		}
		if (TypeNum == TeacherApplicationEnum.ContractFileType.DIPLOMA.val()) {
			return "Diploma";
		}
		if (TypeNum == TeacherApplicationEnum.ContractFileType.CONTRACT.val()) {
			return "Contract";
		}
		if (TypeNum == TeacherApplicationEnum.ContractFileType.CONTRACT_W9.val()) {
			return "W-9 fillable form";
		}
		return null;
	}


	private String  changeName(String name){
		if(StringUtils.isNotBlank(name)) {
			if (StringUtils.equals(name, "avatar")) {
				return "Professional Profile Picture";
			}
			if (StringUtils.equals(name, "lifePictures")) {
				return "Casual pictures of yourself";
			}
			if (StringUtils.equals(name, "shortVideo")) {
				return "15 Second video greeting";
			}
			if (StringUtils.equals(name, "seftIntroduction")) {
				return "Self introduction";
			}
		}
		return null;
	}


	@Test
	public void sendEmail(){
			String email = "zhaojia3@vipkid.com.cn ";
			String name = "Bel";
			String titleTemplate = "reminderApplicantBefore24HoursEmailSubjectTemplate.html";
			String contentTemplate = "reminderApplicantBefore24HoursEmailContentTemplate.html";
			EmailUtils.sendEmail4Recruitment(email, name, titleTemplate, contentTemplate);


	}

}
