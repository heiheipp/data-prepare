package com.heiheipp.common.mbg.mapper;

import com.heiheipp.common.mbg.model.TaskLog;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TaskLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_log
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(@Param("parentId") String parentId, @Param("subId") String subId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_log
     *
     * @mbg.generated
     */
    int insert(TaskLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_log
     *
     * @mbg.generated
     */
    TaskLog selectByPrimaryKey(@Param("parentId") String parentId, @Param("subId") String subId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_log
     *
     * @mbg.generated
     */
    List<TaskLog> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TaskLog record);
}