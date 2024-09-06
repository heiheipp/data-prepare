drop table if exists test_table_5;
create table test_table_5
(
    V_RELATION_ID       varchar(50) not null COMMENT '账户客户关系id',
    N_RELATION_SEQ_NO   INT(5)      not null COMMENT '账户客户关系顺序号',
    D_START_DATE        date        not null COMMENT '',
    D_END_DATE          date        not null COMMENT '',
    F_DEL_FLAG          VARCHAR(1)  not null COMMENT '',
    N_NEXT_NO           int(5)      not null COMMENT '',
    V_INST_CODE         varchar(10) not null COMMENT '',
    V_SRC_CUST_REF_CODE varchar(50) not null COMMENT '内部客户号',
    V_CUST_ADDRESS_ID   varchar(50) not null COMMENT '',
    primary key (V_RELATION_ID, N_RELATION_SEQ_NO)
) engine = innodb comment = '账户客户关系表';

create index idx_test_table_5_1 on test_table_5 (V_SRC_CUST_REF_CODE);
