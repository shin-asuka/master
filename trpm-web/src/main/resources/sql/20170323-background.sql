CREATE TABLE `bg_sterling_screening` (
`id` BIGINT(20) unsigned NOT NULL AUTO_INCREMENT,
`teacher_id` BIGINT NULL COMMENT '老师ID',
`candidate_id` VARCHAR(15) NULL COMMENT 'Sterling 系统的候选人ID',
`screening_id` VARCHAR(15) NULL COMMENT 'Sterling 系统筛选请求标识',
`status` VARCHAR(10) NULL COMMENT 'Sterling 系统状态，new,pending,complete,error,release,cancelled,rejected',
`result` VARCHAR(10) NULL COMMENT 'Sterling 系统 结果 n/a,clear,alert',
`dispute_status` VARCHAR(15) NULL COMMENT '有争议的状态 可能为空：active,deactivated',
`dispute_created_at` DATETIME NULL COMMENT '争议开始的时间',
`submitted_at` DATETIME NULL COMMENT 'Sterling 系统 screening 提交的时间',
`update_at` DATETIME NULL COMMENT 'Sterling 系统 screening 最后一次修改的时间',
`web_link` TEXT NULL COMMENT '报告结果 web地址',
`pdf_link` TEXT NULL COMMENT '报告结果 pdf 地址',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`id`),
INDEX `idx_screening_id_candidate_id` (`candidate_id`, `screening_id`),
INDEX `idx_teacher_id` (`teacher_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bg_sterling_adverse` (
`id` BIGINT(20) unsigned NOT NULL AUTO_INCREMENT,
`screening_id` VARCHAR(15) NULL COMMENT 'Sterling 系统的 screening id',
`bg_sterling_screening_id` BIGINT NULL COMMENT '数据库表 bg_sterling_screening 的主键',
`actions_id` VARCHAR(15) NULL COMMENT 'Sterling 系统的 actions id',
`actions_status` VARCHAR(18) NULL COMMENT 'Sterling 系统 actions status  ：initated,awaiting,complete,cancelled',
`actions_updated_at` DATETIME NULL COMMENT 'adverse 最后一次变更的时间',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`id`),
INDEX `idx_bg_sterling_screening_id` (`bg_sterling_screening_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bg_sterling_report` (
`id` BIGINT(20) unsigned NOT NULL AUTO_INCREMENT ,
`report_id` VARCHAR(15) NULL COMMENT 'Sterling 系统report item id',
`screening_id` VARCHAR(15) NULL COMMENT 'Sterling 系统的screening 的id',
`bg_sterling_screening_id` BIGINT NULL COMMENT '系统表 bg_sterling_screening 主键',
`type` TEXT NULL COMMENT '报告的类型',
`status` VARCHAR(10) NULL COMMENT '报告的状态 new,pending,complete,error,release,cancelled,rejected',
`result` VARCHAR(10) NULL COMMENT 'n/a,alert,clear,not verified,verified,discrepancy,no record,complete,success,partial match,no data,error,review needed',
`updated_at` DATETIME NULL COMMENT '最后一次修改的时间',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`id`),
INDEX `idx_bg_sterling_screening_id` (`bg_sterling_screening_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



CREATE TABLE `teacher_gated_launch`(
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '老师ID',
  `type_number` int(11) NOT NULL DEFAULT '0' COMMENT '1:backgroundCheck;',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:deactivated; 1:active',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建人',
  `update_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新人',
   PRIMARY KEY (`id`),
   KEY idx_teacher_type_status(`teacher_id`,`type_number`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教师端灰度发布控制表';

CREATE TABLE `bg_sterling_screening_ca` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint(20) NOT NULL COMMENT '老师ID',
  `result` varchar(20) NOT NULL DEFAULT '' COMMENT 'PASS FAIL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建人',
  `update_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY idx_teacher_id(`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='加拿大老师背景调查表';


CREATE TABLE `teacher_license` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint(20) NOT NULL COMMENT '老师ID',
  `social_no` varchar(100) NOT NULL DEFAULT '' COMMENT '社会安全或社会保险号',
  `driver_license` varchar(100) NOT NULL DEFAULT '' COMMENT '驾照号码',
  `driver_license_type` varchar(50) NOT NULL DEFAULT '' COMMENT '驾照类型',
  `driver_license_issuing_agency` varchar(200) NOT NULL DEFAULT '' COMMENT '驾照签发机构',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建人',
  `update_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='老师证件表';


ALTER TABLE `teacher_address`
ADD COLUMN `type` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '地址类型，0：老数据默认；1：最近美国地址(背调用)' AFTER `zip_code`;

ALTER TABLE `teacher`
ADD COLUMN `maiden_name` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '夫姓（已婚女士）' AFTER `last_name`;

ALTER TABLE `teacher_contract_file`
ADD COLUMN `screening_id` int(11) DEFAULT NULL COMMENT '背景调查表id' AFTER `teacher_application_id`;

ALTER TABLE teacher_contract_file MODIFY fail_reason VARCHAR(1024);