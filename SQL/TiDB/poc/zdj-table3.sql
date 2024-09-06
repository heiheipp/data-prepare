drop table if exists zdj_table3;
create table zdj_table3
(
    BRANCH                    varchar(5)     not null COMMENT '机构号',
    ACCT_NO                   varchar(16)    not null COMMENT '账号',
    PRODUCT_NO                varchar(9)     not null COMMENT '可售产品编号',
    CURRENCY                  varchar(3)     not null COMMENT '币种',
    TERM                      varchar(2)     not null COMMENT '期限',
    INST                      decimal(6, 3)  not null COMMENT '利率',
    SIGN_DATE                 varchar(8)     not null COMMENT '合约签署日期',
    STATUS                    varchar(6)     not null COMMENT '余额'
) engine = innodb comment = 'table3';