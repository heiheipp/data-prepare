package com.heiheipp.common.mbg.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TestTable8 extends BaseModel implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.V_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    private String vAccountNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.N_CYCLE_NO
     *
     * @mbg.generated
     */
    private Integer nCycleNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.D_CYCLE_START_DATE
     *
     * @mbg.generated
     */
    private Date dCycleStartDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.D_CYCLE_END_DATE
     *
     * @mbg.generated
     */
    private Date dCycleEndDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.D_CYCLE_DATE
     *
     * @mbg.generated
     */
    private Date dCycleDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.V_STATEMENT_PRODUCTION_CD
     *
     * @mbg.generated
     */
    private String vStatementProductionCd;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.N_OPENING_BALANCE
     *
     * @mbg.generated
     */
    private BigDecimal nOpeningBalance;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.N_OPENING_DUE
     *
     * @mbg.generated
     */
    private BigDecimal nOpeningDue;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.D_OPENING_DUE_DATE
     *
     * @mbg.generated
     */
    private Date dOpeningDueDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_8.N_DIRECT_DEBIT_AMOUNT
     *
     * @mbg.generated
     */
    private BigDecimal nDirectDebitAmount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table test_table_8
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.V_ACCOUNT_NUMBER
     *
     * @return the value of test_table_8.V_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    public String getvAccountNumber() {
        return vAccountNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.V_ACCOUNT_NUMBER
     *
     * @param vAccountNumber the value for test_table_8.V_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    public void setvAccountNumber(String vAccountNumber) {
        this.vAccountNumber = vAccountNumber == null ? null : vAccountNumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.N_CYCLE_NO
     *
     * @return the value of test_table_8.N_CYCLE_NO
     *
     * @mbg.generated
     */
    public Integer getnCycleNo() {
        return nCycleNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.N_CYCLE_NO
     *
     * @param nCycleNo the value for test_table_8.N_CYCLE_NO
     *
     * @mbg.generated
     */
    public void setnCycleNo(Integer nCycleNo) {
        this.nCycleNo = nCycleNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.D_CYCLE_START_DATE
     *
     * @return the value of test_table_8.D_CYCLE_START_DATE
     *
     * @mbg.generated
     */
    public Date getdCycleStartDate() {
        return dCycleStartDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.D_CYCLE_START_DATE
     *
     * @param dCycleStartDate the value for test_table_8.D_CYCLE_START_DATE
     *
     * @mbg.generated
     */
    public void setdCycleStartDate(Date dCycleStartDate) {
        this.dCycleStartDate = dCycleStartDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.D_CYCLE_END_DATE
     *
     * @return the value of test_table_8.D_CYCLE_END_DATE
     *
     * @mbg.generated
     */
    public Date getdCycleEndDate() {
        return dCycleEndDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.D_CYCLE_END_DATE
     *
     * @param dCycleEndDate the value for test_table_8.D_CYCLE_END_DATE
     *
     * @mbg.generated
     */
    public void setdCycleEndDate(Date dCycleEndDate) {
        this.dCycleEndDate = dCycleEndDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.D_CYCLE_DATE
     *
     * @return the value of test_table_8.D_CYCLE_DATE
     *
     * @mbg.generated
     */
    public Date getdCycleDate() {
        return dCycleDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.D_CYCLE_DATE
     *
     * @param dCycleDate the value for test_table_8.D_CYCLE_DATE
     *
     * @mbg.generated
     */
    public void setdCycleDate(Date dCycleDate) {
        this.dCycleDate = dCycleDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.V_STATEMENT_PRODUCTION_CD
     *
     * @return the value of test_table_8.V_STATEMENT_PRODUCTION_CD
     *
     * @mbg.generated
     */
    public String getvStatementProductionCd() {
        return vStatementProductionCd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.V_STATEMENT_PRODUCTION_CD
     *
     * @param vStatementProductionCd the value for test_table_8.V_STATEMENT_PRODUCTION_CD
     *
     * @mbg.generated
     */
    public void setvStatementProductionCd(String vStatementProductionCd) {
        this.vStatementProductionCd = vStatementProductionCd == null ? null : vStatementProductionCd.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.N_OPENING_BALANCE
     *
     * @return the value of test_table_8.N_OPENING_BALANCE
     *
     * @mbg.generated
     */
    public BigDecimal getnOpeningBalance() {
        return nOpeningBalance;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.N_OPENING_BALANCE
     *
     * @param nOpeningBalance the value for test_table_8.N_OPENING_BALANCE
     *
     * @mbg.generated
     */
    public void setnOpeningBalance(BigDecimal nOpeningBalance) {
        this.nOpeningBalance = nOpeningBalance;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.N_OPENING_DUE
     *
     * @return the value of test_table_8.N_OPENING_DUE
     *
     * @mbg.generated
     */
    public BigDecimal getnOpeningDue() {
        return nOpeningDue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.N_OPENING_DUE
     *
     * @param nOpeningDue the value for test_table_8.N_OPENING_DUE
     *
     * @mbg.generated
     */
    public void setnOpeningDue(BigDecimal nOpeningDue) {
        this.nOpeningDue = nOpeningDue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.D_OPENING_DUE_DATE
     *
     * @return the value of test_table_8.D_OPENING_DUE_DATE
     *
     * @mbg.generated
     */
    public Date getdOpeningDueDate() {
        return dOpeningDueDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.D_OPENING_DUE_DATE
     *
     * @param dOpeningDueDate the value for test_table_8.D_OPENING_DUE_DATE
     *
     * @mbg.generated
     */
    public void setdOpeningDueDate(Date dOpeningDueDate) {
        this.dOpeningDueDate = dOpeningDueDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_8.N_DIRECT_DEBIT_AMOUNT
     *
     * @return the value of test_table_8.N_DIRECT_DEBIT_AMOUNT
     *
     * @mbg.generated
     */
    public BigDecimal getnDirectDebitAmount() {
        return nDirectDebitAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_8.N_DIRECT_DEBIT_AMOUNT
     *
     * @param nDirectDebitAmount the value for test_table_8.N_DIRECT_DEBIT_AMOUNT
     *
     * @mbg.generated
     */
    public void setnDirectDebitAmount(BigDecimal nDirectDebitAmount) {
        this.nDirectDebitAmount = nDirectDebitAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_8
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", vAccountNumber=").append(vAccountNumber);
        sb.append(", nCycleNo=").append(nCycleNo);
        sb.append(", dCycleStartDate=").append(dCycleStartDate);
        sb.append(", dCycleEndDate=").append(dCycleEndDate);
        sb.append(", dCycleDate=").append(dCycleDate);
        sb.append(", vStatementProductionCd=").append(vStatementProductionCd);
        sb.append(", nOpeningBalance=").append(nOpeningBalance);
        sb.append(", nOpeningDue=").append(nOpeningDue);
        sb.append(", dOpeningDueDate=").append(dOpeningDueDate);
        sb.append(", nDirectDebitAmount=").append(nDirectDebitAmount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    /**
     * 生成文件头字符
     * @return
     */
    public static String generateFileHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("V_ACCOUNT_NUMBER,");
        sb.append("N_CYCLE_NO,");
        sb.append("D_CYCLE_START_DATE,");
        sb.append("D_CYCLE_END_DATE,");
        sb.append("D_CYCLE_DATE,");
        sb.append("V_STATEMENT_PRODUCTION_CD,");
        sb.append("N_OPENING_BALANCE,");
        sb.append("N_OPENING_DUE,");
        sb.append("D_OPENING_DUE_DATE,");
        sb.append("N_DIRECT_DEBIT_AMOUNT");

        return sb.toString();
    }
}