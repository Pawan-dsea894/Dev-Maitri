package com.iexceed.appzillonbanking.cagl.collection.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerDetails {

	private String customerGroupId;

	private String customerGroupType;

	private String cusTotalDue;

	private String cusTotalAdv;

	private String productId;

	private String cusFlag;

	private String customerName;

	private String loanLosId;

	private String loanDue;

	private String customerGroupName;

	private String cusNetDue;

	private String cusCollectionAmt;

	private String groupCustomerId;

	private String customerId;

	private String loanOutsAmt;

	private String loanId;

	private String loanCollectionAmt;
}
