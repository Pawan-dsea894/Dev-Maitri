package com.iexceed.appzillonbanking.kendra.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDetails {

	private String userLocked;
	private String roleId;
	private String locDetails;
	private String locTypes;
	private String userId;
	private String designation;
	private String phNum;
	private String empName;
	private String offMailId;
	private String userStat;
	private String dept;
	private String subFunc;
	private String hrisLocID;
	private String repManagerID;
	private String repManagerName;
	private String branchId;
	private String meetingDate;

}
