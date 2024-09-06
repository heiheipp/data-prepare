package com.heiheipp.common.mbg.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TestTable7 extends BaseModel implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    private String vAccountNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_INST_CODE
     *
     * @mbg.generated
     */
    private String vInstCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_CARD_PRODUCT_CODE
     *
     * @mbg.generated
     */
    private String vCardProductCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_ACCT_TYPE_CD
     *
     * @mbg.generated
     */
    private String vAcctTypeCd;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_AGGREEMENT_NUMBER
     *
     * @mbg.generated
     */
    private String vAggreementNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_CUST_REF_CODE
     *
     * @mbg.generated
     */
    private String vCustRefCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_CCY_CODE
     *
     * @mbg.generated
     */
    private String vCcyCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_MANUAL_STATUS_CD
     *
     * @mbg.generated
     */
    private String vManualStatusCd;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.D_MANUAL_STATUS_SET_DATE
     *
     * @mbg.generated
     */
    private Date dManualStatusSetDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_AUTO_STATUS_CD
     *
     * @mbg.generated
     */
    private String vAutoStatusCd;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.D_AUTO_STATUS_SET_DATE
     *
     * @mbg.generated
     */
    private Date dAutoStatusSetDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.D_OPEN_DATE
     *
     * @mbg.generated
     */
    private Date dOpenDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_CORPORATE_ACCT_FLAG
     *
     * @mbg.generated
     */
    private String vCorporateAcctFlag;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_EXT_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    private String vExtAccountNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_EXT_ACCOUNT_NAME
     *
     * @mbg.generated
     */
    private String vExtAccountName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.N_CARD_NUMBER
     *
     * @mbg.generated
     */
    private Integer nCardNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.N_CUR_BOOK_BAL
     *
     * @mbg.generated
     */
    private BigDecimal nCurBookBal;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.N_HIGHEST_BAL
     *
     * @mbg.generated
     */
    private BigDecimal nHighestBal;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.N_CUR_DEBIT_INTEREST
     *
     * @mbg.generated
     */
    private BigDecimal nCurDebitInterest;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.N_CUR_CREDIT_INTEREST
     *
     * @mbg.generated
     */
    private BigDecimal nCurCreditInterest;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.N_PRE_DELINQUENCY_DAYS
     *
     * @mbg.generated
     */
    private Integer nPreDelinquencyDays;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.N_CUR_CYCLE_NO
     *
     * @mbg.generated
     */
    private Integer nCurCycleNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.N_CUR_STATEMENT_NO
     *
     * @mbg.generated
     */
    private Integer nCurStatementNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.V_ACCT_REF_NO
     *
     * @mbg.generated
     */
    private String vAcctRefNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_table_7.D_LAST_AUTOPAYMENT_DATE
     *
     * @mbg.generated
     */
    private Date dLastAutopaymentDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table test_table_7
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_ACCOUNT_NUMBER
     *
     * @return the value of test_table_7.V_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    public String getvAccountNumber() {
        return vAccountNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_ACCOUNT_NUMBER
     *
     * @param vAccountNumber the value for test_table_7.V_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    public void setvAccountNumber(String vAccountNumber) {
        this.vAccountNumber = vAccountNumber == null ? null : vAccountNumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_INST_CODE
     *
     * @return the value of test_table_7.V_INST_CODE
     *
     * @mbg.generated
     */
    public String getvInstCode() {
        return vInstCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_INST_CODE
     *
     * @param vInstCode the value for test_table_7.V_INST_CODE
     *
     * @mbg.generated
     */
    public void setvInstCode(String vInstCode) {
        this.vInstCode = vInstCode == null ? null : vInstCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_CARD_PRODUCT_CODE
     *
     * @return the value of test_table_7.V_CARD_PRODUCT_CODE
     *
     * @mbg.generated
     */
    public String getvCardProductCode() {
        return vCardProductCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_CARD_PRODUCT_CODE
     *
     * @param vCardProductCode the value for test_table_7.V_CARD_PRODUCT_CODE
     *
     * @mbg.generated
     */
    public void setvCardProductCode(String vCardProductCode) {
        this.vCardProductCode = vCardProductCode == null ? null : vCardProductCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_ACCT_TYPE_CD
     *
     * @return the value of test_table_7.V_ACCT_TYPE_CD
     *
     * @mbg.generated
     */
    public String getvAcctTypeCd() {
        return vAcctTypeCd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_ACCT_TYPE_CD
     *
     * @param vAcctTypeCd the value for test_table_7.V_ACCT_TYPE_CD
     *
     * @mbg.generated
     */
    public void setvAcctTypeCd(String vAcctTypeCd) {
        this.vAcctTypeCd = vAcctTypeCd == null ? null : vAcctTypeCd.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_AGGREEMENT_NUMBER
     *
     * @return the value of test_table_7.V_AGGREEMENT_NUMBER
     *
     * @mbg.generated
     */
    public String getvAggreementNumber() {
        return vAggreementNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_AGGREEMENT_NUMBER
     *
     * @param vAggreementNumber the value for test_table_7.V_AGGREEMENT_NUMBER
     *
     * @mbg.generated
     */
    public void setvAggreementNumber(String vAggreementNumber) {
        this.vAggreementNumber = vAggreementNumber == null ? null : vAggreementNumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_CUST_REF_CODE
     *
     * @return the value of test_table_7.V_CUST_REF_CODE
     *
     * @mbg.generated
     */
    public String getvCustRefCode() {
        return vCustRefCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_CUST_REF_CODE
     *
     * @param vCustRefCode the value for test_table_7.V_CUST_REF_CODE
     *
     * @mbg.generated
     */
    public void setvCustRefCode(String vCustRefCode) {
        this.vCustRefCode = vCustRefCode == null ? null : vCustRefCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_CCY_CODE
     *
     * @return the value of test_table_7.V_CCY_CODE
     *
     * @mbg.generated
     */
    public String getvCcyCode() {
        return vCcyCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_CCY_CODE
     *
     * @param vCcyCode the value for test_table_7.V_CCY_CODE
     *
     * @mbg.generated
     */
    public void setvCcyCode(String vCcyCode) {
        this.vCcyCode = vCcyCode == null ? null : vCcyCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_MANUAL_STATUS_CD
     *
     * @return the value of test_table_7.V_MANUAL_STATUS_CD
     *
     * @mbg.generated
     */
    public String getvManualStatusCd() {
        return vManualStatusCd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_MANUAL_STATUS_CD
     *
     * @param vManualStatusCd the value for test_table_7.V_MANUAL_STATUS_CD
     *
     * @mbg.generated
     */
    public void setvManualStatusCd(String vManualStatusCd) {
        this.vManualStatusCd = vManualStatusCd == null ? null : vManualStatusCd.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.D_MANUAL_STATUS_SET_DATE
     *
     * @return the value of test_table_7.D_MANUAL_STATUS_SET_DATE
     *
     * @mbg.generated
     */
    public Date getdManualStatusSetDate() {
        return dManualStatusSetDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.D_MANUAL_STATUS_SET_DATE
     *
     * @param dManualStatusSetDate the value for test_table_7.D_MANUAL_STATUS_SET_DATE
     *
     * @mbg.generated
     */
    public void setdManualStatusSetDate(Date dManualStatusSetDate) {
        this.dManualStatusSetDate = dManualStatusSetDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_AUTO_STATUS_CD
     *
     * @return the value of test_table_7.V_AUTO_STATUS_CD
     *
     * @mbg.generated
     */
    public String getvAutoStatusCd() {
        return vAutoStatusCd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_AUTO_STATUS_CD
     *
     * @param vAutoStatusCd the value for test_table_7.V_AUTO_STATUS_CD
     *
     * @mbg.generated
     */
    public void setvAutoStatusCd(String vAutoStatusCd) {
        this.vAutoStatusCd = vAutoStatusCd == null ? null : vAutoStatusCd.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.D_AUTO_STATUS_SET_DATE
     *
     * @return the value of test_table_7.D_AUTO_STATUS_SET_DATE
     *
     * @mbg.generated
     */
    public Date getdAutoStatusSetDate() {
        return dAutoStatusSetDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.D_AUTO_STATUS_SET_DATE
     *
     * @param dAutoStatusSetDate the value for test_table_7.D_AUTO_STATUS_SET_DATE
     *
     * @mbg.generated
     */
    public void setdAutoStatusSetDate(Date dAutoStatusSetDate) {
        this.dAutoStatusSetDate = dAutoStatusSetDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.D_OPEN_DATE
     *
     * @return the value of test_table_7.D_OPEN_DATE
     *
     * @mbg.generated
     */
    public Date getdOpenDate() {
        return dOpenDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.D_OPEN_DATE
     *
     * @param dOpenDate the value for test_table_7.D_OPEN_DATE
     *
     * @mbg.generated
     */
    public void setdOpenDate(Date dOpenDate) {
        this.dOpenDate = dOpenDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_CORPORATE_ACCT_FLAG
     *
     * @return the value of test_table_7.V_CORPORATE_ACCT_FLAG
     *
     * @mbg.generated
     */
    public String getvCorporateAcctFlag() {
        return vCorporateAcctFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_CORPORATE_ACCT_FLAG
     *
     * @param vCorporateAcctFlag the value for test_table_7.V_CORPORATE_ACCT_FLAG
     *
     * @mbg.generated
     */
    public void setvCorporateAcctFlag(String vCorporateAcctFlag) {
        this.vCorporateAcctFlag = vCorporateAcctFlag == null ? null : vCorporateAcctFlag.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_EXT_ACCOUNT_NUMBER
     *
     * @return the value of test_table_7.V_EXT_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    public String getvExtAccountNumber() {
        return vExtAccountNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_EXT_ACCOUNT_NUMBER
     *
     * @param vExtAccountNumber the value for test_table_7.V_EXT_ACCOUNT_NUMBER
     *
     * @mbg.generated
     */
    public void setvExtAccountNumber(String vExtAccountNumber) {
        this.vExtAccountNumber = vExtAccountNumber == null ? null : vExtAccountNumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_EXT_ACCOUNT_NAME
     *
     * @return the value of test_table_7.V_EXT_ACCOUNT_NAME
     *
     * @mbg.generated
     */
    public String getvExtAccountName() {
        return vExtAccountName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_EXT_ACCOUNT_NAME
     *
     * @param vExtAccountName the value for test_table_7.V_EXT_ACCOUNT_NAME
     *
     * @mbg.generated
     */
    public void setvExtAccountName(String vExtAccountName) {
        this.vExtAccountName = vExtAccountName == null ? null : vExtAccountName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.N_CARD_NUMBER
     *
     * @return the value of test_table_7.N_CARD_NUMBER
     *
     * @mbg.generated
     */
    public Integer getnCardNumber() {
        return nCardNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.N_CARD_NUMBER
     *
     * @param nCardNumber the value for test_table_7.N_CARD_NUMBER
     *
     * @mbg.generated
     */
    public void setnCardNumber(Integer nCardNumber) {
        this.nCardNumber = nCardNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.N_CUR_BOOK_BAL
     *
     * @return the value of test_table_7.N_CUR_BOOK_BAL
     *
     * @mbg.generated
     */
    public BigDecimal getnCurBookBal() {
        return nCurBookBal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.N_CUR_BOOK_BAL
     *
     * @param nCurBookBal the value for test_table_7.N_CUR_BOOK_BAL
     *
     * @mbg.generated
     */
    public void setnCurBookBal(BigDecimal nCurBookBal) {
        this.nCurBookBal = nCurBookBal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.N_HIGHEST_BAL
     *
     * @return the value of test_table_7.N_HIGHEST_BAL
     *
     * @mbg.generated
     */
    public BigDecimal getnHighestBal() {
        return nHighestBal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.N_HIGHEST_BAL
     *
     * @param nHighestBal the value for test_table_7.N_HIGHEST_BAL
     *
     * @mbg.generated
     */
    public void setnHighestBal(BigDecimal nHighestBal) {
        this.nHighestBal = nHighestBal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.N_CUR_DEBIT_INTEREST
     *
     * @return the value of test_table_7.N_CUR_DEBIT_INTEREST
     *
     * @mbg.generated
     */
    public BigDecimal getnCurDebitInterest() {
        return nCurDebitInterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.N_CUR_DEBIT_INTEREST
     *
     * @param nCurDebitInterest the value for test_table_7.N_CUR_DEBIT_INTEREST
     *
     * @mbg.generated
     */
    public void setnCurDebitInterest(BigDecimal nCurDebitInterest) {
        this.nCurDebitInterest = nCurDebitInterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.N_CUR_CREDIT_INTEREST
     *
     * @return the value of test_table_7.N_CUR_CREDIT_INTEREST
     *
     * @mbg.generated
     */
    public BigDecimal getnCurCreditInterest() {
        return nCurCreditInterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.N_CUR_CREDIT_INTEREST
     *
     * @param nCurCreditInterest the value for test_table_7.N_CUR_CREDIT_INTEREST
     *
     * @mbg.generated
     */
    public void setnCurCreditInterest(BigDecimal nCurCreditInterest) {
        this.nCurCreditInterest = nCurCreditInterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.N_PRE_DELINQUENCY_DAYS
     *
     * @return the value of test_table_7.N_PRE_DELINQUENCY_DAYS
     *
     * @mbg.generated
     */
    public Integer getnPreDelinquencyDays() {
        return nPreDelinquencyDays;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.N_PRE_DELINQUENCY_DAYS
     *
     * @param nPreDelinquencyDays the value for test_table_7.N_PRE_DELINQUENCY_DAYS
     *
     * @mbg.generated
     */
    public void setnPreDelinquencyDays(Integer nPreDelinquencyDays) {
        this.nPreDelinquencyDays = nPreDelinquencyDays;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.N_CUR_CYCLE_NO
     *
     * @return the value of test_table_7.N_CUR_CYCLE_NO
     *
     * @mbg.generated
     */
    public Integer getnCurCycleNo() {
        return nCurCycleNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.N_CUR_CYCLE_NO
     *
     * @param nCurCycleNo the value for test_table_7.N_CUR_CYCLE_NO
     *
     * @mbg.generated
     */
    public void setnCurCycleNo(Integer nCurCycleNo) {
        this.nCurCycleNo = nCurCycleNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.N_CUR_STATEMENT_NO
     *
     * @return the value of test_table_7.N_CUR_STATEMENT_NO
     *
     * @mbg.generated
     */
    public Integer getnCurStatementNo() {
        return nCurStatementNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.N_CUR_STATEMENT_NO
     *
     * @param nCurStatementNo the value for test_table_7.N_CUR_STATEMENT_NO
     *
     * @mbg.generated
     */
    public void setnCurStatementNo(Integer nCurStatementNo) {
        this.nCurStatementNo = nCurStatementNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.V_ACCT_REF_NO
     *
     * @return the value of test_table_7.V_ACCT_REF_NO
     *
     * @mbg.generated
     */
    public String getvAcctRefNo() {
        return vAcctRefNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.V_ACCT_REF_NO
     *
     * @param vAcctRefNo the value for test_table_7.V_ACCT_REF_NO
     *
     * @mbg.generated
     */
    public void setvAcctRefNo(String vAcctRefNo) {
        this.vAcctRefNo = vAcctRefNo == null ? null : vAcctRefNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_table_7.D_LAST_AUTOPAYMENT_DATE
     *
     * @return the value of test_table_7.D_LAST_AUTOPAYMENT_DATE
     *
     * @mbg.generated
     */
    public Date getdLastAutopaymentDate() {
        return dLastAutopaymentDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_table_7.D_LAST_AUTOPAYMENT_DATE
     *
     * @param dLastAutopaymentDate the value for test_table_7.D_LAST_AUTOPAYMENT_DATE
     *
     * @mbg.generated
     */
    public void setdLastAutopaymentDate(Date dLastAutopaymentDate) {
        this.dLastAutopaymentDate = dLastAutopaymentDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_table_7
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
        sb.append(", vInstCode=").append(vInstCode);
        sb.append(", vCardProductCode=").append(vCardProductCode);
        sb.append(", vAcctTypeCd=").append(vAcctTypeCd);
        sb.append(", vAggreementNumber=").append(vAggreementNumber);
        sb.append(", vCustRefCode=").append(vCustRefCode);
        sb.append(", vCcyCode=").append(vCcyCode);
        sb.append(", vManualStatusCd=").append(vManualStatusCd);
        sb.append(", dManualStatusSetDate=").append(dManualStatusSetDate);
        sb.append(", vAutoStatusCd=").append(vAutoStatusCd);
        sb.append(", dAutoStatusSetDate=").append(dAutoStatusSetDate);
        sb.append(", dOpenDate=").append(dOpenDate);
        sb.append(", vCorporateAcctFlag=").append(vCorporateAcctFlag);
        sb.append(", vExtAccountNumber=").append(vExtAccountNumber);
        sb.append(", vExtAccountName=").append(vExtAccountName);
        sb.append(", nCardNumber=").append(nCardNumber);
        sb.append(", nCurBookBal=").append(nCurBookBal);
        sb.append(", nHighestBal=").append(nHighestBal);
        sb.append(", nCurDebitInterest=").append(nCurDebitInterest);
        sb.append(", nCurCreditInterest=").append(nCurCreditInterest);
        sb.append(", nPreDelinquencyDays=").append(nPreDelinquencyDays);
        sb.append(", nCurCycleNo=").append(nCurCycleNo);
        sb.append(", nCurStatementNo=").append(nCurStatementNo);
        sb.append(", vAcctRefNo=").append(vAcctRefNo);
        sb.append(", dLastAutopaymentDate=").append(dLastAutopaymentDate);
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
        sb.append("V_INST_CODE,");
        sb.append("V_CARD_PRODUCT_CODE,");
        sb.append("V_ACCT_TYPE_CD,");
        sb.append("V_AGGREEMENT_NUMBER,");
        sb.append("V_CUST_REF_CODE,");
        sb.append("V_CCY_CODE,");
        sb.append("V_MANUAL_STATUS_CD,");
        sb.append("D_MANUAL_STATUS_SET_DATE,");
        sb.append("V_AUTO_STATUS_CD,");
        sb.append("D_AUTO_STATUS_SET_DATE,");
        sb.append("D_OPEN_DATE,");
        sb.append("V_CORPORATE_ACCT_FLAG,");
        sb.append("V_EXT_ACCOUNT_NUMBER,");
        sb.append("V_EXT_ACCOUNT_NAME,");
        sb.append("N_CARD_NUMBER,");
        sb.append("N_CUR_BOOK_BAL,");
        sb.append("N_HIGHEST_BAL,");
        sb.append("N_CUR_DEBIT_INTEREST,");
        sb.append("N_CUR_CREDIT_INTEREST,");
        sb.append("N_PRE_DELINQUENCY_DAYS,");
        sb.append("N_CUR_CYCLE_NO,");
        sb.append("N_CUR_STATEMENT_NO,");
        sb.append("V_ACCT_REF_NO,");
        sb.append("D_LAST_AUTOPAYMENT_DATE");

        return sb.toString();
    }
}