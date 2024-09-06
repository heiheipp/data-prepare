drop table if exists sz_table_1;
create table sz_table_1
(
    BANK_NO                   varchar(3)     not null COMMENT '银行号',
    JIZHANG_DATE              varchar(8)     not null COMMENT '记账日期',
    TRAN_DATE                 varchar(8)     not null COMMENT '交易日期',
    TRAN_TIME                 varchar(8)     not null COMMENT '交易时间',
    TRAN_SEQ                  varchar(9)     not null COMMENT '交易流水',
    FENLU_SEQ                 varchar(2)     not null COMMENT '分录序号',
    JIZHANG_BRANCH            varchar(5)     not null COMMENT '记账机构',
    JIZHANG_CURRENCY          varchar(3)     not null COMMENT '记账币种',
    HESUAN_CODE               varchar(9)     not null COMMENT '核算代码',
    HESUAN_OBJ_CODE           varchar(30)    not null COMMENT '核算对象编号',
    SOURCE_CODE               varchar(3)     not null COMMENT '来源编号',
    CUST_NO                   varchar(16)    not null COMMENT '客户号',
    JIZHANG_AMOUNT            decimal(17, 3) not null COMMENT '记账金额',
    ACCT_NO                   varchar(16)    not null COMMENT '账号',
    TRAN_CODE                 varchar(6)     not null COMMENT '交易码',
    TELLER_NO                 varchar(7)     not null COMMENT '交易柜员',
    primary key (BANK_NO, JIZHANG_DATE, TRAN_DATE, TRAN_TIME, TRAN_SEQ, FENLU_SEQ)
) engine = innodb comment = '会计分录表';
