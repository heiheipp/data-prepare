drop table if exists test_table_7;
create table test_table_7
(
    V_ACCOUNT_NUMBER         varchar(50)    not null COMMENT '',
    V_INST_CODE              varchar(10)    not null COMMENT '',
    V_CARD_PRODUCT_CODE      varchar(20)    not null COMMENT '',
    V_ACCT_TYPE_CD           varchar(20)    not null COMMENT '',
    V_AGGREEMENT_NUMBER      varchar(50)    not null COMMENT '',
    V_CUST_REF_CODE          varchar(50)    not null COMMENT '',
    V_CCY_CODE               char(3)        not null COMMENT '',
    V_MANUAL_STATUS_CD       varchar(5)     not null COMMENT '',
    D_MANUAL_STATUS_SET_DATE date           not null COMMENT '',
    V_AUTO_STATUS_CD         varchar(5)     not null COMMENT '',
    D_AUTO_STATUS_SET_DATE   date           not null COMMENT '',
    D_OPEN_DATE              date           not null COMMENT '',
    V_CORPORATE_ACCT_FLAG    char(1)        not null COMMENT '',
    V_EXT_ACCOUNT_NUMBER     varchar(50)    not null COMMENT '',
    V_EXT_ACCOUNT_NAME       varchar(300)   not null COMMENT '',
    N_CARD_NUMBER            int(5)         not null COMMENT '',
    N_CUR_BOOK_BAL           decimal(21, 3) not null COMMENT '',
    N_HIGHEST_BAL            decimal(21, 3) not null COMMENT '',
    N_CUR_DEBIT_INTEREST     decimal(21, 3) not null COMMENT '',
    N_CUR_CREDIT_INTEREST    decimal(21, 3) not null COMMENT '',
    N_PRE_DELINQUENCY_DAYS   int(5)         not null COMMENT '',
    N_CUR_CYCLE_NO           int(5)         not null COMMENT '当前最新周期号',
    N_CUR_STATEMENT_NO       int(5)         not null COMMENT '',
    V_ACCT_REF_NO            varchar(50)    not null COMMENT '账户关联号',
    D_LAST_AUTOPAYMENT_DATE  date           not null COMMENT '',
    primary key (V_ACCOUNT_NUMBER, V_INST_CODE)
) engine = innodb comment = '账户信息表';

create index idx_test_table_7_1 on test_table_7 (N_CUR_CYCLE_NO);
