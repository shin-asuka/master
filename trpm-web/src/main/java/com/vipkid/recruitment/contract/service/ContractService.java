package com.vipkid.recruitment.contract.service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherTaxpayerFormDao;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.recruitment.dao.TeacherOtherDegreesDao;
import com.vipkid.recruitment.entity.ContractFile;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.recruitment.entity.TeacherOtherDegrees;

/**
 * Created by zhangzhaojun on 2016/11/14.
 */
@Service
public class ContractService {
    private static Logger logger = LoggerFactory.getLogger(ContractService.class);
    @Autowired
    private TeacherOtherDegreesDao teacherOtherDegreesDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherTaxpayerFormDao teacherTaxpayerFormDao;

    @Autowired
    private TeacherAddressDao teacherAddressDao;

    /**
     * 更新teacher表
     * @param teacher
     * @return
     */
     public int  updateTeacher(Teacher teacher){
         return teacherDao.update(teacher);
     }
    /**
     * 更新Teacher表中的DiplomaUrl
     * @param teacher
     * @return
     */
    public Map<String,Object>  updateDiplomaUrl(Teacher teacher){
        Teacher t = teacherDao.findById(teacher.getId());
        if(t.getBachelorDiploma().length()<1){
            return ResponseUtils.responseFail("Your DiplomaUrl file is Empty . !",this);
        }
        this.teacherDao.update(teacher);
        return ResponseUtils.responseSuccess();
    }
    /**
     * 更新Teacher表中的ContractUrl
     * @param teacher
     * @return
     */
     public Map<String,Object>  updateContract(Teacher teacher){
        Teacher t = teacherDao.findById(teacher.getId());
        if(t.getContract().length()<1){
            return ResponseUtils.responseFail("Your ContractUrl file is Empty . !",this);
        }
        this.teacherDao.update(teacher);
        return ResponseUtils.responseSuccess();
    }
    /**
     * 更新Teacher表中的IdentificationUrl
     * @param teacher
     * @return
     */
    public Map<String,Object>  updateIdentification(Teacher teacher){
        Teacher t = teacherDao.findById(teacher.getId());
        if(t.getPassport().length()<1){
            return ResponseUtils.responseFail("Your IdentificationUrl file is Empty . !",this);
        }
        this.teacherDao.update(teacher);
        return ResponseUtils.responseSuccess();
    }


