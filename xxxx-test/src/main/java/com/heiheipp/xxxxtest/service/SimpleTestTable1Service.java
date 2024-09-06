package com.heiheipp.xxxxtest.service;

import cn.hutool.core.bean.BeanUtil;
import com.heiheipp.common.mbg.model.TestTable1;
import com.heiheipp.xxxxtest.model.XxxxTestTable1ConfigModel;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxi
 * @version 1.0
 * @className SimpleTestTable1Service
 * @desc TODO
 * @date 2022/3/23 1:41
 */
@Slf4j
public class SimpleTestTable1Service {

    /**
     * PreparedStatement
     */
    private PreparedStatement preparedStatement = null;

    /**
     * sql header
     */
    private String sqlHeader = "insert into test_table_1";

    /**
     * sql columns
     */
    private String sqlColumns = "(column_1, column_2, column_3, column_4, column_5, column_6,\n" +
            "column_7, column_8, column_9, column_10, column_11, column_12, column_13, column_14,\n" +
            "column_15, column_16, column_17, column_18, column_19, column_20, column_21, column_22,\n" +
            "column_23, column_24, column_25, column_26, column_27, column_28, column_29, column_30,\n" +
            "column_31, column_32, column_33, column_34, column_35, column_36, column_37, column_38,\n" +
            "column_39, column_40, column_41, column_42, column_43, column_44, column_45, column_46,\n" +
            "column_47, column_48, column_49, column_50, column_51, column_52, column_53, column_54,\n" +
            "column_55, column_56, column_57, column_58, column_59, column_60, column_61, column_62,\n" +
            "column_63, column_64, column_65, column_66, column_67, column_68, column_69, column_70,\n" +
            "column_71, column_72, column_73, column_74, column_75, column_76, column_77, column_78,\n" +
            "column_79, column_80, column_81, column_82, column_83, column_84, column_85, column_86,\n" +
            "column_87, column_88, column_89, column_90, column_91, column_92, column_93, column_94,\n" +
            "column_95, column_96, column_97, column_98, column_99, column_100, column_101, column_102,\n" +
            "column_103, column_104, column_105, column_106, column_107, column_108, column_109, column_110,\n" +
            "column_111, column_112, column_113, column_114, column_115, column_116, column_117, column_118,\n" +
            "column_119, column_120, column_121, column_122, column_123, column_124, column_125, column_126,\n" +
            "column_127, column_128, column_129, column_130, column_131, column_132, column_133, column_134,\n" +
            "column_135, column_136, column_137, column_138, column_139, column_140, column_141, column_142,\n" +
            "column_143, column_144, column_145, column_146, column_147, column_148, column_149, column_150,\n" +
            "column_151)";

    /**
     * values
     */
    private String value = " values ";

    /**
     * sql body
     */
    private String sqlBody = "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * keys
     */
    private String[] keys = new String[]{"column1", "column2", "column3", "column4", "column5", "column6",
            "column7", "column8", "column9", "column10", "column11", "column12", "column13", "column14",
            "column15", "column16", "column17", "column18", "column19", "column20", "column21", "column22",
            "column23", "column24", "column25", "column26", "column27", "column28", "column29", "column30",
            "column31", "column32", "column33", "column34", "column35", "column36", "column37", "column38",
            "column39", "column40", "column41", "column42", "column43", "column44", "column45", "column46",
            "column47", "column48", "column49", "column50", "column51", "column52", "column53", "column54",
            "column55", "column56", "column57", "column58", "column59", "column60", "column61", "column62",
            "column63", "column64", "column65", "column66", "column67", "column68", "column69", "column70",
            "column71", "column72", "column73", "column74", "column75", "column76", "column77", "column78",
            "column79", "column80", "column81", "column82", "column83", "column84", "column85", "column86",
            "column87", "column88", "column89", "column90", "column91", "column92", "column93", "column94",
            "column95", "column96", "column97", "column98", "column99", "column100", "column101", "column102",
            "column103", "column104", "column105", "column106", "column107", "column108", "column109", "column110",
            "column111", "column112", "column113", "column114", "column115", "column116", "column117", "column118",
            "column119", "column120", "column121", "column122", "column123", "column124", "column125", "column126",
            "column127", "column128", "column129", "column130", "column131", "column132", "column133", "column134",
            "column135", "column136", "column137", "column138", "column139", "column140", "column141", "column142",
            "column143", "column144", "column145", "column146", "column147", "column148", "column149", "column150",
            "column151"};

