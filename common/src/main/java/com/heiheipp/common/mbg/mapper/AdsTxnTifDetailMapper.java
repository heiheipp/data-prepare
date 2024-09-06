package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.AdsTxnTifDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdsTxnTifDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_detail
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_detail
     *
     * @mbg.generated
     */
    int insert(AdsTxnTifDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_detail
     *
     * @mbg.generated
     */
    AdsTxnTifDetail selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_detail
     *
     * @mbg.generated
     */
    List<AdsTxnTifDetail> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_detail
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AdsTxnTifDetail record);

    /**
     * 批量插入
     * @param adsTxnTifDetails
     * @return
     */
    int batchInsert(@Param("detail") List<AdsTxnTifDetail> adsTxnTifDetails);
}