drop table if exists test_table_3;
create table test_table_3
(
    V_CUST_ID           varchar(40)  not null COMMENT '客户号（主键）',
    V_CUST_TYPE_CD      varchar(5)   NOT NULL COMMENT '客户类型代码',
    V_INST_CODE         varchar(10)  NOT NULL COMMENT '机构号',
    V_SRC_CUST_REF_CODE varchar(50)  NOT NULL COMMENT '内部客户号',
    V_FIRST_NAME        varchar(100) NOT NULL COMMENT '名字',
    V_LAST_NAME         varchar(100) NOT NULL COMMENT '姓氏',
    V_SEX_CD            varchar(5)   NOT NULL COMMENT '性别',
    V_ADDRESS           varchar(300) NOT NULL COMMENT '地址',
    V_CITY              varchar(100) NOT NULL COMMENT '城市',
    V_COUNTRY           varchar(100) NOT NULL COMMENT '县',
    V_POSTCODE          varchar(20)  NOT NULL COMMENT '邮编',
    V_PHONE             varchar(50)  NOT NULL COMMENT '电话',
    V_MOBILE            varchar(50)  NOT NULL COMMENT '手机',
    V_EMAIL             varchar(100) NOT NULL COMMENT 'email地址',
    V_MARITAL_STATUS_CD varchar(5)   NOT NULL COMMENT '婚姻状况',
    D_BIRTHDAY          date         NOT NULL COMMENT '生日',
    primary key (V_CUST_ID)
) engine = innodb comment = '客户信息表';

create index idx_test_table_3_1 on test_table_3 (V_SRC_CUST_REF_CODE);
