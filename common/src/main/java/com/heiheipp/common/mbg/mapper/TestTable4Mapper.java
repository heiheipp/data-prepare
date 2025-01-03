package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.TestTable3;
import com.heiheipp.common.mbg.model.TestTable4;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TestTable4Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_4
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("vCardId") String vCardId, @Param("vCardIssueId") String vCardIssueId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_4
     *
     * @mbg.generated
     */
    int insert(TestTable4 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_4
     *
     * @mbg.generated
     */
    TestTable4 selectByPrimaryKey(@Param("vCardId") String vCardId, @Param("vCardIssueId") String vCardIssueId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_4
     *
     * @mbg.generated
     */
    List<TestTable4> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_4
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TestTable4 record);

    /**
     * 批量插入
     * @param testTable4s
     * @return
     */
    int batchInsert(@Param("tt4") List<TestTable4> testTable4s);
}