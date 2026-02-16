package com.iexceed.appzillonbanking.core.payload;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestFields {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("userId")
	private String userId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("customerId")
	private String customerId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("custName")
	private String custName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("deviceId")
	private String deviceId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("customerType")
	private String customerType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("customerCountryCode")
	private String customerCountryCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("email")
	private String email;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("fullName")
	private String fullName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("dob")
	private String dob;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loginPassword")
	private String loginPassword;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loginPin")
	private String loginPin;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("creditCardNumber")
	private String creditCardNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("idCardNo")
	private String idCardNo;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("idCardType")
	private String idCardType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("gender")
	private String gender;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("province")
	private String province;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("district")
	private String district;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("community")
	private String community;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("village")
	private String village;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("streetNumber")
	private String streetNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("kyc")
	private String kyc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("userTitle")
	private String userTitle;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAccountHolderAddress")
	private String debitAccountHolderAddress;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAccountHolderHouseNum")
	private String debitAccountHolderHouseNum;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("residentStatus")
	private String residentStatus;

	// customer account details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAcctNo")
	private String debitAcctNo;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAcctBrn")
	private String debitAcctBrn;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAcctCcy")
	private String debitAcctCcy;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAcctType")
	private String debitAcctType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitProductType")
	private String debitProductType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAccountHolderName")
	private String debitAccountHolderName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAccountIfsc")
	private String debitAccountIfsc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitBankCode")
	private String debitBankCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAccBrnId")
	private String debitAccBrnId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("debitAccBalance")
	private String debitAccBalance;

	// fund transfer details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transactionCcy")
	private String transactionCcy;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transactionType")
	private String transactionType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transferType")
	private String transferType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transactionMode")
	private String transactionMode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transactionAmount")
	private String transactionAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transRemarks")
	private String transRemarks;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transactionDate")
	private String transactionDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("equivalentAmount")
	private String equivalentAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transactionRefNumber")
	private String transactionRefNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("cbsId")
	private String cbsId;

	// limit details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("limitGroup")
	private String limitGroup;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("dailyLmt")
	private String dailyLmt;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("txnMinLmt")
	private String txnMinLmt;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("txnMaxLmt")
	private String txnMaxLmt;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("monthlyLmt")
	private String monthlyLmt;

	// charge fee details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("chargeAmount")
	private String chargeAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("chargeCurrency")
	private String chargeCurrency;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("chargeType")
	private String chargeType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("creditChargeAmount")
	private String creditChargeAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("feeAccount")
	private String feeAccount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("authStatus")
	private String authStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("chargeWaive")
	private String chargeWaive;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("fromSlab")
	private String fromSlab;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("toSlab")
	private String toSlab;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("checkerId")
	private String checkerId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("makerId")
	private String makerId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("effectiveDate")
	private String effectiveDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transactionMinAmount")
	private String transactionMinAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("transactionMaxAmount")
	private String transactionMaxAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productDesc")
	private String productDesc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("versionNumber")
	private String versionNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("flatAmount")
	private String flatAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("percentage")
	private String percentage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("slabNumber")
	private String slabNumber;

	// biller details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerCategoryId")
	@Schema(example = "1", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerCategoryId.value}")
	private String billerCategoryId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerCategory")
	@Schema(example = "12", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerCategory.value}")
	private String billerCategory;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerId")
	@Schema(example = "123909090943", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerId.value}")
	private String billerId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerAccountId")
	@Schema(example = "123909090943", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerId.value}")
	private String billerAccountId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerName")
	@Schema(example = "idea", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerName.value}")
	private String billerName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerLocationId")
	@Schema(example = "12", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerLocationId.value}")
	private String billerLocationId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerLocation")
	@Schema(example = "Bengaluru", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerLocation.value}")
	private String billerLocation;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerShortName")
	@Schema(example = "paps", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerShortName.value}")
	private String billerShortName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerConsumerNumber")
	@Schema(example = "31231", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerConsumerNumber.value}")
	private String billerConsumerNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerStatus")
	@Schema(example = "31231", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerStatus.value}")
	private String billerStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billNumber")
	@Schema(example = "31231", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billNumber.value}")
	private String billNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billerAccountNumber")
	@Schema(example = "31231", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billerAccountNumber.value}")
	private String billerAccountNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billName")
	@Schema(example = "31231", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billName.value}")
	private String billName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("billNickName")
	@Schema(example = "31231", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.billNickName.value}")
	private String billNickName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("payeeGroup")
	@Schema(example = "31231", requiredMode = RequiredMode.REQUIRED, description = "${AddBillerRequest.payeeGroup.value}")
	private String payeeGroup;

	// beneficiary details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefName")
	private String benefName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefImage")
	private String benefImage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("beneficiaryCustId")
	private String beneficiaryCustId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("beneficiaryDeviceId")
	private String beneficiaryDeviceId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefMobileNum")
	private String benefMobileNum;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefType")
	private String benefType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefEmail")
	private String benefEmail;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefStatus")
	private String benefStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefCoolingTime")
	private Timestamp benefCoolingTime;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefShortName")
	private String benefShortName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefCountryCode")
	private String benefCountryCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAccountHolderName")
	private String benefAccountHolderName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAccountHolderAddress")
	private String benefAccountHolderAddress;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAccountHolderHouseNum")
	private String benefAccountHolderHouseNum;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("beneficiaryCreditCardNumber")
	private String beneficiaryCreditCardNumber;

	// beneficiary account details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAcctNo")
	private String benefAcctNo;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAcctCcy")
	private String benefAcctCcy;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAcctType")
	private String benefAcctType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefBankName")
	private String benefBankName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefIfsc")
	private String benefIfsc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAcctBrn")
	private String benefAcctBrn;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAccBrnId")
	private String benefAccBrnId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefAccBalance")
	private String benefAccBalance;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefBankCode")
	private String benefBankCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefBankAddress")
	private String benefBankAddress;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("benefProductType")
	private String benefProductType;

	// session details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("sessionId")
	private String sessionId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("userName")
	private String userName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("password")
	private String password;

	// transaction history details
	@Schema(example = "CD", requiredMode = RequiredMode.REQUIRED, description = "${Transactionhistoryrequest.groupClass.value}")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("groupClass")
	private String groupClass;

	@Schema(example = "20181217", requiredMode = RequiredMode.REQUIRED, description = "${Transactionhistoryrequest.startDate.value}")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("startDate")
	private String startDate;

	@Schema(example = "20181219", requiredMode = RequiredMode.REQUIRED, description = "${Transactionhistoryrequest.endDate.value}")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("endDate")
	private String endDate;

	@Schema(example = "331231", requiredMode = RequiredMode.REQUIRED, description = "${Transactionhistoryrequest.appId.value}")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("txnNumber")
	private String txnNumber;

	@Schema(example = "10", requiredMode = RequiredMode.REQUIRED, description = "${Transactionhistoryrequest.appId.value}")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("txnCount")
	private String txnCount;

	// Loan Details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanAccountNumber")
	private String loanAccountNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanAccountType")
	private String loanAccountType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanTenure")
	private String loanTenure;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanAmount")
	private String loanAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanPrincipalAmount")
	private String loanPrincipalAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanInterestAmount")
	private String loanInterestAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanRemarks")
	private String loanRemarks;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanDate")
	private Date loanDate;

	// Deposit Details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("depositAccNumber")
	private String depositAccNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("depositType")
	private String depositType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("depositAmount")
	private String depositAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("depositCcy")
	private String depositCcy;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("depositRemarks")
	private String depositRemarks;

	// Deposit Calculator
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("principalAmount")
	private String principalAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("nominalInterestRate")
	private String nominalInterestRate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("numberOfYears")
	private String numberOfYears;

	// SMS Details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("messageSenderName")
	private String messageSenderName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("messageReceivername")
	private String messageReceivername;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("messageText")
	private String messageText;

	// Cheque Book details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("chequeNumber")
	private String chequeNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("numOfChequeBooks")
	private String numOfChequeBooks;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("numOfChequeLeaves")
	private String numOfChequeLeaves;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("deliverTo")
	private String deliverTo;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("stopChequeReason")
	private String stopChequeReason;

	// Passbook details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("passbookNumber")
	private String passbookNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("numOfPassbooks")
	private String numOfPassbooks;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("stopPassbookReason")
	private String stopPassbookReason;

	// FAQ Details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("question")
	private String question;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("answer")
	private String answer;

	// favourite details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("favouriteName")
	@Schema(example = "null", requiredMode = RequiredMode.REQUIRED, description = "${FavouriteRequest.favouriteName.value}")
	private String favouriteName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("fromaccountType")
	@Schema(example = "null", requiredMode = RequiredMode.REQUIRED, description = "${FavouriteRequest.fromaccountType.value}")
	private String fromaccountType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("channelId")
	@Schema(example = "null", requiredMode = RequiredMode.REQUIRED, description = "${FavouriteRequest.channelId.value}")
	private String channelId;

	// Raise Complaint Parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("complaintCategory")
	private String complaintCategory;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("complaintCategoryId")
	private String complaintCategoryId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("complaintDesc")
	private String complaintDesc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("complaintReason")
	private String complaintReason;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("complaintDoc")
	private String complaintDoc;

	// otp parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("authenticationReferenceCode")
	private String authenticationReferenceCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("otp")
	private String otp;

	// Schedule Transfer Parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("scheduleId")
	private String scheduleId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("txnScheduleFromDate")
	private String txnScheduleFromDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("txnScheduleToDate")
	private String txnScheduleToDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("scheduleTxnType")
	private String scheduleTxnType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("noOfInstallments")
	private String noOfInstallments;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("scheduleFrequency")
	private String scheduleFrequency;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("txnScheduleDay")
	private String txnScheduleDay;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("txnScheduleMonth")
	private String txnScheduleMonth;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("scheduleTransferRemarks")
	private String scheduleTransferRemarks;

	// EMI Calculator parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanTenureInMonth")
	private String loanTenureInMonth;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("rateOfInterest")
	private String rateOfInterest;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("loanTenureInYears")
	private String loanTenureInYears;

	// Product Lead Parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productBranchId")
	private String productBranchId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productCategoryId")
	private String productCategoryId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productChannelId")
	private String productChannelId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productCustomerId")
	private String productCustomerId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productEmailId")
	private String productEmailId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("contactNumber")
	private String contactNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productCityId")
	private String productCityId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productStateId")
	private String productStateId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productFirstName")
	private String productFirstName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("productLastName")
	private String productLastName;

	// Locator parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("longitude")
	private String longitude;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("lattitude")
	private String lattitude;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("city")
	private String city;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("state")
	private String state;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("branch")
	private String branch;

	// apply now details
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "36", requiredMode = RequiredMode.REQUIRED, description = "${ApplydepositRequest.tenureInMonths.value}")
	@JsonProperty("depositTenureInMonths")
	private int depositTenureInMonths;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "10000000", requiredMode = RequiredMode.REQUIRED, description = "${ApplydepositRequest.interestRate.value}")
	@JsonProperty("depositInterestRate")
	private float depositInterestRate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "Maturity", requiredMode = RequiredMode.REQUIRED, description = "${ApplydepositRequest.interestPayout.value}")
	@JsonProperty("depositInterestPayout")
	private String depositInterestPayout;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "John", requiredMode = RequiredMode.REQUIRED, description = "${ApplydepositRequest.nomineeDetails.value}")
	@JsonProperty("nomineeDetails")
	private String nomineeDetails;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "150000000", requiredMode = RequiredMode.REQUIRED, description = "${ApplydepositRequest.maturityAmt.value}")
	@JsonProperty("maturityAmount")
	private BigDecimal maturityAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(example = "01-01-2025", requiredMode = RequiredMode.REQUIRED, description = "${ApplydepositRequest.maturityDate.value}")
	@JsonProperty("maturityDate")
	private Date maturityDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("depositDate")
	private Date depositDate;

	// budget parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("budgetId")
	private String budgetId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("budgetDesc")
	private String budgetDesc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("budgetPeriod")
	private String budgetPeriod;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("budgetCategory")
	private String budgetCategory;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("budgetAmount")
	private String budgetAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("thresholdAmount")
	private String thresholdAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("budgetStart")
	private String budgetStart;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("budgetStatus")
	private String budgetStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("durationtype")
	private String durationtype;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("quarter")
	private String quarter;

	// Goal Parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("goalTxnId")
	private String goalTxnId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("goalImgId")
	private String goalImgId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("goalName")
	private String goalName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("goalDesc")
	private String goalDesc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("targetAmount")
	private String targetAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("fundingAccount")
	private String fundingAccount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("allocatePercentage")
	private String allocatePercentage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("goalStatus")
	private String goalStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("goalAccNumber")
	private String goalAccNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("initialDepositAmt")
	private String initialDepositAmt;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("siRequired")
	private String siRequired;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("autoRedemption")
	private String autoRedemption;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("redemptAccountNum")
	private String redemptAccountNum;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("accNumberType")
	private String accNumberType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("accNumberDesc")
	private String accNumberDesc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("currentAmount")
	private String currentAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("amountAsOf")
	private String amountAsOf;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("siSorceAccNumber")
	private String siSorceAccNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("siStatus")
	private String siStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("siStartDate")
	private String siStartDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("siFrequency")
	private String siFrequency;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("siAmount")
	private String siAmount;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("goalAccountFlag")
	private String goalAccountFlag;

	// miscellaneous api parameters
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("channel")
	private String channel;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("apiDesc")
	private String apiDesc;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("id")
	private String id;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("base64String")
	private String base64String;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("fileType")
	private String fileType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("catalogCode")
	private String catalogCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("primaryAccountFlag")
	private String primaryAccountFlag;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("groupNumber")
	private String groupNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("stage")
	private String stage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("handle")
	private String handle;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("language")
	private String language;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("signature")
	private String signature;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("timeStamp")
	private String timeStamp;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("brandId")
	private String brandId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("action")
	private String action;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("notification")
	private String notification;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("accessType")
	private String accessType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("code")
	private String code;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("errorCode")
	private String errorCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("errorMessage")
	private String errorMessage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("networkType")
	private String networkType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("nomineeId")
	private String nomineeId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("relationship")
	private String relationship;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("guardianAddress")
	private String guardianAddress;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("guardianName")
	private String guardianName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("pinCode")
	private String pinCode;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("sameAddress")
	private String sameAddress;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("nomineeName")
	private String nomineeName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("frequency")
	private String frequency;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("cardType")
	private String cardType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("cardGrade")
	private String cardGrade;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("cardNumber")
	private String cardNumber;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("cardName")
	private String cardName;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("withdrawalLimit")
	private String withdrawalLimit;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("onlineTxnLimit")
	private String onlineTxnLimit;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("intTxnLimit")
	private String intTxnLimit;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("date")
	private String date;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("widget")
	private String widget;
	
}
