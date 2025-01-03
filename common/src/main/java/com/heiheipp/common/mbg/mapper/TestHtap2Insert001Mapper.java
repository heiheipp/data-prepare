package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.TestHtap2Insert001;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestHtap2Insert001Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TEST_HTAP2_INSERT_001
     *
     * @mbg.generated
     */
    int insert(TestHtap2Insert001 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TEST_HTAP2_INSERT_001
     *
     * @mbg.generated
     */
    List<TestHtap2Insert001> selectAll();

    /**
     * 批量插入
     * @param testTables
     * @return
     */
    int batchInsert(@Param("insert001") List<TestHtap2Insert001> testTables);
}