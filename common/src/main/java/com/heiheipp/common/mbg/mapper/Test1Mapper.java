package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.Test1;
import java.util.List;

public interface Test1Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test1
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test1
     *
     * @mbg.generated
     */
    int insert(Test1 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test1
     *
     * @mbg.generated
     */
    Test1 selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test1
     *
     * @mbg.generated
     */
    List<Test1> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test1
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Test1 record);
}