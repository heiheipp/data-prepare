drop table if exists test_table_1;
create table test_table_1
(
    id   int         PRIMARY KEY COMMENT 'id号',
    name varchar(50) not null COMMENT '姓名',
    sex  varchar(8)  not null COMMENT '性别',
    age  int         not null COMMENT '年龄'
) engine = innodb comment = '测试表1';