package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.AdsTxnTifBas;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdsTxnTifBasMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas
     *
     * @mbg.generated
     */
    int insert(AdsTxnTifBas record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas
     *
     * @mbg.generated
     */
    AdsTxnTifBas selectByPrimaryKey(@Param("unnTrno") BigDecimal unnTrno, @Param("txnDt") Date txnDt);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas
     *
     * @mbg.generated
     */
    List<AdsTxnTifBas> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ads_txn_tif_bas
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AdsTxnTifBas record);

    /**
     * 批量插入
     * @param adsTxnTifBass
     * @return
     */
    int batchInsert(@Param("bas") List<AdsTxnTifBas> adsTxnTifBass);
}