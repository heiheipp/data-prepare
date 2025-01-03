DROP TABLE IF EXISTS `test_table_1`;
CREATE TABLE `test_table_1`
(
    `column_1`   decimal(20, 0) NOT NULL COMMENT '统一交易号（主键）',
    `column_2`   varchar(3) COMMENT '银行号',
    `column_3`   varchar(5) COMMENT '省行联行号',
    `column_4`   varchar(18) COMMENT '交易账号',
    `column_5`   datetime           not null COMMENT '交易日期（主键）',
    `column_6`   varchar(9) COMMENT '原记录号',
    `column_7`   varchar(9) COMMENT '记录号',
    `column_8`   varchar(5) COMMENT '账户所属省行机构号',
    `column_9`   varchar(18) COMMENT '主账户账号',
    `column_10`  varchar(4) COMMENT '交易账号子账户类别',
    `column_11`  int(3) COMMENT '交易账号册号',
    `column_12`  int(2) COMMENT '交易账号序号',
    `column_13`  varchar(12) COMMENT '交易流水号',
    `column_14`  varchar(32) COMMENT '外围系统UUID',
    `column_15`  varchar(4) COMMENT '产品大类',
    `column_16`  varchar(4) COMMENT '产品子类',
    `column_17`  varchar(30) COMMENT '产品描述',
    `column_18`  varchar(50) COMMENT '文书合同号',
    `column_19`  varchar(1) COMMENT '账户属性',
    `column_20`  varchar(2) COMMENT '客户类别',
    `column_21`  varchar(3) COMMENT '账户类别',
    `column_22`  varchar(19) COMMENT '卡号',
    `column_23`  varchar(2) COMMENT '交易性质',
    `column_24`  varchar(1) COMMENT '交易属性',
    `column_25`  varchar(4) COMMENT '交易类型',
    `column_26`  varchar(4) COMMENT '交易类型描述',
    `column_27`  varchar(8) COMMENT '交易时间',
    `column_28`  varchar(7) COMMENT '交易柜员',
    `column_29`  varchar(5) COMMENT '交易机构号',
    `column_30`  varchar(70) COMMENT '交易机构名称',
    `column_31`  date COMMENT '起息日期',
    `column_32`  varchar(1) COMMENT '批量处理特殊标记',
    `column_33`  varchar(6) COMMENT '原子交易码',
    `column_34`  varchar(6) COMMENT '原始交易码',
    `column_35`  varchar(2) COMMENT '提示码',
    `column_36`  varchar(6) COMMENT '提示码描述',
    `column_37`  varchar(2) COMMENT '交易渠道',
    `column_38`  date COMMENT '原交易日期',
    `column_39`  varchar(12) COMMENT '原始交易流水号',
    `column_40`  varchar(3) COMMENT '交易货币类型',
    `column_41`  varchar(3)     not null COMMENT '交易货币英文码',
    `column_42`  decimal(14, 6) not null COMMENT '交易汇率',
    `column_43`  varchar(1)     not null COMMENT '借贷标识',
    `column_44`  varchar(1)     not null COMMENT '冲正标识',
    `column_45`  decimal(21, 3) not null COMMENT '交易金额',
    `column_46`  decimal(21, 3) not null COMMENT '交易后金额',
    `column_47`  decimal(21, 3) not null COMMENT '交易后可用金额',
    `column_48`  decimal(21, 3) not null COMMENT '冻结金额',
    `column_49`  decimal(21, 3) not null COMMENT '透支限额',
    `column_50`  decimal(21, 3) not null COMMENT '可用透支金额',
    `column_51`  decimal(21, 3) not null COMMENT '贴现息金额',
    `column_52`  decimal(21, 3) not null COMMENT '正常本金余额',
    `column_53`  decimal(21, 3) not null COMMENT '逾期本金余额',
    `column_54`  decimal(21, 3) not null COMMENT '欠息余额',
    `column_55`  decimal(21, 3) not null COMMENT '逾期本金金额',
    `column_56`  decimal(21, 3) not null COMMENT '逾期利息金额',
    `column_57`  decimal(21, 3) not null COMMENT '本金罚息金额',
    `column_58`  decimal(21, 3) not null COMMENT '利息罚息金额',
    `column_59`  decimal(21, 3) not null COMMENT '罚息的罚息金额',
    `column_60`  varchar(160)   not null COMMENT '备注',
    `column_61`  varchar(80)    not null COMMENT '用途',
    `column_62`  varchar(225)   not null COMMENT '附言',
    `column_63`  varchar(140)   not null COMMENT '摘要',
    `column_64`  varchar(1)     not null COMMENT '钞汇性质',
    `column_65`  varchar(6)     not null COMMENT '钞汇性质描述',
    `column_66`  varchar(140)   not null COMMENT '名义付款人账户名称',
    `column_67`  varchar(19)    not null COMMENT '名义付款人卡号',
    `column_68`  varchar(35)    not null COMMENT '名义付款人账号',
    `column_69`  varchar(4)     not null COMMENT '名义付款人子账户类别号',
    `column_70`  int(3)         not null COMMENT '名义付款人账号册号',
    `column_71`  int(2)         not null COMMENT '名义付款人序号',
    `column_72`  varchar(80)    not null COMMENT '名义付款人账户所属机构名称',
    `column_73`  varchar(14)    not null COMMENT '名义付款人账户所属机构号',
    `column_74`  varchar(5)     not null COMMENT '名义付款人账户省联行号',
    `column_75`  varchar(140)   not null COMMENT '真实付款人账户名称',
    `column_76`  varchar(19)    not null COMMENT '真实付款人卡号',
    `column_77`  varchar(35)    not null COMMENT '真实付款人账号',
    `column_78`  varchar(4)     not null COMMENT '真实付款人子账户类别号',
    `column_79`  int(3)         not null COMMENT '真实付款人账号册号',
    `column_80`  int(2)         not null COMMENT '真实付款人序号',
    `column_81`  varchar(80)    not null COMMENT '真实付款人账户所属机构名称',
    `column_82`  varchar(14)    not null COMMENT '真实付款人账户所属机构号',
    `column_83`  varchar(5)     not null COMMENT '真实付款人账户省联行号',
    `column_84`  varchar(140)   not null COMMENT '名义收款人账户名称',
    `column_85`  varchar(19)    not null COMMENT '名义收款人卡号',
    `column_86`  varchar(35)    not null COMMENT '名义收款人账号',
    `column_87`  varchar(4)     not null COMMENT '名义收款人子账户类别号',
    `column_88`  int(3)         not null COMMENT '名义收款人账号册号',
    `column_89`  int(2)         not null COMMENT '名义收款人序号',
    `column_90`  varchar(80)    not null COMMENT '名义收款人账户所属机构名称',
    `column_91`  varchar(14)    not null COMMENT '名义收款人账户所属机构号',
    `column_92`  varchar(5)     not null COMMENT '名义收款人账户省联行号',
    `column_93`  varchar(140)   not null COMMENT '真实收款人账户名称',
    `column_94`  varchar(19)    not null COMMENT '真实收款人卡号',
    `column_95`  varchar(35)    not null COMMENT '真实收款人账号',
    `column_96`  varchar(4)     not null COMMENT '真实收款人子账户类别号',
    `column_97`  int(3)         not null COMMENT '真实收款人账号册号',
    `column_98`  int(2)         not null COMMENT '真实收款人序号',
    `column_99`  varchar(80)    not null COMMENT '真实收款人账户所属机构名称',
    `column_100` varchar(14)    not null COMMENT '真实收款人账户所属机构号',
    `column_101` varchar(5)     not null COMMENT '真实收款人账户省联行号',
    `column_102` varchar(4)     not null COMMENT '凭证类型',
    `column_103` varchar(30)    not null COMMENT '凭证类型描述',
    `column_104` varchar(40)    not null COMMENT '凭证号码',
    `column_105` varchar(4)     not null COMMENT '产生凭证类型',
    `column_106` varchar(30)    not null COMMENT '产生凭证类型描述',
    `column_107` varchar(40)    not null COMMENT '产生凭证号码',
    `column_108` varchar(35)    not null COMMENT '费用账号',
    `column_109` varchar(4)     not null COMMENT '费用账号子账户类别号',
    `column_110` int(2)         not null COMMENT '费用笔数',
    `column_111` decimal(21, 3) not null COMMENT '应收费用总金额',
    `column_112` decimal(21, 3) not null COMMENT '优惠金额',
    `column_113` varchar(256)   not null COMMENT '汇款人地址及电话',
    `column_114` varchar(256)   not null COMMENT '收款人地址及电话',
    `column_115` varchar(64)    not null COMMENT '业务编号',
    `column_116` varchar(64)    not null COMMENT '对方业务编号',
    `column_117` varchar(64)    not null COMMENT '汇款行业务编号',
    `column_118` varchar(64)    not null COMMENT '收款行业务编号',
    `column_119` varchar(64)    not null COMMENT '客户业务编号',
    `column_120` varchar(40)    not null COMMENT '客户申请号',
    `column_121` varchar(6)     not null COMMENT '系统名称',
    `column_122` varchar(5)     not null COMMENT '客户所在行行号',
    `column_123` date           not null COMMENT '报文日期',
    `column_124` varchar(4)     not null COMMENT '旧线机构号',
    `column_125` varchar(30)    not null COMMENT '交易标识码',
    `column_126` varchar(1)     not null COMMENT '归集还原类型',
    `column_127` varchar(1)     not null COMMENT '三方标识',
    `column_128` varchar(18)    not null COMMENT '现金虚交易的触发账号',
    `column_129` varchar(5)     not null COMMENT '现金虚交易的触发账号的省行号',
    `column_130` varchar(16)    not null COMMENT '交易顺序号（交易流水号/时间戳）',
    `column_131` varchar(16)    not null COMMENT '缺失标志',
    `column_132` varchar(35)    not null COMMENT '对方账户',
    `column_133` varchar(140)   not null COMMENT '对方账户名称',
    `column_134` date           not null COMMENT '最后操作时间',
    `column_135` varchar(16)    not null COMMENT '最后修改柜员',
    `column_136` varchar(2)     not null COMMENT '最后操作标识:01-联机加工，02-批量补入，03-批量补齐',
    `column_137` varchar(50)    not null COMMENT '关联键值',
    `column_138` varchar(10)    not null COMMENT '提示码长描述',
    `column_139` varchar(1)     not null COMMENT 'NRA标识-键值账号',
    `column_140` varchar(1)     not null COMMENT 'NRA标识-名义付款人',
    `column_141` varchar(1)     not null COMMENT 'NRA标识-真实付款人',
    `column_142` varchar(1)     not null COMMENT 'NRA标识-名义收款人',
    `column_143` varchar(1)     not null COMMENT 'NRA标识-真实收款人',
    `column_144` varchar(16)    not null COMMENT '客户号',
    `column_145` varchar(1)     not null COMMENT '跨行标识',
    `column_146` varchar(1)     not null COMMENT '异地标识',
    `column_147` varchar(70) COMMENT '预留项1',
    `column_148` varchar(70) COMMENT '预留项2',
    `column_149` varchar(70) COMMENT '预留项3',
    `column_150` varchar(70) COMMENT '预留项4',
    `column_151` varchar(1000) COMMENT '保留扩展区',
    PRIMARY KEY (column_144, column_5, column_1) CLUSTERED
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='交易基础信息表';