    /**
     * 在TeacherApplication中加入一条带审核数据
     * @param teacher
     * @return
     */
    public Map<String,Object>  updateTeacherApplication(Teacher teacher,List<Integer> ids){
        Teacher t = teacherDao.findById(teacher.getId());
        TeacherTaxpayerForm teacherTaxpayerForm = teacherTaxpayerFormDao.findByTeacherIdAndType(teacher.getId(), TeacherEnum.FormType.W9.val());
        if(teacherTaxpayerForm==null){
            if(teacher.getCountry().equals("USA")) {
                return ResponseUtils.responseFail("Your W9 file is not uploaded. !", this);
            }else {
                //查询教师的Location id
                  TeacherAddress teacherAddress = teacherAddressDao.findById(t.getCurrentAddressId());
                //  2497273 = 老师location 为   United States
                if(teacherAddress!=null&&teacherAddress.getCountryId()==2497273){
                    return ResponseUtils.responseFail("Your W9 file is not uploaded. !", this);
                }
            }

        }

        List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                TeacherApplication application = list.get(i);
                application.setCurrent(0);
                this.teacherApplicationDao.update(application);
            }
        }
        TeacherApplication application = new TeacherApplication();
        application.setTeacherId(teacher.getId());//  步骤关联的教师
        application.setApplyDateTime(new Timestamp(System.currentTimeMillis()));
        application.setStatus(TeacherApplicationEnum.Status.CONTRACT.toString());
        application = teacherApplicationDao.initApplicationData(application);
        this.teacherApplicationDao.save(application);
        logger.info("用户：{}，update table TeacherApplication Column Current = 0,  add table TeacherApplication row Current = 1",teacher.getId());
        TeacherOtherDegrees teacherOtherDegrees;
        List<TeacherOtherDegrees> ts = new ArrayList<TeacherOtherDegrees>();
        for(Integer id:ids){
            logger.info("查询文件id:{}",id);
            teacherOtherDegrees =teacherOtherDegreesDao.findById(id);
            teacherOtherDegrees.setTeacherApplicationId(application.getId());
            logger.info("applicationId:{}",application.getId());
            ts.add(teacherOtherDegrees);
        }
        logger.info("用户：{}批量更新文件的TeacherApplicationId:{}",teacher.getId(),application.getId());
        teacherOtherDegreesDao.updateBatch(ts);
        //查询
        List<TeacherOtherDegrees> files =  teacherOtherDegreesDao.findByTeacherIdAndTeacherApplicationId(teacher.getId(),application.getId());
        if(CollectionUtils.isNotEmpty(files)) {
            int Identification = 0;
            int Diploma = 0;
            int Contract = 0;
            for (TeacherOtherDegrees file : files) {
                if (file.getFileType() == 3) {
                    Identification++;
                }
                if (file.getFileType() == 4) {
                    Diploma++;
                }
                if (file.getFileType() == 5) {
                    Contract++;
                }
            }

            if (Identification == 0 || Diploma == 0 || Contract == 0) {
                return ResponseUtils.responseFail("Your file is not uploaded. !", this);
            }
            return ResponseUtils.responseSuccess();
        }else{
            return ResponseUtils.responseFail("Your file is not uploaded. !", this);
        }

       }

    /**
     * 插入teacherOtherDegrees里的文件
     * @param teacherOtherDegrees
     * @return
     */
    public  int save(TeacherOtherDegrees teacherOtherDegrees){
           return teacherOtherDegreesDao.save(teacherOtherDegrees);
       }

    /**
     * 删除teacherOtherDegrees里的文件
     * @param teacherOtherDegrees
     * @return
     */
    public  int delete(TeacherOtherDegrees teacherOtherDegrees){
            return teacherOtherDegreesDao.delete(teacherOtherDegrees);
        }

        public Map<String,Object> reomteFile(int fileId,Teacher teacher){
           TeacherOtherDegrees teacherOtherDegrees =  teacherOtherDegreesDao.findById(fileId);
            if(teacherOtherDegrees.getTeacherId()!=teacher.getId()){
                return ResponseUtils.responseFail("You can't delete the file !",this);
            }else {
                if (teacherOtherDegrees.getTeacherApplicationId() == 0) {
                    teacherOtherDegreesDao.delete(teacherOtherDegrees);
                    return ResponseUtils.responseSuccess();
                } else {
                    return ResponseUtils.responseSuccess();
                }
            }
        }

    /**
     * 更改老师的lifeCycle
     * @param teacher
     * @return
     */
    public Map<String,Object> toPublic(Teacher teacher){
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ResponseUtils.responseFail("You have no legal power into the next phase !",this);
        }
        if(TeacherApplicationEnum.Status.CONTRACT.toString().equals(listEntity.get(0).getStatus())
                && TeacherApplicationEnum.Result.PASS.toString().equals(listEntity.get(0).getResult())){


            teacher.setLifeCycle(TeacherEnum.LifeCycle.PUBLICITY_INFO.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(), TeacherEnum.LifeCycle.CONTRACT, TeacherEnum.LifeCycle.PUBLICITY_INFO, teacher.getId());
            this.teacherDao.update(teacher);
            return ResponseUtils.responseSuccess();
        }
        return ResponseUtils.responseFail("You have no legal power into the next phase !",this);
    }



    /**
     * 查询老师上传过的文件
     * @param t
     * @return
     */
    public ContractFile findContract(Teacher t){
        ContractFile contractFile = new ContractFile();
        Teacher teacher =  teacherDao.findById(t.getId());
         logger.info("用户：{}查询上传文件",teacher.getId());

        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        logger.info("用户：{}查询TeacherApplication",teacher.getId());
        List<TeacherOtherDegrees> teacherOtherDegreeses = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listEntity)) {
            TeacherApplication teacherApplication = listEntity.get(0);
           teacherOtherDegreeses = teacherOtherDegreesDao.findByTeacherIdAndTeacherApplicationId(teacher.getId(), teacherApplication.getId());
        }
        if(CollectionUtils.isEmpty(teacherOtherDegreeses)) {
            logger.info("用户{}查询未提交的文件", teacher.getId());
            teacherOtherDegreeses = teacherOtherDegreesDao.findByTeacherId(teacher.getId());
        }
            if(CollectionUtils.isNotEmpty(teacherOtherDegreeses)) {
                List<TeacherOtherDegrees>  degrees = new ArrayList<TeacherOtherDegrees>();
                List<TeacherOtherDegrees>  contract = new ArrayList<TeacherOtherDegrees>();
                List<String>  res = new ArrayList<String>();
                List<TeacherOtherDegrees>  identification = new ArrayList<TeacherOtherDegrees>();
                List<TeacherOtherDegrees>  diploma = new ArrayList<TeacherOtherDegrees>();
                List<TeacherOtherDegrees>  tax = new ArrayList<TeacherOtherDegrees>();
                List<TeacherOtherDegrees>  certification = new ArrayList<TeacherOtherDegrees>();
                teacherOtherDegreeses.forEach(obj->{
                  if (obj.getFileType() == 1) {
                      degrees.add(obj);
                  }
                  if (obj.getFileType() == 2) {
                      certification.add(obj);
                  }
                  if (obj.getFileType() == 3) {
                      obj.setTypeName("identity");
                      identification.add(obj);
                  }
                  if (obj.getFileType() == 6) {
                      obj.setTypeName("passport");
                      identification.add(obj);

                  }
                  if (obj.getFileType() == 7) {
                      obj.setTypeName("driver");
                      identification.add(obj);
                  }
                  if (obj.getFileType() == 4) {
                      diploma.add(obj);
                  }
                  if (obj.getFileType() == 5) {
                      contract.add(obj);
                  }
                  if (obj.getFileType() == 8) {
                      tax.add(obj);
                    }
                  if(obj.getResult()!=null&&obj.getResult().equals("")) {
                      res.add(obj.getResult());
                  }

                });
                if(CollectionUtils.isNotEmpty(tax)) {
                    contractFile.setTax(tax.get(tax.size()-1));
                }

                if(CollectionUtils.isNotEmpty(contract)) {
                    contractFile.setContract(contract.get(contract.size()-1));
                }
                if(CollectionUtils.isNotEmpty(diploma)){
                    contractFile.setDiploma(diploma.get(diploma.size()-1));
                }
                if(CollectionUtils.isNotEmpty(identification)){
                    contractFile.setIdentification(identification.get(identification.size()-1));
                }
                contractFile.setResult(isPass(res));
                contractFile.setCertification(certification);
                contractFile.setDegrees(degrees);
        }



            return contractFile;

    }

    public String isPass(List<String> res){
        if(CollectionUtils.isEmpty(res)){
            return String.valueOf(TeacherApplicationEnum.Result.FAIL);
        }

        for(String result:res){
            if(result.equals("FAIL")){
                return String.valueOf(TeacherApplicationEnum.Result.FAIL);
            }
        }
        return String.valueOf(TeacherApplicationEnum.Result.PASS);
    }

}
