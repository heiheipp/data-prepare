package com.heiheipp.common.util;

import cn.hutool.core.date.DateUnit;
import org.assertj.core.util.DateUtil;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author zhangxi
 * @version 1.0
 * @className DateUtilTest
 * @desc TODO
 * @date 2022/3/14 9:52
 */
public class DateUtilTest {

    @Test
    public void testBetween() {
        String dateStr1 = "2022-03-14";
        String dateStr2 = "2022-03-01";

        Date date1 = DateUtil.parse(dateStr1);
        Date date2 = DateUtil.parse(dateStr2);

        long days = cn.hutool.core.date.DateUtil.between(date2, date1, DateUnit.DAY);
        System.out.println("days=" + days);
    }

    @Test
    public void testGetTime() {
        String dateStr1 = "2022-03-14";
        String dateStr2 = "2022-03-01";

        Date date1 = DateUtil.parse(dateStr1);
        Date date2 = DateUtil.parse(dateStr2);

//        System.out.println("date1 gettime=" + date1.getTime());
//        System.out.println("date1+0 gettime=" + (cn.hutool.core.date.DateUtil.offsetDay(date1, 1).toString()));
//        System.out.println("date=" + cn.hutool.core.date.DateUtil.date(1647273599999L).toString());
//        System.out.println("now=" + System.currentTimeMillis());

        long time;
        Timestamp timestamp;
        for (int i = 0; i < 10; i++) {
            time = DateTimeUtil.randomLongTime(date1.getTime(),
                    cn.hutool.core.date.DateUtil.offsetDay(date1, 1).getTime() - 1);
            timestamp = new Timestamp(time);
            System.out.println("long time " + i + " = " + time);
            System.out.println("date time " + i + " = " + cn.hutool.core.date.DateUtil.date(time));
            System.out.println("hh:mm:ss = " + cn.hutool.core.date.DateUtil.date(timestamp.getTime()).toTimeStr());
        }
    }
}
