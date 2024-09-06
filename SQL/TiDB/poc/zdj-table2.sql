drop table if exists zdj_table2;
create table zdj_table2
(
    BRANCH                    varchar(5)     not null COMMENT '机构号',
    ACCT_NO                   varchar(16)    not null COMMENT '账号',
    CUST_NO                   varchar(16)    not null COMMENT '客户号',
    PRODUCT_NO                varchar(9)     not null COMMENT '可售产品编号',
    CURRENCY                  varchar(3)     not null COMMENT '币种',
    TERM                      varchar(2)     not null COMMENT '期限',
    END_DATE                  varchar(8)     not null COMMENT '到期日',
    AMOUNT                    decimal(17, 3) not null COMMENT '余额'
) engine = innodb comment = 'table2';