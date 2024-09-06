package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.AdsTxnTifBas200c;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdsTxnTifBas200cMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_200c
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_200c
     *
     * @mbg.generated
     */
    int insert(AdsTxnTifBas200c record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_200c
     *
     * @mbg.generated
     */
    AdsTxnTifBas200c selectByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_200c
     *
     * @mbg.generated
     */
    List<AdsTxnTifBas200c> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas_200c
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AdsTxnTifBas200c record);

    /**
     * 批量插入
     * @param adsTxnTifBas200cs
     * @return
     */
    int batchInsert(@Param("ads200") List<AdsTxnTifBas200c> adsTxnTifBas200cs);
}