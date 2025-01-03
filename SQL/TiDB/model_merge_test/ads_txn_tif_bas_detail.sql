DROP TABLE IF EXISTS `ads_txn_tif_bas_detail`;
CREATE TABLE `ads_txn_tif_bas_detail`
(
    UNN_TRNO                             decimal(22, 0) NOT NULL COMMENT '统一交易号',
    BOCGP_INST_NO                        varchar(3)                                       DEFAULT NULL COMMENT '中银集团银行号',
    CTRYW_CLRG_ORG_CODE                  varchar(5)                                       DEFAULT NULL COMMENT '全国清算机构代码',
    TXN_ACCNO                            varchar(18)                                      DEFAULT NULL COMMENT '交易账号',
    TXN_DT                               date           NOT NULL COMMENT '交易日期',
    ORI_RCRD_NO                          varchar(9)                                       DEFAULT NULL COMMENT '原记录号',
    RCRD_NO                              varchar(9)                                       DEFAULT NULL COMMENT '记录号',
    ACC_PRONO                            varchar(5)                                       DEFAULT NULL COMMENT '账户省行机构编号',
    MSAC_ACCNO                           varchar(18)                                      DEFAULT NULL COMMENT '主账户账号',
    TXN_ACCNO_SBACC_TYPE_NO              varchar(4)                                       DEFAULT NULL COMMENT '交易账号子账户类别号',
    TXN_ACCNO_VLMNO                      varchar(3)                                       DEFAULT NULL COMMENT '交易账号册号',
    TXN_ACCNO_SN                         varchar(4)                                       DEFAULT NULL COMMENT '交易账号序号',
    TXN_SN                               varchar(18)                                      DEFAULT NULL COMMENT '交易流水号',
    UUID                                 varchar(32)                                      DEFAULT NULL COMMENT '外国系统UUID',
    PD_LGCLS                             varchar(4)                                       DEFAULT NULL COMMENT '产品大类',
    PD_AS                                varchar(4)                                       DEFAULT NULL COMMENT '产品子类',
    PD_DES                               varchar(100)                                     DEFAULT NULL COMMENT '产品描述',
    DOC_CTR_NO                           varchar(50)                                      DEFAULT NULL COMMENT '文书合同号',
    ACC_ATR                              varchar(1)                                       DEFAULT NULL COMMENT '账户属性',
    CUS_TP                               varchar(2)                                       DEFAULT NULL COMMENT '客户类型',
    ACC_TP                               varchar(3)                                       DEFAULT NULL COMMENT '账户类型',
    CDNO                                 varchar(19)                                      DEFAULT NULL COMMENT '卡号',
    TRNSC                                varchar(2)                                       DEFAULT NULL COMMENT '交易性质',
    TXN_ATR                              varchar(1)                                       DEFAULT NULL COMMENT '交易属性',
    CTIQ_TRTY                            varchar(4)                                       DEFAULT NULL COMMENT 'CTIQ交易类型',
    TRTY_DES                             varchar(50)                                      DEFAULT NULL COMMENT '交易类型描述',
    TXN_TIME                             time                                             DEFAULT NULL COMMENT '交易时间',
    TXN_TLR_REFNO                        varchar(7)                                       DEFAULT NULL COMMENT '交易柜员编号',
    TXN_ORG_REFNO                        varchar(5)                                       DEFAULT NULL COMMENT '交易机构编号',
    TXN_ORG_NAME                         varchar(120)                                     DEFAULT NULL COMMENT '交易机构名称',
    VALUE_DT                             date                                             DEFAULT NULL COMMENT '起息日期',
    BCHPO_SPCL_IDR                       varchar(1)                                       DEFAULT NULL COMMENT '批量处理特殊标记：0—联机交易，1—批量交易拆分时折出的前笔交易，2—批量交易拆分时折出的非首笔交易，3—批屋不涉及',
    ATOMT_CD                             varchar(10)                                      DEFAULT NULL COMMENT '原子交易码',
    ORGNT_CD                             varchar(10)                                      DEFAULT NULL COMMENT '原始交易码',
    PROMP                                varchar(2)                                       DEFAULT NULL COMMENT '提示码',
    PROMP_DES                            varchar(6)                                       DEFAULT NULL COMMENT '提示码描述',
    TXN_CHNL                             varchar(6)                                       DEFAULT NULL COMMENT '交易渠道',
    ORI_TXN_DT                           date                                             DEFAULT NULL COMMENT '原交易日期',
    ORGNT_SN                             varchar(16)                                      DEFAULT NULL COMMENT '原始交易流水号',
    TXN_CURR_TP                          varchar(3)                                       DEFAULT NULL COMMENT '交易货币类型',
    TXN_CURR_ENG_CD                      varchar(3)                                       DEFAULT NULL COMMENT '交易货币英文码',
    TXN_EXRT                             decimal(14, 6)                                   DEFAULT NULL COMMENT '交易汇率',
    DBTCR_IDR                            varchar(1)                                       DEFAULT NULL COMMENT '借贷标识',
    RVRS_IDR                             varchar(1)                                       DEFAULT NULL COMMENT '冲正标识',
    TXN_AMT                              decimal(21, 3)                                   DEFAULT NULL COMMENT '交易金额',
    TXN_AF_BAL                           decimal(21, 3)                                   DEFAULT NULL COMMENT '交易后余额',
    TXN_AF_AVL_BAL                       decimal(21, 3)                                   DEFAULT NULL COMMENT '交易后可用余额',
    FRZ_AMT                              decimal(21, 3)                                   DEFAULT NULL COMMENT '冻结金额',
    OD_QOT                               decimal(21, 3)                                   DEFAULT NULL COMMENT '透支限额',
    AVL_OD_QOT                           decimal(21, 3)                                   DEFAULT NULL COMMENT '可用透支限额',
    DSCT_SSONI_AMT                       decimal(21, 3)                                   DEFAULT NULL COMMENT '贴现息金额',
    RGLR_PNP_BAL                         decimal(21, 3)                                   DEFAULT NULL COMMENT '正常本金余额',
    ODUE_PNP_BAL                         decimal(21, 3)                                   DEFAULT NULL COMMENT '逾期本金余额',
    DRINT_BAL                            decimal(21, 3)                                   DEFAULT NULL COMMENT '欠息余额',
    ODUE_PNP_AMT                         decimal(21, 3)                                   DEFAULT NULL COMMENT '逾期本金金额',
    ODUE_INT_AMT                         decimal(21, 3)                                   DEFAULT NULL COMMENT '逾期利息金额',
    PNP_PNINT_AMT                        decimal(21, 3)                                   DEFAULT NULL COMMENT '本金罚息金额',
    INT_PNINT_AMT                        decimal(21, 3)                                   DEFAULT NULL COMMENT '利息罚息金额',
    PNINT_OF_PNINT_AMT                   decimal(21, 3)                                   DEFAULT NULL COMMENT '罚息的罚息金额',
    RMRK                                 varchar(1000)                                    DEFAULT NULL COMMENT '备注',
    PURP                                 varchar(200)                                     DEFAULT NULL COMMENT '用途',
    MEMO                                 varchar(225)                                     DEFAULT NULL COMMENT '附言',
    SUMM                                 varchar(140)                                     DEFAULT NULL COMMENT '摘要',
    CSHEX_CHAR                           varchar(1)                                       DEFAULT NULL COMMENT '钞汇性质',
    CSHEX_CHAR_DES                       varchar(6)                                       DEFAULT NULL COMMENT '钞汇性质描述',
    NOMPR_ACNM                           varchar(200)                                     DEFAULT NULL COMMENT '名义付款人账户名称',
    NOMPR_CRDNO                          varchar(19)                                      DEFAULT NULL COMMENT '名义付款人卡号',
    NOMPR_ACCNO                          varchar(35)                                      DEFAULT NULL COMMENT '名义付款人账号',
    NOMPR_SBACC_TYPE_NO                  varchar(4)                                       DEFAULT NULL COMMENT '名义付款人子账户类别号',
    NOMPR_VLMNO                          varchar(3)                                       DEFAULT NULL COMMENT '名义付款人册号',
    NOMPR_SN                             varchar(2)                                       DEFAULT NULL COMMENT '名义付款人序号',
    NOMPR_ACC_BLNG_ORGNM                 varchar(210)                                     DEFAULT NULL COMMENT '名义付款人账户所属机构名称',
    NOMPR_ACC_BLNG_ORGNO                 varchar(14)                                      DEFAULT NULL COMMENT '名义付款人账户所属机构号',
    NOMPR_ACC_CTRYW_CLRG_ORG_CODE        varchar(5)                                       DEFAULT NULL COMMENT '名义付款人账户全国清算机构代码',
    ACPAYR_ACNM                          varchar(200)                                     DEFAULT NULL COMMENT '真实付款人账户名称',
    ACPAYR_CRDNO                         varchar(19)                                      DEFAULT NULL COMMENT '真实付款人卡号',
    ACPAYR_ACCNO                         varchar(35)                                      DEFAULT NULL COMMENT '真实付款人账号',
    ACPR_SBACC_TYPE_NO                   varchar(4)                                       DEFAULT NULL COMMENT '赢实付款人子账户类别号',
    ACPR_VLMNO                           varchar(3)                                       DEFAULT NULL COMMENT '真实付款人册号',
    ACPR_SN                              varchar(2)                                       DEFAULT NULL COMMENT '真实付款人障号',
    ACPR_ACC_BLNG_ORGNM                  varchar(210)                                     DEFAULT NULL COMMENT '真实付款人账户所属机构名称',
    ACPR_ACC_BLNG_ORGNO                  varchar(14)                                      DEFAULT NULL COMMENT '真实付歌人账户所用机构号',
    ACPAYR_ACC_CTRYW_CLRG_ORG_CODE       varchar(5)                                       DEFAULT NULL COMMENT '真实付款人账户全国清算机构代码',
    NOMPE_ACNM                           varchar(200)                                     DEFAULT NULL COMMENT '名义收款人账户名称',
    NOMPE_CRDNO                          varchar(19)                                      DEFAULT NULL COMMENT '名义收款人卡号',
    NOMPE_ACCNO                          varchar(35)                                      DEFAULT NULL COMMENT '名义收款人账号',
    NOMPE_SBACC_TYPE_NO                  varchar(4)                                       DEFAULT NULL COMMENT '名义收款人子账户类别号',
    NOMPE_VLMNO                          varchar(3)                                       DEFAULT NULL COMMENT '名义收款人册号',
    NOMPE_SN                             varchar(2)                                       DEFAULT NULL COMMENT '名义收款人序号',
    NOMPE_ACC_BLNG_ORGNM                 varchar(210)                                     DEFAULT NULL COMMENT '名义收款人账户所属机构名称',
    NOMPE_ACC_BLNG_ORGNO                 varchar(14)                                      DEFAULT NULL COMMENT '名义收款人账户所属机构号',
    NOMPE_ACC_CTRYW_CLRG_ORG_CODE        varchar(5)                                       DEFAULT NULL COMMENT '名义收款人账户全国清算机构代码',
    ACPAYE_ACNM                          varchar(200)                                     DEFAULT NULL COMMENT '真实收款人账户名称',
    ACPAYE_CRDNO                         varchar(19)                                      DEFAULT NULL COMMENT '真实收款人卡号',
    ACPAYE_ACCNO                         varchar(35)                                      DEFAULT NULL COMMENT '真实收款人账号',
    ACPE_SBACC_TYPE_NO                   varchar(4)                                       DEFAULT NULL COMMENT '赢实收款人子账户类别号',
    ACPE_VLMNO                           varchar(3)                                       DEFAULT NULL COMMENT '真实收款人册号',
    ACPE_SN                              varchar(2)                                       DEFAULT NULL COMMENT '真实收款人障号',
    ACPE_ACC_BLNG_ORGNM                  varchar(210)                                     DEFAULT NULL COMMENT '真实收款人账户所属机构名称',
    ACPE_ACC_BLNG_ORGNO                  varchar(14)                                      DEFAULT NULL COMMENT '真实收歌人账户所用机构号',
    ACPAYE_ACC_CTRYW_CLRG_ORG_CODE       varchar(5)                                       DEFAULT NULL COMMENT '真实收款人账户全国清算机构代码',
    VCHR_TP                              varchar(4)                                       DEFAULT NULL COMMENT '凭证类型',
    VCHR_TP_DES                          varchar(30)                                      DEFAULT NULL COMMENT '凭证类型描述',
    VCHR_NO                              varchar(40)                                      DEFAULT NULL COMMENT '凭证号码',
    GEN_VCHR_TP                          varchar(4)                                       DEFAULT NULL COMMENT '产生凭证类型',
    GEN_VCHR_TP_DES                      varchar(30)                                      DEFAULT NULL COMMENT '产生凭证类型描述',
    GEN_VCHR_NO                          varchar(40)                                      DEFAULT NULL COMMENT '产生凭证号码',
    EPS_ACCNO                            varchar(35)                                      DEFAULT NULL COMMENT '费用账号',
    EPS_ACCNO_SBACC_TYPE_NO              varchar(4)                                       DEFAULT NULL COMMENT '费用账号子账户类别号',
    EPS_DNUM                             int(22)                                          DEFAULT NULL COMMENT '费用笔数',
    RCVB_EPS_TOT_AMT                     decimal(21, 3)                                   DEFAULT NULL COMMENT '应收费用总金额',
    PRFT_AMT                             decimal(21, 3)                                   DEFAULT NULL COMMENT '优惠金额',
    RMTR_ADDR_AND_TEL                    varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '汇款人地址及电话',
    RPPER_ADDR_AND_TEL                   varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '收款人地址及电话',
    BSN_REFNO                            varchar(64)                                      DEFAULT NULL COMMENT '业务编号',
    CNTPT_BSN_REFNO                      varchar(64)                                      DEFAULT NULL COMMENT '对方业务编号',
    RMBK_BSN_REFNO                       varchar(64)                                      DEFAULT NULL COMMENT '汇款行业务编号',
    RCPTB_BSN_REFNO                      varchar(64)                                      DEFAULT NULL COMMENT '收款行业务编号',
    CUS_BSN_REFNO                        varchar(40)                                      DEFAULT NULL COMMENT '客户业务编号',
    CUS_APLNO                            varchar(40)                                      DEFAULT NULL COMMENT '客户申请号',
    SYNM                                 varchar(6)                                       DEFAULT NULL COMMENT '系统名称',
    CUS_WTL_TLN                          varchar(5)                                       DEFAULT NULL COMMENT '客户所在行行号',
    MSGDT                                date                                             DEFAULT NULL COMMENT '报文日期',
    OLDLN_ORGNO                          varchar(4)                                       DEFAULT NULL COMMENT '旧线机构号',
    TXN_IDR_CD                           varchar(30)                                      DEFAULT NULL COMMENT '交易标识码',
    GTHR_RDCT_TP                         varchar(1)                                       DEFAULT NULL COMMENT '归集还原类型',
    TRIPT_IDR                            varchar(1)                                       DEFAULT NULL COMMENT '三方标识',
    CASH_VIRT_TXN_TRGR_ACCNO             varchar(18)                                      DEFAULT NULL COMMENT '现金虚交易的触发账号',
    CASH_VIRT_TXN_TRGR_ACCNO_PNBRN_CNAPS varchar(5)                                       DEFAULT NULL COMMENT '现金虚交易的触发账号的省行号',
    TXN_SEQ_NO                           varchar(16)                                      DEFAULT NULL COMMENT '交易顺序号（交易流水号／时间戳）',
    MSS_FLAG                             varchar(16)                                      DEFAULT NULL COMMENT '缺失标志',
    CNTPT_ACCNO                          varchar(35)                                      DEFAULT NULL COMMENT '对方账号',
    CNTPT_ACNM                           varchar(200)                                     DEFAULT NULL COMMENT '对方账户名称',
    LAST_MNPLT_TIME                      datetime                                         DEFAULT NULL COMMENT '最后操作时间',
    LAST_MOD_TLR                         varchar(16)                                      DEFAULT NULL COMMENT '最后修改柜员',
    LAST_MNPLT_IDR                       varchar(2)                                       DEFAULT NULL COMMENT '最后操作标识：01联机加工，02批量补入，03批量补齐',
    RSVFD_01                             varchar(70)                                      DEFAULT NULL COMMENT '预留项1',
    RSVFD_02                             varchar(70)                                      DEFAULT NULL COMMENT '预留项2',
    RSVFD_03                             varchar(70)                                      DEFAULT NULL COMMENT '预留硕3',
    RSVFD_04                             varchar(70)                                      DEFAULT NULL COMMENT '预留项4',
    RLTV_KVAL                            varchar(50)                                      DEFAULT NULL COMMENT '关联键值',
    PROMP_LONG_DES                       varchar(100)                                     DEFAULT NULL COMMENT '提示码长描述',
    NRA_IDR_KVAL_ACCNO                   varchar(1)                                       DEFAULT NULL COMMENT 'NRA标识-键值账号',
    NRA_IDR_NOMPR                        varchar(1)                                       DEFAULT NULL COMMENT 'NRA标识-名义付款人',
    NRA_IDR_ACPR                         varchar(1)                                       DEFAULT NULL COMMENT 'NRA标识-真实付款人',
    NRA_IDR_NOMPE                        varchar(1)                                       DEFAULT NULL COMMENT 'NRA标识-名义收款人',
    NRA_IDR_ACPE                         varchar(1)                                       DEFAULT NULL COMMENT 'NRA标识-真实收款人',
    CUSNO                                varchar(10)                                      DEFAULT NULL COMMENT '客户号',
    INTBK_IDR                            varchar(1)                                       DEFAULT NULL COMMENT '跨行标识',
    DIFFP_IDR                            varchar(1)                                       DEFAULT NULL COMMENT '异地标识',
    EXPD_INF                             varchar(8000)                                    DEFAULT NULL COMMENT '扩展信息',
    GLBL_SN                              varchar(34)                                      DEFAULT NULL COMMENT '全局序列号',
    GLBL_BSN_TRCK_NO                     varchar(200)                                     DEFAULT NULL COMMENT '全局业务跟踪号',
    SBMSN_SCN_REFNO                      varchar(8)                                       DEFAULT NULL COMMENT '报送场景编号',
    TXN_COMPL_FLAG                       varchar(200)                                     DEFAULT NULL COMMENT '交易完整性标志 Y-完整／业务跟踪子号-不完整',
    TRCD                                 varchar(10)                                      DEFAULT NULL COMMENT '交易码',
    CORP_PRVT_IDR                        varchar(1)                                       DEFAULT NULL COMMENT '对公对私标识C-对公／P-对私',
    TRAN_KEY                             varchar(100)   NOT NULL,
    SRC_SYS                              varchar(4)                                       DEFAULT NULL,
    SRC_MOD                              varchar(4)                                       DEFAULT NULL,
    POST_DATE                            varchar(8)                                       DEFAULT NULL,
    ACTUAL_DATE                          varchar(8)                                       DEFAULT NULL,
    ACTUAL_TIME                          varchar(8)                                       DEFAULT NULL,
    CLASSIFY_ID                          varchar(4)                                       DEFAULT NULL,
    TRAN_CAT                             varchar(1)                                       DEFAULT NULL,
    IS_SHOW                              varchar(1)                                       DEFAULT NULL,
    NWK_TYP                              varchar(4)                                       DEFAULT NULL,
    TRAN_ID                              varchar(20)                                      DEFAULT NULL,
    VIR_CARD                             varchar(28)                                      DEFAULT NULL,
    OPP_CUSM_NO                          varchar(17)                                      DEFAULT NULL,
    OPP_CARD_NO_TYPE                     varchar(1)                                       DEFAULT NULL,
    OPP_TRAN_AMOUNT                      decimal(21, 3)                                   DEFAULT NULL,
    OPP_TRAN_CURE_EN                     varchar(3)                                       DEFAULT NULL,
    OPP_REMIT_TYPE                       varchar(1)                                       DEFAULT NULL,
    OPP_REMIT_TYPE_DESC                  varchar(6)                                       DEFAULT NULL,
    TRAN_AMOUNT_CNY                      decimal(21, 3)                                   DEFAULT NULL,
    MER_NO                               varchar(15)                                      DEFAULT NULL,
    MER_NAME                             varchar(30)                                      DEFAULT NULL,
    MCC                                  varchar(4)                                       DEFAULT NULL,
    STATUS                               varchar(1)                                       DEFAULT NULL,
    ERR_MSG                              varchar(100)                                     DEFAULT NULL,
    REMARK                               varchar(160)                                     DEFAULT NULL,
    CUST_EDIT_STAT                       varchar(1)                                       DEFAULT NULL,
    SUM_FLAG                             varchar(1)                                       DEFAULT NULL,
    EDIT_FLAG                            varchar(1)                                       DEFAULT NULL,
    OL_TIME                              datetime                                         DEFAULT NULL,
    BAT_TIME                             datetime                                         DEFAULT NULL,
    FINAL_TIME                           datetime                                         DEFAULT NULL,
    FINAL_SIGN                           varchar(20)                                      DEFAULT NULL,
    HIT_TYPE                             varchar(140)                                     DEFAULT NULL,
    HIT_REASON                           varchar(140)                                     DEFAULT NULL,
    HIE_PATH                             varchar(100)                                     DEFAULT NULL,
    BOOK_ID                              varchar(100)                                     DEFAULT NULL,
    USE_OBJ                              varchar(2)                                       DEFAULT NULL,
    DAY                                  bigint(20)     not null,
    RES_FIELD                            varchar(10)                                      DEFAULT NULL,
    PRIMARY KEY (UNN_TRNO, TXN_DT) CLUSTERED
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='基础交易信息表_bas+detail';
