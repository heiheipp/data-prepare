package com.heiheipp.common.mbg.model;

import java.io.Serializable;

public class CdsTable3 implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cds_table_3.id
     *
     * @mbg.generated
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column cds_table_3.field1
     *
     * @mbg.generated
     */
    private String field1;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table cds_table_3
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cds_table_3.id
     *
     * @return the value of cds_table_3.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cds_table_3.id
     *
     * @param id the value for cds_table_3.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column cds_table_3.field1
     *
     * @return the value of cds_table_3.field1
     *
     * @mbg.generated
     */
    public String getField1() {
        return field1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column cds_table_3.field1
     *
     * @param field1 the value for cds_table_3.field1
     *
     * @mbg.generated
     */
    public void setField1(String field1) {
        this.field1 = field1 == null ? null : field1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cds_table_3
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", field1=").append(field1);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}