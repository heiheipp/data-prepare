DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log` (
  `parent_id`      varchar (44) NOT NULL,
  `sub_id`         varchar (40) not null DEFAULT '0',
  `task_type`      varchar (100) not null default '',
  `task_content`   varchar (200) not null default '',
  `status`         varchar (2) not null,
  `total_nums`     bigint not null default 0,
  `processed_nums` bigint not null default 0,
  `create_time`    timestamp not null,
  `update_time`    timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`parent_id`, `sub_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='任务执行记录表';