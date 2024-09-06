package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.AdsTxnTifBas400c;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdsTxnTifBas400cMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_400c
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_400c
     *
     * @mbg.generated
     */
    int insert(AdsTxnTifBas400c record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_400c
     *
     * @mbg.generated
     */
    AdsTxnTifBas400c selectByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_400c
     *
     * @mbg.generated
     */
    List<AdsTxnTifBas400c> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_400c
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AdsTxnTifBas400c record);

    /**
     * 批量插入
     * @param adsTxnTifBas400cs
     * @return
     */
    int batchInsert(@Param("ads400") List<AdsTxnTifBas400c> adsTxnTifBas400cs);
}