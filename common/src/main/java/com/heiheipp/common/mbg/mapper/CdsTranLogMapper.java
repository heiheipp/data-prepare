package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.CdsTranLog;
import java.util.List;

public interface CdsTranLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_tran_log
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_tran_log
     *
     * @mbg.generated
     */
    int insert(CdsTranLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_tran_log
     *
     * @mbg.generated
     */
    CdsTranLog selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_tran_log
     *
     * @mbg.generated
     */
    List<CdsTranLog> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_tran_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(CdsTranLog record);
}