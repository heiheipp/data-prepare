package com.heiheipp.common.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.text.ParseException;
import java.util.Date;

/**
 * @author zhangxi
 * @version 1.0
 * @className DateTimeUtil
 * @desc TODO
 * @date 2022/3/14 11:03
 */
public class DateTimeUtil {

    /**
     * 随机long型时间
     *
     * @param begin
     * @param end
     * @return
     */
    public static long randomLongTime(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return randomLongTime(begin, end);
        }
        return rtn;
    }

    /**
     * 计算日期差
     *
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    public static int daysBetween(String startDateStr, String endDateStr) {
        int dayBetween = 0;

        if (!StrUtil.isEmpty(startDateStr) && !StrUtil.isEmpty(endDateStr)) {

            Date start = DateUtil.parse(startDateStr);
            Date end = DateUtil.parse(endDateStr);

            // 日期校验
            if (start.compareTo(end) > 0) {
                throw new RuntimeException("起始日期不能大于终止日期");
            }

            // 计算时间差
            dayBetween = new Long(DateUtil.between(start, end, DateUnit.DAY)).intValue() + 1;
        }

        return dayBetween;
    }
}
