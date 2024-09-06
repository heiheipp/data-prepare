drop table if exists test_table_4;
create table test_table_4
(
    V_CARD_ID                varchar(50) not null COMMENT '卡片id',
    V_CARD_ISSUE_ID          varchar(5)  not null COMMENT '信用卡发行id',
    V_INST_CODE              varchar(10) not null COMMENT '机构号',
    V_CARD_MAILER_CONTACT_ID varchar(50) not null COMMENT '卡片邮寄地址编号',
    primary key (V_CARD_ID, V_CARD_ISSUE_ID)
) engine = innodb comment = '卡片信息表';

create index idx_test_table_4_1 on test_table_4 (V_CARD_MAILER_CONTACT_ID);
