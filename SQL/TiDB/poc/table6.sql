drop table if exists test_table_6;
create table test_table_6
(
    V_CARD_APP_ID       varchar(50) not null COMMENT '卡应用id',
    N_CARD_APP_ISSUE_ID int(5)      not null COMMENT '卡应用发行id',
    V_ACCOUNT_NUMBER    varchar(50) not null COMMENT '账号',
    V_INST_CODE         varchar(10) not null COMMENT '',
    V_CARD_ID           varchar(50) not null COMMENT '',
    N_CARD_ISSUE_ID     int(5)      not null COMMENT '信用卡发卡id',
    V_CARD_NUMBER       varchar(50) not null COMMENT '',
    D_START_DATE        date        not null COMMENT '',
    D_END_DATE          date        not null COMMENT '',
    primary key (V_CARD_APP_ID, N_CARD_APP_ISSUE_ID)
) engine = innodb comment = '卡应用信息表';

create index idx_test_table_6_1 on test_table_6 (V_CARD_ID);
