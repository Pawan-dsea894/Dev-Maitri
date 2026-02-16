package com.iexceed.appzillonbanking.logs.domain.apz;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "TB_ASMI_SECURITY_PARAMETERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiSecurityParams implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "APP_ID")
    @JsonProperty("appId")
    private String appId;

    @Column(name = "MIN_NUM_NUM")
    @JsonProperty("minNumNum")
    private int minNumNum;

    @Column(name = "MIN_NUM_SPCL_CHAR")
    @JsonProperty("minNumSpclChar")
    private int minNumSpclChar;

    @Column(name = "MIN_NUM_UPPER_CASE_CHAR")
    @JsonProperty("minNumUpperCaseChar")
    private int minNumUpperCaseChar;

    @Column(name = "MIN_LENGTH")
    @JsonProperty("minLength")
    private int minLength;

    @Column(name = "MAX_LENGTH")
    @JsonProperty("maxLength")
    private int maxLength;

    @Column(name = "PASS_CHANGE_FREQ")
    @JsonProperty("passChangeFreq")
    private int passChangeFreq;

    @Column(name = "LAST_N_PASS_NOT_TO_USE")
    @JsonProperty("lastNPassNotToUse")
    private int lastNPassNotToUse;

    @Column(name = "SESSION_TIMEOUT")
    @JsonProperty("sessionTimeout")
    private int sessionTimeout;

    @Column(name = "NOOFFAILEDCOUNTS")
    @JsonProperty("nooffailedcounts")
    private int nooffailedcounts;

    @Column(name = "CREATE_USER_ID")
    private String createUserId;

    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;

    @Column(name = "VERSION_NO")
    @JsonProperty("versionNo")
    private Integer versionNo;

    @Column(name = "SERVER_TOKEN")
    @JsonProperty("serverToken")
    private String serverToken;

    @Column(name = "FAIL_COUNT_TIMEOUT")
    @JsonProperty("failCountTimeout")
    private int failCountTimeout;

    @Column(name = "PASSWORD_COUNT")
    @JsonProperty("passwordCount")
    private int passwordCount;

    @Column(name = "DEFAULT_AUTHORIZATION")
    @JsonProperty("defaultAuthorization")
    private String defaultAuthorization;

    @Column(name = "RESTRICT_SPL_CHARS")
    @JsonProperty("restrictSplChars")
    private String restrictedSplChars;

    //below two fields added for password enhancement by ripu
    @Column(name = "ALLOW_USER_PASSWORD_ENTRY")
    @JsonProperty("allowUserPasswordEntry")
    private String allowUserPasswordEntry;

    @Column(name = "AUTO_APPROVE")
    @JsonProperty("autoApprove")
    private String autoApprove;

    @Column(name = "PWD_RSET_VALIDATE_PARAMS")
    @JsonProperty("pwdRsetValidateParams")
    private String pwdRsetValidateParams;

    @Column(name = "PWD_RSET_COMM_CHANNEL")
    @JsonProperty("pwdRsetCommChannel")
    private String pwdRsetCommChannel;

    @Column(name = "PWD_RSET_ACCEPT_USR_PWD")
    @JsonProperty("pwdRsetAcceptUsrPwd")
    private String pwdRsetAcceptUsrPwd;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "MAKER_TS")
    @JsonProperty("makerTs")
    private Date makerTs;

    @Column(name = "MAKER_ID")
    @JsonProperty("makerId")
    private String makerId;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "CHECKER_TS")
    @JsonProperty("checkerTs")
    private Date checkerTs;

    @Column(name = "CHECKER_ID")
    @JsonProperty("checkerId")
    private String checkerId;

    @Column(name = "AUTH_STATUS")
    @JsonProperty("authStat")
    private String authStat;

    @Column(name = "TXN_LOG_REQ")
    @JsonProperty("txnlogReq")
    private String transactionLogRequest;

    @Column(name = "PWD_COMM_CHANNEL")
    @JsonProperty("pwdCommChannel")
    private String passwordCommunicationChannel;

    @Column(name = "MULTIPLE_SESSION_ALLOWED")
    @JsonProperty("multipleSessionAllowed")
    private String loginAllowedForMultipleDevice;

    @Column(name = "TXN_LOG_PAYLOAD")
    @JsonProperty("transactionLogPayload")
    private String transactionLogPayload;

    @Column(name = "FMW_TXN_PAYLOAD")
    @JsonProperty("fmwTxnPayload")
    private String fmwTxnPayload;


  //following 7 added by sasidhar for otpresend feature
  	@Column(name = "OTP_RESEND")
      @JsonProperty("otpResend")
  	private String otpResend;

  	@Column(name = "OTP_FORMAT")
      @JsonProperty("otpFormat")
  	 private String otpFormat;

  	@Column(name = "OTP_RESEND_COUNT")
      @JsonProperty("otpResendCount")
  	 private Integer otpResendCount;

  	@Column(name = "OTP_RESEND_LOCK_TIMEOUT")
     @JsonProperty("otpResendLockTimeOut")
  	 private Integer otpResendLockTimeOut;

  	@Column(name = "OTP_VALIDATION_COUNT")
    @JsonProperty("otpValidationCount")
  	private Integer otpValidationCount;

  	//password on authorization added on 03/04/17
  	@Column(name = "PASSWORD_ON_AUTHORIZATION")
  	@JsonProperty("passwordOnAuthorization")
  	private String passwordOnAuthorization;
    @Column(name = "OTP_REGEN_COUNT")
    @JsonProperty("otpRegenCount")
    private int otpRegenCount;


    //App access Expiry time 
    @Column(name = "ACCESS_TOKEN_EXPIRY")
  	@JsonProperty("accessTokenExpiry")
	private Integer accessTokenExpiry;

	@Column(name = "OTP_EXPIRY")
    @JsonProperty("otpExpiry")
	private Integer otpExpiry;

	@Column(name = "OTP_LENGTH")
    @JsonProperty("otpLength")
	private Integer otpLength;

	@Column(name = "PWD_CHANGE_COMM_CHANNEL")
	@JsonProperty("pwdChangeCommChannel")
    private String pwdChangeCommChannel;


	@Column(name = "DATA_INTEGRITY")
	@JsonProperty("dataIntegrity")
	private String dataIntegrity;

	@Column(name="FMW_TXN_REQ")
    private String fmwTxnReq;

}
