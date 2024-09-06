package com.heiheipp.common.constant;

import cn.hutool.json.JSONUtil;
import com.heiheipp.common.util.DataBuildUtil;

import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author zhangxi
 * @version 1.0
 * @className DataModelConstant
 * @desc TODO
 * @date 2022/3/16 16:46
 */
public class DataModelConstant {

    /**
     * 默认省市代码长度
     */
    public static final int PROVINCE_LENGTH = 2;

    public static final BigDecimal ZERO_AMOUNT = new BigDecimal(0);

    public static final String DEFAULT_BRANCH_NAME = DataBuildUtil.generateMsg("某", 15, "网点");

    public static final String DEFAULT_BRANCH_NO = DataBuildUtil.getRandomWithLength(5);

    public static final String DEFAULT_PROVINCE_LH_NO = DataBuildUtil.getRandomWithLength(5);

    public static final String DEFAULT_PAYER_NAME = DataBuildUtil.generateMsg("某", 2, "付款人");

    public static final String DEFAULT_PAYER_CARD_NO = "1234567890123456789";

    public static final String DEFAULT_PAYER_ACCOUNT = "12345678901234567890001";

    /**
     * 默认子账户类别
     */
    public static final String DEFAULT_ACCOUNT_TYPE = "1111";

    public static final String DEFAULT_RECEIVER_NAME = DataBuildUtil.generateMsg("某", 2,
            "收款人");

    public static final String DEFAULT_RECEIVER_CARD_NO = "9876543210987654321";

    public static final String DEFAULT_RECEIVER_ACCOUNT = "98765432109876543210001";

    public static final String DEFAULT_ADDRESS_PHONE = DataBuildUtil.generateMsg("某", 2, "路")
            + "XXX-XXXXXXXX";

    /**
     * 默认业务编号
     */
    public static final String DEFAULT_BUSINESS_NO = "1122334455";

    /**
     * TEST_TABLE_3
     */
    public static final String TEST_TABLE_3_KEY = "TEST_TABLE_3";

    /**
     * TEST_TABLE_4
     */
    public static final String TEST_TABLE_4_KEY = "TEST_TABLE_4";

    /**
     * TEST_TABLE_5
     */
    public static final String TEST_TABLE_5_KEY = "TEST_TABLE_5";

    /**
     * TEST_TABLE_6
     */
    public static final String TEST_TABLE_6_KEY = "TEST_TABLE_6";

    /**
     * TEST_TABLE_7
     */
    public static final String TEST_TABLE_7_KEY = "TEST_TABLE_7";

    /**
     * TEST_TABLE_8
     */
    public static final String TEST_TABLE_8_KEY = "TEST_TABLE_8";

    /**
     * customer type code
     */
    public static final String DEFAULT_CUST_TYPE_CD = "01";

    /**
     * cust ref code suffix
     */
    public static final String CUST_REF_CODE_SUFFIX = "01";

    /**
     * relation id suffix
     */
    public static final String RELATION_ID_SUFFIX = "99";

    /**
     * ZDJ_TABLE1
     */
    public static final String ZDJ_TABLE1_KEY = "ZDJ_TABLE1";

    /**
     * ZDJ_TABLE2
     */
    public static final String ZDJ_TABLE2_KEY = "ZDJ_TABLE2";

    /**
     * ZDJ_TABLE3
     */
    public static final String ZDJ_TABLE3_KEY = "ZDJ_TABLE3";

    /**
     * ADS_TXN_TIF_BAS_50C
     */
    public static final String ADS_TXN_TIF_BAS_50C_KEY = "ADS_TXN_TIF_BAS_50C";

    /**
     * ADS_TXN_TIF_BAS_100C
     */
    public static final String ADS_TXN_TIF_BAS_100C_KEY = "ADS_TXN_TIF_BAS_100C";

    /**
     * ADS_TXN_TIF_BAS_200C
     */
    public static final String ADS_TXN_TIF_BAS_200C_KEY = "ADS_TXN_TIF_BAS_200C";

    /**
     * ADS_TXN_TIF_BAS_300C
     */
    public static final String ADS_TXN_TIF_BAS_300C_KEY = "ADS_TXN_TIF_BAS_300C";

    /**
     * ADS_TXN_TIF_BAS_400C
     */
    public static final String ADS_TXN_TIF_BAS_400C_KEY = "ADS_TXN_TIF_BAS_400C";

    /**
     * ADS_TXN_TIF_BAS_FOLLOW
     */
    public static final String ADS_TXN_TIF_BAS_FOLLOW_KEY = "ADS_TXN_TIF_BAS_FOLLOW";

    /**
     * ADS_TXN_TIF_BAS
     */
    public static final String ADS_TXN_TIF_BAS_KEY = "ADS_TXN_TIF_BAS";

    /**
     * ADS_TXN_TIF_BAS_DETAIL
     */
    public static final String ADS_TXN_TIF_BAS_DETAIL_KEY = "ADS_TXN_TIF_BAS_DETAIL";

    /**
     * ADS_TXN_TIF_DETAIL
     */
    public static final String ADS_TXN_TIF_DETAIL_KEY = "ADS_TXN_TIF_DETAIL";

    /**
     * 固定map
     */
    private static final SortedMap<Object, Object> SORTED_MAP = new TreeMap<Object, Object>() {
        private static final long serialVersionUID = 1L;

        {
            put("channel", "mobile");
            put("customType", "个人客户");
        }
    };

    /**
     * json字符串
     */
    public static final String DEFAULT_JSON_STRING = JSONUtil.toJsonStr(SORTED_MAP);
}
