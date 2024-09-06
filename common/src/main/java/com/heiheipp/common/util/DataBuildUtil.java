package com.heiheipp.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author zhangxi
 * @version 1.0
 * @className DataBuildUtil
 * @desc TODO
 * @date 2022/3/8 17:25
 */
public class DataBuildUtil {

    /**
     * 交易类型数组
     */
    private static String[] tranTypeArray = {"0001", "0002", "0003", "0004"};

    /**
     * 借贷标识
     */
    private static String[] debitOrCreditArray = {"C", "D"};

    /**
     * 渠道标识
     */
    private static String[] channelArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                                            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                                            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};

    /**
     * 核算代码
     */
    public static String[] hesuanCodeArray = {"1001", "2001", "3001", "4001", "5001", "6001", "7001",
                                               "8001", "9001", "1101"};

    /**
     * 核算对象编号
     */
    public static String[] hesuanObjArray = {"A01", "A02", "A03", "A04", "A05", "A06", "A07", "A08", "A09", "A10"};

    /**
     * 来源编号
     */
    public static String[] sourceCodeArray = {"001", "002", "003", "004", "005", "006", "007", "008", "009", "010"};

    /**
     * 币种
     */
    public static String[] currencyArray = {"CNY", "EUR", "USD"};

    /**
     * 期限
     */
    public static String[] termArray = {"1Y", "2Y", "3Y", "6M", "3M"};

    /**
     * 交易码
     */
    public static String[] transCodeArray = {"1010", "1045", "1050", "1060"};

    /**
     * 省市代码
     */
    public static String[] provinceCodeArray = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                                                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                                                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                                                "31", "32", "33", "34", "35", "36", "37", "38"};

    /**
     * 生成客户id
     * @param startOffset
     * @param currentOffset
     * @return
     */
    public static String generateCustId(String custIdPrefix, int startOffset, int currentOffset, int custIdFillLength) {
        return custIdPrefix + String.format("%0" + custIdFillLength + "d", startOffset + currentOffset);
    }

    /**
     * 生成客户id
     * @param startOffset
     * @param currentOffset
     * @return
     */
    public static String generateCustId(int randomLength, int startOffset, int currentOffset, int custIdFillLength) {
        return getRandomWithLength(randomLength) + String.format("%0" + custIdFillLength + "d",
                startOffset + currentOffset);
    }

    /**
     * 生成卡号
     * @param cardNoPrefix
     * @param startOffset
     * @param currentOffset
     * @param custIdFillLength
     * @return
     */
    public static String generateCardNo(String cardNoPrefix, int startOffset, int currentOffset, int custIdFillLength) {
        return cardNoPrefix + String.format("%0" + custIdFillLength + "d", startOffset + currentOffset);
    }

    /**
     * 生成交易账号
     * @param accountPrefix
     * @param startOffset
     * @param currentOffset
     * @param accountFillLength
     * @return
     */
    public static String generateAccount(String accountPrefix, int startOffset, int currentOffset,
                                         int accountFillLength) {
        return accountPrefix + String.format("%0" + accountFillLength + "d", startOffset + currentOffset);
    }

    /**
     * 简单顺序序号通用生成方法
     * @param prefix
     * @param startOffset
     * @param currentOffset
     * @param length
     * @return
     */
    public static String generateSeqStr(String prefix, int startOffset, int currentOffset, int length) {
        return prefix + String.format("%0" + length + "d", startOffset + currentOffset);
    }

    /**
     * 生成顺序网点编号
     * @param prefix
     * @param startOffset
     * @param currentOffset
     * @param length
     * @return
     */
    public static String generateBranchNo(String prefix, int startOffset, int currentOffset, int length) {
        return prefix + String.format("%0" + length + "d",
                (startOffset + currentOffset) > 9999 ?
                        subInt(startOffset + currentOffset, 0, length) : startOffset + currentOffset);
    }

    /**
     * 截取整数中的某几位
     * @param origin
     * @param directionType
     * @param length
     * @return
     */
    private static int subInt(int origin, int directionType, int length) {
        String result = Integer.toString(origin);

        // directionType: 1-从前往后, 0-从后往前
        switch (directionType) {
            case 0:
                result = result.substring(result.length() > length ? result.length() - length : 0);
                break;
            case 1:
                result = result.substring(0, result.length() > length ? length : result.length());
                break;
            default:
                break;
        }

        return Integer.parseInt(result);
    }

    /**
     * 生成交易类型
     * @return
     */
    public static String getTranType() {
        return tranTypeArray[getIntegerRandom(tranTypeArray.length)];
    }

    /**
     * 交易类型描述
     * @param tranType
     * @return
     */
    public static String getTranTypeDesc(String tranType) {
        switch (tranType) {
            case "0001":
                return "转出";
            case "0002":
                return "转入";
            case "0003":
                return "支付";
            case "0004":
                return "消费";
            default:
                return "";
        }
    }

    /**
     * 获取随机数
     * @param n
     * @return
     */
    public static int getIntegerRandom(int n) {
        return (int)(Math.random() * n);
    }

    /**
     * 获取交易方向
     * @param tranType
     * @return
     */
    public static String getTranCat(String tranType) {
        switch (tranType) {
            case "0001" :
            case "0003" :
            case "0004" :
                return "0";
            case "0002" :
                return "1";
            default:
                return "";
        }
    }

    /**
     * 获取交易信息
     * @param tranType
     * @return
     */
    public static String getTranMsg(String tranType) {
        switch (tranType) {
            case "0001":
                return "转出";
            case "0002":
                return "转入";
            case "0003":
                return "支付";
            case "0004":
                return "消费";
            default:
                return "";
        }
    }

    /**
     * 获取随机金额
     * @param unscaledValDigit
     * @param scaleDigit
     * @return
     */
    public static BigDecimal getRandomBigDecimal(int unscaledValDigit, int scaleDigit) {
        return BigDecimal.valueOf(getIntegerRandom(unscaledValDigit), scaleDigit);
    }

    /**
     * 计算百分比
     * @param x
     * @param total
     * @return
     */
    public static int getPercentge(long x, long total) {
        //return ((Double) Math.floor((float)x / (float)total * 100.00)).intValue();
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setRoundingMode(RoundingMode.FLOOR);

        return Integer.valueOf(numberFormat.format((float) x / (float) total * 100));
    }

    /**
     * 计算百分比是否约为10的倍数
     * @param x
     * @param total
     * @return
     */
    public static boolean getPercentageWtihTens(long x, long total) {
        return getPercentge(x, total) % 10 == 0;
    }

    /**
     * 按指定位数生成随机数
     * @param len
     * @return
     */
    public static String getRandomWithLength(int len) {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
            rs.append(r.nextInt(10));
        }
        return rs.toString();
    }

    /**
     * 生成中文描述
     * @param prefix
     * @param cycleNum
     * @param suffix
     * @return
     */
    public static String generateMsg(String prefix, int cycleNum, String suffix) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < cycleNum; i++) {
            sb.append(prefix);
        }
        sb.append(suffix);

        return sb.toString();
    }

    /**
     * 生成借贷标识
     * @return
     */
    public static String getDebitOrCreditFlag() {
        return debitOrCreditArray[getIntegerRandom(debitOrCreditArray.length)];
    }

    /**
     * 生成冲正标识
     * @param flag
     * @return
     */
    public static String getChongzhengFlag(String flag) {
        switch (flag) {
            case "C":
                return "0";
            case "D":
                return "1";
            default:
                return "";
        }
    }

    /**
     * 生成渠道标识
     * @return
     */
    public static String getChannel() {
        return channelArray[getIntegerRandom(channelArray.length)];
    }

    /**
     * String类转Time
     * @param strDate
     * */
    public static Time strToTime(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Time time = new Time(d.getTime());
        return time.valueOf(str);
    }
}
