package com.heiheipp.common.util;

import cn.hutool.core.text.csv.CsvConfig;
import cn.hutool.core.text.csv.CsvWriteConfig;
import cn.hutool.core.util.CharUtil;

import java.io.Serializable;

/**
 * @author zhangxi
 * @version 1.0
 * @className CsvWriterConfigNew
 * @desc TODO
 * @date 2022/3/13 18:42
 */
public class CsvWriterConfigNew extends CsvConfigNew<CsvWriterConfigNew> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否始终使用文本分隔符，文本包装符，默认false，按需添加
     */
    protected boolean alwaysDelimitText;
    /**
     * 换行符
     */
    protected char[] lineDelimiter = {CharUtil.CR, CharUtil.LF};

    /**
     * 默认配置
     *
     * @return 默认配置
     */
    public static CsvWriterConfigNew defaultConfig() {
        return new CsvWriterConfigNew();
    }

    /**
     * 设置是否始终使用文本分隔符，文本包装符，默认false，按需添加
     *
     * @param alwaysDelimitText 是否始终使用文本分隔符，文本包装符，默认false，按需添加
     * @return this
     */
    public CsvWriterConfigNew setAlwaysDelimitText(boolean alwaysDelimitText) {
        this.alwaysDelimitText = alwaysDelimitText;
        return this;
    }

    /**
     * 设置换行符
     *
     * @param lineDelimiter 换行符
     * @return this
     */
    public CsvWriterConfigNew setLineDelimiter(char[] lineDelimiter) {
        this.lineDelimiter = lineDelimiter;
        return this;
    }
}