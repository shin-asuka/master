DROP TABLE IF EXISTS `bg_sterling_screening`;
CREATE TABLE `bg_sterling_screening` (
`id` BIGINT NULL AUTO_INCREMENT,
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
`create_time` DATETIME NULL COMMENT '记录创建的时间',
`update_time` DATETIME NULL COMMENT '记录最后一次更新的时间',
PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bg_sterling_adverse`;
CREATE TABLE `bg_sterling_adverse` (
`id` BIGINT NULL AUTO_INCREMENT,
`screening_id` VARCHAR(15) NULL COMMENT 'Sterling 系统的 screening id',
`bg_sterling_screening_id` BIGINT NULL COMMENT '数据库表 bg_sterling_screening 的主键',
`actions_id` VARCHAR(15) NULL COMMENT 'Sterling 系统的 actions id',
`actions_status` VARCHAR(18) NULL COMMENT 'Sterling 系统 actions status  ：initated,awaiting,complete,cancelled',
`actions_updated_at` DATETIME NULL COMMENT 'adverse 最后一次变更的时间',
`create_time` DATETIME NULL COMMENT '本记录创建的时间',
`update_time` DATETIME NULL COMMENT '本记录最后一次修改的时间',
PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `bg_sterling_report`;
CREATE TABLE `bg_sterling_report` (
`id` BIGINT NULL AUTO_INCREMENT ,
`report_id` VARCHAR(15) NULL COMMENT 'Sterling 系统report item id',
`screening_id` VARCHAR(15) NULL COMMENT 'Sterling 系统的screening 的id',
`bg_sterling_screening_id` INTEGER NULL COMMENT '系统表 bg_sterling_screening 主键',
`type` TEXT NULL COMMENT '报告的类型',
`status` VARCHAR(10) NULL COMMENT '报告的状态 new,pending,complete,error,release,cancelled,rejected',
`result` VARCHAR(10) NULL COMMENT 'n/a,alert,clear,not verified,verified,discrepancy,no record,complete,success,partial match,no data,error,review needed',
`updated_at` DATETIME NULL COMMENT '最后一次修改的时间',
`create_time` DATETIME NULL COMMENT '本记录创建的时间',
`update_time` DATETIME NULL COMMENT '本记录最后一次修改的时间',
PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;