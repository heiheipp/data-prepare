package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.AdsTxnTifBas300c;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdsTxnTifBas300cMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_300c
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_300c
     *
     * @mbg.generated
     */
    int insert(AdsTxnTifBas300c record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_300c
     *
     * @mbg.generated
     */
    AdsTxnTifBas300c selectByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_300c
     *
     * @mbg.generated
     */
    List<AdsTxnTifBas300c> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_300c
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AdsTxnTifBas300c record);

    /**
     * 批量插入
     * @param adsTxnTifBas300cs
     * @return
     */
    int batchInsert(@Param("ads300") List<AdsTxnTifBas300c> adsTxnTifBas300cs);
}