DROP TABLE IF EXISTS `TEST_HTAP2_RULE_001`;
CREATE TABLE `TEST_HTAP2_RULE_001`
(
    DTE            varchar(8)    DEFAULT NULL COMMENT '日期',
    COD_CHK_RULE   varchar(100)  DEFAULT NULL COMMENT '校验规则编码',
    NAM_CHK_RULE   varchar(100)  DEFAULT NULL COMMENT '校验规则名称',
    COD_PJT        varchar(100)  DEFAULT NULL COMMENT '报送项目代码',
    ID_SEQ         int(100)      DEFAULT NULL COMMENT '序号',
    COD_TBL        varchar(100)  DEFAULT NULL COMMENT '对应校验明细表英文名',
    COD_CHK_FLD    varchar(100)  DEFAULT NULL COMMENT '校验字段英文名',
    NAM_CHK_FLD    varchar(100)  DEFAULT NULL COMMENT '校验字段中文名',
    COD_RLN_TAB    varchar(100)  DEFAULT NULL COMMENT '被关联表英文名',
    NAM_RLN_TAB    varchar(100)  DEFAULT NULL COMMENT '被关联表中文名',
    TXT_CHK_TYP    varchar(100)  DEFAULT NULL COMMENT '校验类型',
    TXT_CHK_CTEN   varchar(1000) DEFAULT NULL COMMENT '校验内容',
    TXT_CHK_SQL    varchar(1000) DEFAULT NULL COMMENT '对应SQL',
    COD_WARN_TYP   varchar(64)   DEFAULT NULL COMMENT '预警分类',
    DAT_ENABLE     varchar(8)    DEFAULT NULL COMMENT '生效日期',
    DAT_DISABLE    varchar(8)    DEFAULT NULL COMMENT '失效日期',
    TXT_REMARK     varchar(300)  DEFAULT NULL COMMENT '备注',
    ID_USER        varchar(50)   DEFAULT NULL COMMENT '维护人',
    DTM_MATNC      varchar(19)   DEFAULT NULL COMMENT '维护时间',
    RESERVE_FEILDA varchar(300)  DEFAULT NULL COMMENT '备用字段1',
    RESERVE_FEILDB varchar(300)  DEFAULT NULL COMMENT '备用字段2',
    RESERVE_FEILDC varchar(300)  DEFAULT NULL COMMENT '备用字段3'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='校验规则配置表';