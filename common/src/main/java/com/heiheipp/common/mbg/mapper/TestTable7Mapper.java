package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.TestTable4;
import com.heiheipp.common.mbg.model.TestTable7;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TestTable7Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_7
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("vAccountNumber") String vAccountNumber, @Param("vInstCode") String vInstCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_7
     *
     * @mbg.generated
     */
    int insert(TestTable7 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_7
     *
     * @mbg.generated
     */
    TestTable7 selectByPrimaryKey(@Param("vAccountNumber") String vAccountNumber, @Param("vInstCode") String vInstCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_7
     *
     * @mbg.generated
     */
    List<TestTable7> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_7
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TestTable7 record);

    /**
     * 批量插入
     * @param testTable7s
     * @return
     */
    int batchInsert(@Param("tt7") List<TestTable7> testTable7s);
}