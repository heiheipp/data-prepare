//package com.heiheipp.dataprepare.util;
//
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.Random;
//import java.util.UUID;
//
//public class DataBuildUtil {
//
//    private static String[] maritalstatus = {"0", "1", "5", "6"};
//    private static String[] instcode = {"111111", "222222", "333333", "444444","555555","666666"};
//    private static String charStr = "abcdefghijklmnopqrstuvwxyz";
//    private static String numbers = "0123456789";
//    private static int[] year = {1999, 2000, 2001, 2002, 2003, 2004, 2005};
//    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//
//    //for cust_tran_info table
//    private static String[] classifyid = {"0001", "0002", "0003", "0004"};
//
//    //for cust_employ_info table
//    private static String[] occucode = {"01", "02", "03", "04", "05", "06"};
//    private static String[] emprinds = {"01", "02", "03", "04", "05", "06"};
//
//    public static CustTranInfo createCustTranInfo(CustBasicInfo custBasicInfo){
//        CustTranInfo tranInfo = new CustTranInfo();
//        tranInfo.setTrankey(getUUID());
//        tranInfo.setCustid(custBasicInfo.getCustid());
//        tranInfo.setTrancid(getDigitRandom(16));
//        tranInfo.setTrandate(getRandomDate(year[getIntegerRandom(year.length)]));
//        tranInfo.setClassifyid(classifyid[getIntegerRandom(classifyid.length)]);
//        if (tranInfo.getClassifyid().equals("0002")) {
//            tranInfo.setTrancat("1");
//        }else {
//            tranInfo.setTrancat("0");
//        }
//        tranInfo.setCardno(tranInfo.getTrancid());
//        tranInfo.setTranamount(getRandomBigDecimal(1000, 3));
//        tranInfo.setAcctavaibala(tranInfo.getTranamount());
//        return tranInfo;
//    }
//
//    public static String getUUID(){
//        return UUID.randomUUID().toString();
//    }
//
//    public static int getIntegerRandom(int n) {
//        return (int)(Math.random()*n);
//    }
//
//    public static int getScopeIntegerRandom(int min, int max) {
//        Random random = new Random();
//        return random.nextInt(max - min + 1) + min;
//     }
//
//    public static String getCharArray(int length) {
//        Random random = new Random();
//        StringBuffer valSb = new StringBuffer();
//        int charLength = charStr.length();
//        for (int i = 0; i < length; i++) {
//            int index = random.nextInt(charLength);
//            valSb.append(charStr.charAt(index));
//        }
//        return valSb.toString();
//    }
//
//    public static String getDigitRandom(int digit){
//        Random random = new Random();
//        StringBuffer flag = new StringBuffer();
//        for (int j = 0; j < digit; j++) {
//            flag.append(numbers.charAt(random.nextInt(9)) + "");
//        }
//        return flag.toString();
//    }
//
//    public static String getRandomDate(int year) {
//        Random r = new Random();
//        return year + String.format("%02d%02d",
//                new Object[] { r.nextInt(12) + 1, r.nextInt(31) + 1 });
//    }
//
//    public static BigDecimal getRandomBigDecimal(int unscaledValDigit, int scaleDigit){
//        return BigDecimal.valueOf(getIntegerRandom(unscaledValDigit), scaleDigit);
//    }
//
//    public static void main(String[] args) {
//        System.out.println(getIntegerRandom(4));
////        System.out.println(getCharArray(5));
////        System.out.println(""+ (getRandom(2) + 1));
////        System.out.println(getDigitRandom(10));
////        System.out.println(getRandomDate(2019));
//        System.out.println(getRandomBigDecimal(5000000, 2));
//        System.out.println(new BigDecimal(100).add(new BigDecimal(100)).add(new BigDecimal(100)));
//    }
//
//
//}
