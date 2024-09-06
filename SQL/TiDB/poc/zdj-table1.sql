drop table if exists zdj_table1;
create table zdj_table1
(
    BRANCH                    varchar(5)     not null COMMENT '机构号',
    TRAN_SEQ                  varchar(9)     not null COMMENT '交易流水',
    ACCT_NO                   varchar(16)    not null COMMENT '账号',
    AMOUNT                    decimal(17, 3) not null COMMENT '交易金额',
    TRAN_CODE                 varchar(6)     not null COMMENT '交易码',
    TELLER_NO                 varchar(7)     not null COMMENT '交易柜员',
    CUST_NO                   varchar(16)    not null COMMENT '客户号',
    TRAN_DATE                 varchar(8)     not null COMMENT '交易日期',
    TRAN_TIME                 varchar(8)     not null COMMENT '交易时间',
    TELLER_NO1                varchar(7)     not null COMMENT '交易柜员'
) engine = innodb comment = 'table1';
