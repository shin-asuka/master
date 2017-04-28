CREATE TABLE `teacher_glory_info` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `priority` int(8) NOT NULL,
  `avatar` varchar(100) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `share_title` varchar(255) NOT NULL,
  `share_description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

