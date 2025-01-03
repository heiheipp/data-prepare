package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.ZdjTable1;
import com.heiheipp.common.mbg.model.ZdjTable3;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZdjTable3Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zdj_table3
     *
     * @mbg.generated
     */
    int insert(ZdjTable3 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zdj_table3
     *
     * @mbg.generated
     */
    List<ZdjTable3> selectAll();

    /**
     * 批量插入
     * @param zdjTable3s
     * @return
     */
    int batchInsert(@Param("zdj3") List<ZdjTable3> zdjTable3s);
}