package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.OperationRecorder;
import java.util.List;

public interface OperationRecorderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operation_recorder
     *
     * @mbg.generated
     */
    int insert(OperationRecorder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operation_recorder
     *
     * @mbg.generated
     */
    List<OperationRecorder> selectAll();
}