package com.iexceed.appzillonbanking.cagl.loan.constants;

import com.iexceed.appzillonbanking.cagl.loan.payload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Map;

public class DigiAgileValidation {

	private static final Logger logger = LogManager.getLogger(DigiAgileValidation.class);

	public boolean isValid(DigiAgileUserDtls user) {
		if (user == null)
			return false;
		if (isEmpty(user.getApplicationId(), "ApplicationId"))
			return false;
		if (isEmpty(user.getCustomerId(), "CustomerId"))
			return false;
		if (isEmpty(user.getKendraId(), "KendraId"))
			return false;
		if (isEmpty(user.getBranchId(), "BranchId"))
			return false;
		if (isEmpty(user.getKmId(), "KmId"))
			return false;
		if (isEmpty(user.getCustomerName(), "CustomerName"))
			return false;
		if (isEmpty(user.getMobileNum(), "MobileNum"))
			return false;
		if (isEmpty(user.getKmName(), "KmName"))
			return false;
		if (isEmpty(user.getKendraName(), "KendraName"))
			return false;

		// Optional / commented fields
		// if (isEmpty(user.getKendrafrequency(), "KendraFrequency")) return false; //
		// ASK 3
		// if (isEmpty(user.getMeetingday(), "MeetingDay")) return false; // ASK 4

		if (user.getGroupId() == null) {
			logger.debug("The empty field is :: GroupId");
			return false;
		}

		// if (isEmpty(user.getLoanAmount(), "LoanAmount")) return false;
		if (isEmpty(user.getAnnualPercentageRate(), "AnnualPercentageRate"))
			return false;

		KYCDetails kyc = user.getKycDtls();
		if (kyc == null)
			return false;

		// if (isEmpty(kyc.getUpdatedOn(), "UpdatedOn")) return false; // ASK 5
		if (isEmpty(kyc.getDob(), "Dob"))
			return false;
		if (isEmpty(kyc.getMaritalStatus(), "MaritalStatus"))
			return false;
		if (isEmpty(kyc.getMemRelation(), "MemRelation"))
			return false;
		if (isEmpty(kyc.getMobileNum(), "MobileNum"))
			return false;
		if (isEmpty(kyc.getPrimaryId(), "PrimaryId"))
			return false;
		if (isEmpty(kyc.getPrimaryType(), "PrimaryType"))
			return false;
		if (isEmpty(kyc.getVintage(), "Vintage"))
			return false;
		if (isEmpty(kyc.getStateBranch(), "StateBranch"))
			return false;
		if (isEmpty(kyc.getActivationDate(), "ActivationDate"))
			return false;
		if (kyc.getMemRelation().equalsIgnoreCase("SPOUSE")) {
			if (isEmpty(kyc.getDepDob(), "DepDob"))
				return false;
			if (isEmpty(kyc.getDepDocId(), "DepDocId"))
				return false;
			if (isEmpty(kyc.getDepDocType(), "DepDocType"))
				return false;
			if (isEmpty(kyc.getDepname(), "DepName"))
				return false;
		}
		List<Income> incomeList = user.getIncome();
		if (incomeList == null || incomeList.isEmpty())
			return false;

		for (Income i : incomeList) {
			if (isEmpty(i.getTotIncome(), "TotIncome"))
				return false;
			if (i.getTotExpense() == null) {
				logger.debug("The empty field is :: TotExpense");
				return false;
			}
			if (isEmpty(i.getAssesmentDt(), "AssesmentDt"))
				return false;
		}

		List<Earnings> earningsList = user.getEarnings();
		if (earningsList == null || earningsList.isEmpty())
			return false;

		for (Earnings e : earningsList) {
			if (isEmpty(e.getCustomerId(), "CustomerId"))
				return false;
			if (isEmpty(e.getName(), "Name"))
				return false;
			if (isEmpty(e.getDob(), "Dob"))
				return false;
			if (isEmpty(e.getMemRelation(), "MemRelation"))
				return false;
			if (isEmpty(e.getLegaldocName(), "LegaldocName"))
				return false;
			if (isEmpty(e.getLegaldocId(), "LegaldocId"))
				return false;
		}

		BankDetails b = user.getBankDtls();
		if (b == null)
			return false;

		if (isEmpty(b.getBankAccNo(), "BankAccNo"))
			return false;
		if (isEmpty(b.getBankAccName(), "BankAccName"))
			return false;
		if (isEmpty(b.getBankBranchName(), "BankBranchName"))
			return false;
		if (isEmpty(b.getBankName(), "BankName"))
			return false;
		if (isEmpty(b.getBankIfscCode(), "BankIfscCode"))
			return false;

		List<AddressDtls> addrList = user.getAddressDtls();
		if (addrList == null || addrList.isEmpty())
			return false;

		for (AddressDtls a : addrList) {
			if (isEmpty(a.getAddressType(), "AddressType"))
				return false;
			if (isEmpty(a.getAddLine1(), "AddLine1"))
				return false;
			if (isEmpty(a.getAddLine2(), "AddLine2"))
				return false;
			if (isEmpty(a.getState(), "State"))
				return false;
			if (isEmpty(a.getDistrict(), "District"))
				return false;
			if (isEmpty(a.getVillageLocality(), "VillageLocality"))
				return false;
			if (isEmpty(a.getPincode(), "Pincode"))
				return false;
			if (isEmpty(a.getTaluk(), "Taluk"))
				return false;
		}

		LoanDtls loan = user.getLoanDtls();
		if (loan == null)
			return false;

		if (isEmpty(loan.getCaglAmt(), "CaglAmt"))
			return false;
		if (isEmpty(loan.getCbAmt(), "CbAmt"))
			return false;
		if (isEmpty(loan.getCustVintageInterestRate(), "CustVintageInterestRate"))
			return false;
		if (isEmpty(loan.getInsurancePercentage(), "InsurancePercentage"))
			return false;
		if (isEmpty(loan.getProduct(), "Product"))
			return false;
		if (isEmpty(loan.getProductId(), "ProductId"))
			return false;
		if (isEmpty(loan.getProductType(), "ProductType"))
			return false;
		if (isEmpty(loan.getShortDesc(), "ShortDesc"))
			return false;
		if (isEmpty(loan.getSpouseInsurance(), "SpouseInsurance"))
			return false;
		if (isEmpty(loan.getLoanMode(), "LoanMode"))
			return false;
		if (isEmpty(loan.getAmount(), "Amount"))
			return false;

		ChargeAndBreakupDetails ch = loan.getChargeAndBreakupDtls();
		if (ch == null)
			return false;

		// if (isEmpty(ch.getGST(), "GST")) return false; // ASK 6
		if (isEmpty(ch.getLoanAmt(), "LoanAmt"))
			return false;
		if (isEmpty(ch.getAprxLoanCharges(), "AprxLoanCharges"))
			return false;
		if (isEmpty(ch.getLoanProcessingFee(), "LoanProcessingFee"))
			return false;
		if (isEmpty(ch.getIscbUpdated(), "IscbUpdated"))
			return false;
		// if (isEmpty(ch.getInterest_Fee(), "Interest_Fee")) return false;
		// if (isEmpty(ch.getUpfront_Fee(), "Upfront_Fee")) return false;
		if (isEmpty(ch.getAprxLoanAmt(), "AprxLoanAmt"))
			return false;

		InsuranceDetails ins = loan.getInsurDtls();
		if (ins == null)
			return false;

		if (isEmpty(ins.getMember(), "Member"))
			return false;
		// if (isEmpty(ins.getSpouse(), "Spouse")) return false; // ASK 7
		if (isEmpty(ins.getApplicant_insurance_amt(), "ApplicantInsuranceAmt"))
			return false;
		if (isEmpty(ins.getSpouse_insurance_amt(), "SpouseInsuranceAmt"))
			return false;
		if (isEmpty(ins.getInsuranceProvider(), "InsuranceProvider"))
			return false;
		if (isEmpty(ins.getInsurCharges(), "InsurCharges"))
			return false;

		Object dis = loan.getDisburseMode();
		if (!(dis instanceof Map<?, ?> disMap))
			return false;

		if (isEmpty(String.valueOf(disMap.get("idDesc")), "DisburseMode.idDesc"))
			return false;
		if (isEmpty(String.valueOf(disMap.get("id")), "DisburseMode.id"))
			return false;

		Object purpose = loan.getPurpose();
		if (!(purpose instanceof Map<?, ?> purposeMap))
			return false;

		if (isEmpty(String.valueOf(purposeMap.get("productSubPurpose")), "productSubPurpose"))
			return false;
		if (isEmpty(String.valueOf(purposeMap.get("purposeDesc")), "purposeDesc"))
			return false;
		if (isEmpty(String.valueOf(purposeMap.get("productId")), "productId"))
			return false;
		if (isEmpty(String.valueOf(purposeMap.get("purpose")), "purpose"))
			return false;

		Object repay = loan.getRepayFrequency();
		if (!(repay instanceof Map<?, ?> repayMap))
			return false;

		Object id = repayMap.get("id");
		if (id == null || !id.toString().matches("\\d+")) {
			logger.debug("The empty field is :: RepayFrequency.id");
			return false;
		}

		if (isEmpty(String.valueOf(repayMap.get("idDesc")), "RepayFrequency.idDesc"))
			return false;

		CBDetails cb = user.getCbDetails();
		if (cb == null)
			return false;

		if (isEmpty(cb.getCustomer_id(), "CustomerId"))
			return false;
		if (isEmpty(cb.getLoan_ID(), "LoanID"))
			return false;
		if (isEmpty(cb.getApplied_loan_code(), "AppliedLoanCode"))
			return false;
		if (isEmpty(cb.getRequest_Date(), "RequestDate"))
			return false;
		if (isEmpty(cb.getOTS_Flag(), "OTSFlag"))
			return false;
		if (isEmpty(cb.getEligible_emi(), "EligibleEmi"))
			return false;
		if (isEmpty(cb.getDerived_Attribute_1(), "DerivedAttribute1"))
			return false;
		if (isEmpty(cb.getDerived_Attribute_2(), "DerivedAttribute2"))
			return false;
		if (isEmpty(cb.getDerived_Attribute_3(), "DerivedAttribute3"))
			return false;
		if (isEmpty(cb.getDerived_Attribute_4(), "DerivedAttribute4"))
			return false;
		if (isEmpty(cb.getDerived_Attribute_5(), "DerivedAttribute5"))
			return false;
		if (isEmpty(cb.getDerived_Attribute_6(), "DerivedAttribute6"))
			return false;
		if (isEmpty(cb.getFinal_Decision(), "FinalDecision"))
			return false;
		if (isEmpty(cb.getApproved_Loan_Amount(), "ApprovedLoanAmount"))
			return false;
		if (isEmpty(cb.getNQA_Flag(), "NQAFlag"))
			return false;
		if (isEmpty(cb.getFlow_response(), "FlowResponse"))
			return false;
		if (isEmpty(cb.getIRIS_message(), "IRISMessage"))
			return false;
		if (isEmpty(cb.getRoi(), "ROI"))
			return false;
		if (isEmpty(cb.getEir(), "EIR"))
			return false;
		if (isEmpty(cb.getInsurance_Charge_Member(), "InsuranceChargeMember"))
			return false;
		if (isEmpty(cb.getInsurance_Charge_Spouse(), "InsuranceChargeSpouse"))
			return false;
		if (isEmpty(cb.getProcessing_fees_without_GST(), "ProcessingFeesWithoutGST"))
			return false;
		// if (isEmpty(cb.getGst(), "CB GST")) return false;
		if (isEmpty(cb.getInterest_Fee(), "InterestFee"))
			return false;
		if (isEmpty(cb.getUpfront_Fee(), "UpfrontFee"))
			return false;
		return true;
	}

	private boolean isEmpty(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			logger.debug("The empty field is :: " + fieldName);
			return true;
		}
		return false;
	}
}