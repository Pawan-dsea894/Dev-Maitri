package com.iexceed.appzillonbanking.cagl.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t24_collection_sheets")
@IdClass(T24CollectionSheetPK.class)
public class T24CollectionSheet {

	@Id
	private int kendraId;

	@Id
	private String branchId;

	@Id
	private Timestamp txnDate;

	@Id
	private String handledBy;

	@Column(name = "Collection_ID")
	private String collectionId;

	@Column(name = "Meeting_Day")
	private String meetingDay;

	@Column(name = "Collection_Type")
	private String collectionType;

	@Column(name = "Kendra_Name")
	private String kendraName;

	@Column(name = "Group_ID")
	private String groupId;

	@Column(name = "Group_Name")
	private String groupName;

	@Column(name = "Group_Customer_ID")
	private String groupCustomerId;

	@Column(name = "Group_Total_Due")
	private String groupTotalDue;

	@Column(name = "Group_Total_Advance")
	private String groupTotalAdvance;

	@Column(name = "Group_Net_Due")
	private String groupNetDue;

	@Column(name = "Group_Tot_Coll_Amount")
	private String groupTotalCollectionAmt;

	@Column(name = "Customer_ID")
	private String customerId;

	@Column(name = "Customer_Name")
	private String customerName;

	@Column(name = "Customer_Group_ID")
	private String customerGroupId;

	@Column(name = "Loan_ID")
	private String loanId;

	@Column(name = "Loan_Due")
	private String loanDue;

	@Column(name = "Loan_Collected_Amount")
	private String loanCollectedAmt;

	@Column(name = "Cust_Total_Due")
	private String customerTotalDue;

	@Column(name = "Cust_Total_Advance")
	private String customerTotalAdvance;

	@Column(name = "Cust_Net_Due")
	private String customerNetDue;

	@Column(name = "Cust_Collected_Amount")
	private String customerCollectedAmt;

	@Column(name = "Cust_Flag")
	private String customerFlg;

	@Column(name = "Cust_Attendance")
	private String customerAttendance;

	@Column(name = "Cust_Fine")
	private String customerFine;

	@Column(name = "Kendra_Total_API")
	private String kendraTotalAPI;

	@Column(name = "Total_Amount_Collected")
	private double totalAmountCollected;

	@Column(name = "Kendra_Total_Cash")
	private double kendraTotalCash;

	@Column(name = "Fee_Collected_Amount")
	private double feeCollectedAmt;

	@Column(name = "Total_Disb_Amount")
	private double totalDisbAmt;

	@Column(name = "Total_Fines_Collected")
	private double totalFinesCollected;

	@Column(name = "Net_Collection")
	private double netCollection;

	@Column(name = "Net_Collection_UPI")
	private double netCollectionUPI;

	@Column(name = "Net_Collection_Cash")
	private double netCollectionCash;

	@Column(name = "Collection_Status")
	private String collectionStatus;

	@Column(name = "Loan_Product_Id")
	private String loanProductId;

	@Column(name = "Loan_Outstanding_Amount")
	private String loanOutstandingAmt;

}
