drop table if exists test_table_8;
create table test_table_8
(
    V_ACCOUNT_NUMBER          varchar(50)    not null COMMENT '',
    N_CYCLE_NO                int(5)         not null COMMENT '账单周期号',
    D_CYCLE_START_DATE        date           not null COMMENT '',
    D_CYCLE_END_DATE          date           not null COMMENT '',
    D_CYCLE_DATE              date           not null COMMENT '',
    V_STATEMENT_PRODUCTION_CD varchar(5)     not null COMMENT '',
    N_OPENING_BALANCE         decimal(21, 3) not null COMMENT '',
    N_OPENING_DUE             decimal(21, 3) not null COMMENT '',
    D_OPENING_DUE_DATE        date           not null COMMENT '期初到期日',
    N_DIRECT_DEBIT_AMOUNT     decimal(21, 3) not null COMMENT '',
    primary key (V_ACCOUNT_NUMBER, N_CYCLE_NO)
) engine = innodb comment = '账单周期表';