    /**
     * 批量插入
     *
     * @param testTable1s
     * @return
     */
    public boolean batchInsert(List<TestTable1> testTable1s, Connection connection, PreparedStatement preparedStatement,
                               XxxxTestTable1ConfigModel xxxxTestTable1ConfigModel) {
        boolean result = false;

        if (!xxxxTestTable1ConfigModel.getXdsDemoConfigModel().isCdsDemo()) {
            // 非cds模式
            try {
                //preparedStatement = connection.prepareStatement(sqlHeader + sqlColumns + value + sqlBody);

                // 数据拼接
                Map<String, Object> map;
                for (int i = 0; i < testTable1s.size(); i++) {
                    map = BeanUtil.beanToMap(testTable1s.get(i));

                    for (int j = 0; j < map.size(); j++) {
                        preparedStatement.setObject(j + 1, map.get(keys[j]));
                    }

                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();
                //log.info("成功插入了[{}]行", arr.length);
                result = true;
            } catch (Exception e) {
                log.error("初始化数据库连接失败");
                e.printStackTrace();
            }
        } else {
            // cds模式
            long startTime = System.currentTimeMillis();

            int selectCount = xxxxTestTable1ConfigModel.getXdsDemoConfigModel().getSelectCount();
            int insertCount = xxxxTestTable1ConfigModel.getXdsDemoConfigModel().getInsertCount();
            int updateCount = xxxxTestTable1ConfigModel.getXdsDemoConfigModel().getUpdateCount();
            BigDecimal[] column_1Array = new BigDecimal[insertCount];
            String[] column_144Array = new String[insertCount];
            String updateSqlStr = "update test_table_1 set column_2 = ? where column_1 = ?";
            String selectSqlStr = "select column_2 from test_table_1 where column_1 = ?";

            try {
                // 关闭自动提交
                connection.setAutoCommit(false);

                if (xxxxTestTable1ConfigModel.getIsolation().equalsIgnoreCase("RC")) {
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                }

                // 执行insert
                int insertNum = 0;
                //preparedStatement = connection.prepareStatement(sqlHeader + sqlColumns + value + sqlBody);
                for (int j = 0; j < insertCount; j++) {
                    // 数据拼接
                    Map<String, Object> map;
                    map = BeanUtil.beanToMap(testTable1s.get(j));

                    for (int z = 0; z < map.size(); z++) {
                        preparedStatement.setObject(z + 1, map.get(keys[z]));

                        if (keys[z].equalsIgnoreCase("column1")) {
                            column_1Array[j] = (BigDecimal) map.get(keys[z]);
                        }

                        if (keys[z].equalsIgnoreCase("column144")) {
                            column_144Array[j] = (String) map.get(keys[z]);
                        }
                    }

                    insertNum += preparedStatement.executeUpdate();
                }
                log.info("Insert count is {}", insertNum);

                // 执行update
                int resultNum = 0;
                preparedStatement = connection.prepareStatement(updateSqlStr);
                for (int j = 0; j < updateCount; j++) {
                    preparedStatement.setObject(1, "AA" + j);
                    preparedStatement.setObject(2, column_1Array[j]);
                    resultNum += preparedStatement.executeUpdate();
                }
                log.info("Update count is {}", resultNum);

                // 执行select
                ResultSet rs = null;
                int selectPos = 0;
                preparedStatement = connection.prepareStatement(selectSqlStr);
                for (int j = 0; j < selectCount; j++) {
                    preparedStatement.setObject(1, column_1Array[selectPos]);
                    rs = preparedStatement.executeQuery();

                    if (selectPos >= column_1Array.length - 1) {
                        selectPos = 0;
                    } else {
                        selectPos++;
                    }
                }
                rs.close();

                // 提交事务
                connection.commit();
                log.info("Sub thread [{}], transaction execution time is {}", Thread.currentThread().getId(),
                        System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("数据库操作失败");
                e.printStackTrace();
            }
        }

        return result;
    }
}
