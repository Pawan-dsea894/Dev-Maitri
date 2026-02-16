package com.iexceed.appzillonbanking.cagl.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetails {
	
	private int groupId;
	private int kendraId;	
	private String grpStatus;
	
	private List<CustData> customerDtls;

}
