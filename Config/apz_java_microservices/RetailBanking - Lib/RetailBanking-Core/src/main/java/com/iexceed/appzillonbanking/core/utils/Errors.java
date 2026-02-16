package com.iexceed.appzillonbanking.core.utils;

public enum Errors {
	DATEERROR("110", "Date and Time is invalid"),
	SEVICETIMEDOUTERROR("450", "Timed out trying to reach external service"),
	SEVICENOTREACHABLEERROR("404", "Unable to reach the service"), SERVICEERROR("460", "Service Response Invalid"),
	CALCULATIONERROR("260", "Calculation Error"), PERSISTENCEERROR("510", "Persistence Error"),
	PROCESSINGREQUESTERROR("600", "Oops! Something went wrong. Please try again."),
	NORECORD("NR100", "No Record/Data Found"),
	DELETEBILLERSUCCESS("DB_003", "Biller deleted successfully"),
	DELETEBILLERPROCESSERROR("DB_001", "Error occured while deleting the biller"),
	NOCREDITCARD("CC_007", "No Credit Card Found"),
	BLOCKCREDITCARD("CC_004", "Request has been raised to block the credit card"),
	CREDITCARDPINRESET("CC_008", "PIN has been successfully reset"),
	CREDITCARDSTATEMENT("CC_007", "No credit card statement found"),
	INSERTRECORD("100", "Record inserted successfully"),
	REQUESTVALIDATIONERROR("560", "Validation failed for one or more request JSON elements"),
	RECORDEXISTSERROR("512", "Record already exists"), DATEFORMATINVALIDERROR("112", "Date format is not acceptable"),
	TIMEFORMATINVALIDERROR("114", "Time format is not acceptable"),
	DATETIMEFORMATINVALIDERROR("116", "Date-Time format is not acceptable"),
	NONEXISTANTCUSTOMERACCOUNTERROR("FT_007", "Customer account does not exist"),
	BENMAXLMTEXD("BEN-MAX-LMT-EXD", "Beneficiary maximum limit exceeded by "),
	BENDAILYLMTEXD("BEN-DAILY-LMT-EXD", "Beneficiary daily limit exceeded by "),
	BENMONTHLYLMTEXD("BEN-MONTHLY-LMT-EXD", "Beneficiary monthly limit exceeded by "),
	CUSTMINLMTEXD("CUST-MIN-LMT-EXD", "Customer's minimum limit exceeded by "),
	CUSTMAXLMTEXD("CUST-MAX-LMT-EXD", "Customer's maximun limit exceeded by "),
	CUSTDAILYLMTEXD("CUST-DAILY-LMT-EXD", "Customer's daily limit exceeded by "),
	CUSTMONTHLYLMTEXD("CUST-MONTHLY-LMT-EXD", "Customer's monthly limit exceeded by "),
	BANKMINLMTEXD("BANK-MIN-LMT-EXD", "Bank's minimum limit exceeded by "),
	BANKMAXLMTEXD("BANK-MAX-LMT-EXD", "Bank's maximum limit exceeded by "),
	BANKDAILYLMTEXD("BANK-DAILY-LMT-EXD", "Bank's daily limit exceeded by "),
	BANKMONTHLYLMTEXD("BANK-MONTHLY-LMT-EXD", "Bank's monthly limit exceeded by "),
	LIMITMISSINGERROR("200", "Limits non-existent"), 
	DATAINSERTIONSUCCESS("ABS_COM_001", "Data insertion successful"),
	DATAINSERTIONFAILURE("ABS_COM_002", "Unable to insert the record"),
	DATAUPDATIONSUCCESS("ABS_COM_003", "Data updation successful"),
	DATAUPDATIONFAILURE("ABS_COM_004", "Unable to update the record(s)"),
	DATADELETIONSUCCESS("ABS_COM_005", "Data deletion successful"),
	DATADELETIONFAILURE("ABS_COM_006", "Unable to deletion the record"),
	DATAFETCHSUCCESS("ABS_COM_007", "Data fetch successful"),
	DATAFETCHFAILURE("ABS_COM_008", "Unable to fetch the data"),
	MAKERCHECKERERROR("SAME_MAKER_CHECKER","Same Maker and Checker Authorization not allowed"),
	SERVERERROR("07","Server Error. Please try again later"),
	EXTERNALSERVICEERROR("8","Failed to execute the external service"),
	DEMOSERVICEERROR("9","Failed to execute the demo service"),
	GENERATEOTPERROR("10","Failed to generate the otp"),
	VALIDATEOTPERROR("11","Invalid otp"),
	BENEFICIARYDELETIONERROR("ABS_COM_009","A transfer is scheduled for this beneficiary in future, so please delete the scheduled transfer before deleting the beneficiary"),
	DATAEXISTS("12","Data already exists"),
	NOBILLPAY("13","No Pending bill pay found"),
	INACTIVEBILLER("14","No biller found or Biller is Inactive"),
	FAQFILENAMEERROR("FAQ_FILE_001","File Name is modified, Please upload a file with valid name");


	
	private final String errorCode;
	private final String errorMessage;

	Errors(String id, String msg) {
		this.errorCode = id;
		this.errorMessage = msg;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
