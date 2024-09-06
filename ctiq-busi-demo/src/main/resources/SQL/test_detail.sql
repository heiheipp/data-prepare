drop table if exists test_detail;
create table test_detail
(
    UNN_TRNO      int         NOT NULL PRIMARY KEY COMMENT '统一交易号',
    TXN_DT        date        NOT NULL COMMENT '交易日期',
    TXN_ACCNO     varchar(18) NOT NULL COMMENT '交易账号',
    SUMMRY        varchar(50) NOT NULL COMMENT '交易摘要'
) engine = innodb comment = '明细测试表';

# ds_0
insert into test_detail values(1, '2021-12-28', '11111111', '消费');
insert into test_detail values(2, '2022-02-10', '11111111', '消费');
insert into test_detail values(3, '2022-05-06', '11111111', '消费');
insert into test_detail values(4, '2022-06-09', '11111111', '消费');
insert into test_detail values(5, '2022-06-23', '11111111', '消费');
insert into test_detail values(6, '2022-08-11', '11111111', '消费');
insert into test_detail values(7, '2022-09-30', '11111111', '消费');
insert into test_detail values(8, '2022-10-03', '11111111', '消费');
insert into test_detail values(9, '2022-11-06', '11111111', '消费');
insert into test_detail values(10, '2022-12-20', '11111111', '消费');

# ds_1
insert into test_detail values(11, '2020-11-28', '11111111', '消费');
insert into test_detail values(12, '2020-12-08', '11111111', '消费');
insert into test_detail values(13, '2021-04-20', '11111111', '消费');
insert into test_detail values(14, '2021-05-01', '11111111', '消费');
insert into test_detail values(15, '2021-06-19', '11111111', '消费');
insert into test_detail values(16, '2021-09-28', '11111111', '消费');
insert into test_detail values(17, '2021-10-02', '11111111', '消费');
insert into test_detail values(18, '2021-10-22', '11111111', '消费');