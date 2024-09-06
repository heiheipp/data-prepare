package com.heiheipp.common.util;

import com.heiheipp.common.mbg.model.BaseModel;
import com.heiheipp.common.mbg.model.TestTable3;
import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangxi
 * @version 1.0
 * @className DataBuildUtilTest
 * @desc TODO
 * @date 2022/3/9 19:46
 */
public class DataBuildUtilTest {

    @Test
    public void testGenerateCustId() {
        System.out.println("custid=" + DataBuildUtil.generateCustId("11", 9722001, 1, 10));
    }

    @Test
    public void testPercentage() {
        //System.out.println("百分比=" + DataBuildUtil.getPercentge(40, 500));
        System.out.println("百分比=" + aaa(1999, 20000));
    }

    @Test
    public void testGetIntegerRandom() {
        for (int i = 0; i < 100; i++) {
            System.out.println("随机数=" + DataBuildUtil.getIntegerRandom(2));
        }
    }

    private String aaa(long a, long b) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setRoundingMode(RoundingMode.FLOOR);

        return numberFormat.format((float) a / (float) b * 100);
    }

    @Test
    public void testCollections() {
        List<TestTable3> testTable3s = new ArrayList<>();
        List<BaseModel> baseModels = new ArrayList<>();

        TestTable3 testTable31 = new TestTable3();
        testTable31.setvCustId("1111");

        TestTable3 testTable32 = new TestTable3();
        testTable32.setvCustId("2222");

        baseModels.add(testTable31);
        baseModels.add(testTable32);

        Collections.addAll(testTable3s, baseModels.toArray(new TestTable3[0]));

        System.out.println("basemodel = " + baseModels.toString());
        System.out.println("testtable3 = " + Arrays.asList(baseModels.toArray(new TestTable3[0])));
    }
}
