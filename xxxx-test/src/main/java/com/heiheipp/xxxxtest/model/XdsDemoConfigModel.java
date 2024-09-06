package com.heiheipp.xxxxtest.model;

import cn.hutool.setting.Setting;
import lombok.Data;

/**
 * @author zhangxi
 * @version 1.0
 * @className XdsDemoConfigModel
 * @desc TODO
 * @date 2022/3/26 11:56
 */
@Data
public class XdsDemoConfigModel {

    /**
     * cds demo flag
     */
    private boolean isCdsDemo;

    /**
     * sql count
     */
    private int sqlCount;

    /**
     * select count
     */
    private int selectCount;

    /**
     * insert count
     */
    private int insertCount;

    /**
     * update count
     */
    private int updateCount;

    /**
     * 构造函数
     * @param setting
     */
    public XdsDemoConfigModel(Setting setting) {
        this.isCdsDemo = setting.getBool("isCdsDemo", "test_table_1");
        this.sqlCount = setting.getInt("sqlCount", "test_table_1");
        this.selectCount = setting.getInt("selectCount", "test_table_1");
        this.insertCount = setting.getInt("insertCount", "test_table_1");
        this.updateCount = setting.getInt("updateCount", "test_table_1");
    }
}
