ALTER TABLE `teacher_pe_comments` ADD COLUMN to_coordinator tinyint(1) DEFAULT 0 comment '0：不选，1：选'
,ADD COLUMN to_coordinator_comment varchar(3000) DEFAULT NULL
,ADD COLUMN teach_trail_class tinyint(1) DEFAULT 0 comment '能否教trail课，1：可以，0：不可以'
,ADD COLUMN state_reason varchar(3000) DEFAULT NULL comment '状态原因'
,ADD COLUMN template_id int(11) DEFAULT 0 comment '对应表teacher_pe_template的id'
,DROP INDEX `idx_application_id`
,ADD UNIQUE KEY `idx_application_id` (`application_id`);


CREATE TABLE `teacher_pe_template` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(100) NOT NULL,
`current` tinyint(1) DEFAULT 0,
`create_time` timestamp default CURRENT_TIMESTAMP,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `teacher_pe_rubric` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`template_id` int(11) NOT NULL,
`name` varchar(100) NOT NULL,
`seq` tinyint DEFAULT 0,
`create_time` timestamp default CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `teacher_pe_section` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`rubric_id` int(11) NOT NULL,
`name` varchar(100) NOT NULL,
`seq` tinyint DEFAULT 0,
`create_time` timestamp default CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
KEY `idx_rubric_id` (`rubric_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `teacher_pe_criteria` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`section_id` int(11) NOT NULL,
`title` varchar(1000) NOT NULL,
`type` varchar(10) NOT NULL COMMENT '取值: input|radio|checkbox|select',
`points` tinyint NOT NULL DEFAULT 0,
`calculated` tinyint(1) NOT NULL DEFAULT 1 COMMENT '取值: 0不参与计算|1参与计算',
`seq` tinyint DEFAULT 0,
`create_time` timestamp default CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
KEY `idx_section_id` (`section_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARSET=utf8;


CREATE TABLE `teacher_pe_option` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`criteria_id` int(11) NOT NULL,
`description` varchar(1000) DEFAULT NULL,
`points` tinyint NOT NULL DEFAULT 0,
`seq` tinyint DEFAULT 0,
`create_time` timestamp default CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
KEY `idx_criteria_id` (`criteria_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `teacher_pe_result` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`application_id` int(11) NOT NULL,
`option_id` int(11) NOT NULL,
`create_time` timestamp default CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_application_id_option_id` (`application_id`,`option_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `teacher_pe_feedback` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`application_id` int(11) NOT NULL,
`candidate` varchar(100) NOT NULL,
`pe` varchar(100) NOT NULL,
`friendly` varchar(100) NOT NULL,
`instructions` varchar(100) NOT NULL,
`helpful` varchar(100) NOT NULL,
`suggestions` varchar(3000) NOT NULL,
`to_mentor` varchar(3000) NOT NULL,
`rate` tinyint NOT NULL DEFAULT 0,
`create_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
KEY `idx_application_id` (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARSET=utf8;