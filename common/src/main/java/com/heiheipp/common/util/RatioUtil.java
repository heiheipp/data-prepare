package com.heiheipp.common.util;

import java.util.Arrays;

/**
 * @author zhangxi
 * @version 1.0
 * @className RatioUtil
 * @desc TODO
 * @date 2022/3/16 15:23
 */
public class RatioUtil {

    /**
     * 初始化标志
     */
    private static boolean init = false;

    private static int[] pre;

    private static int total;

    /**
     * 初始化
     * @param w
     */
    private synchronized static void init(int[] w) {
        pre = new int[w.length];
        pre[0] = w[0];
        for (int i = 1; i < w.length; ++i) {
            pre[i] = pre[i - 1] + w[i];
        }
        total = Arrays.stream(w).sum();
    }

    /**
     * 按比例获取随机数
     * @param input
     * @return
     */
    public static int pickIndex(int[] input) {
        if (!init) {
            init(input);
        }

        int x = (int) (Math.random() * total) + 1;
        return binarySearch(x);
    }

    private static int binarySearch(int x) {
        int low = 0, high = pre.length - 1;
        while (low < high) {
            int mid = (high - low) / 2 + low;
            if (pre[mid] < x) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }
}
