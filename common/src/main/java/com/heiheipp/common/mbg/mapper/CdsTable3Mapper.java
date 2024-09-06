package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.CdsTable3;
import java.util.List;

public interface CdsTable3Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_table_3
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_table_3
     *
     * @mbg.generated
     */
    int insert(CdsTable3 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_table_3
     *
     * @mbg.generated
     */
    CdsTable3 selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_table_3
     *
     * @mbg.generated
     */
    List<CdsTable3> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_table_3
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(CdsTable3 record);
}