package com.iexceed.appzillonbanking.cagl.loan.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfile {

	private String customerName;
	private Long groupId;
	private String customerId;
	private String vintageYear;
	private String mobileNum;
	private String maritalStatus;
	private String primaryType;
	private String primaryId;
	private String isEarning;
	private String depname;
	private String depDob;
	private String dob;

}
