/**
 * 
 */
package com.vipkid.trpm.email;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.util.json.Jackson;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Maps;
import com.google.gson.JsonObject;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.recruitment.common.service.AuditEmailService;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.task.utils.UADateUtils;
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
     
		for(int i = 0 ; i < 10 ; i++){
		    Map<String, String> sendMap = Maps.newHashMap();
	        sendMap.put("title", "Email"+"第gmail:"+i);
	        sendMap.put("content", "hello world!");
		    new EmailEngine().addMailPool("zwlzwl376@gmail.com", sendMap , EmailFormEnum.TEACHVIP);
            Map<String, String> sendMap2 = Maps.newHashMap();
            sendMap2.put("title", "Email"+"163第"+i);
            sendMap2.put("content", "hello world!");
		    new EmailEngine().addMailPool("zwlzwl376@126.com", sendMap2, EmailFormEnum.TEACHVIP);
		}
		
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

}
