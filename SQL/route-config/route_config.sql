drop table if exists route_config;
create table route_config
(
    DATASOURCE_NAME  varchar(20) not null PRIMARY KEY COMMENT '数据源名称',
    LOWER_VALUE      varchar(8)  not null COMMENT '下限日期',
    UPPER_VALUE      varchar(8)  not null COMMENT '上限日期',
    LOWER_VALUE_TYPE int         not null COMMENT '下限日期类型: 1-相对值, 2-绝对值',
    UPPER_VALUE_TYPE int         not null COMMENT '下限日期类型: 1-相对值, 2-绝对值',
    OVERLAPS         int         not null COMMENT '重叠日期天数',
    STATUS           int         not null COMMENT '配置项状态: 1-正常, 2-失效',
    EFFECTIVE_DATE   varchar(8)  not null COMMENT '生效日期',
    EXPIRE_DATE      varchar(8)  not null COMMENT '失效日期',
    ORDER_BY_DATE    int         not null COMMENT '配置项日期排序'
) engine = innodb comment = '路由配置表';

insert into route_config values ('db1', '-365', '0', 1, 1, 10, 1, '20220101', '99999999', 1);
insert into route_config values ('db2', '20200101', '-365', 2, 1, 10, 1, '20220101', '99999999', 2);
insert into route_config values ('db3', '20180101', '20191231', 2, 2, 10, 1, '20220101', '99999999', 2);