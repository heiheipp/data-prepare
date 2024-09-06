package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.AdsTxnTifBas50c;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdsTxnTifBas50cMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_50c
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_50c
     *
     * @mbg.generated
     */
    int insert(AdsTxnTifBas50c record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_50c
     *
     * @mbg.generated
     */
    AdsTxnTifBas50c selectByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_50c
     *
     * @mbg.generated
     */
    List<AdsTxnTifBas50c> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_50c
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AdsTxnTifBas50c record);

    /**
     * 批量插入
     * @param adsTxnTifBas50cs
     * @return
     */
    int batchInsert(@Param("ads50") List<AdsTxnTifBas50c> adsTxnTifBas50cs);
}