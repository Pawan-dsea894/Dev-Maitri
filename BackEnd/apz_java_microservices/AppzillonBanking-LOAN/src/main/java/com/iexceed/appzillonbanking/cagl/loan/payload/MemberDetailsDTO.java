package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailsDTO {
	private MemberProfile memberProfile;
	private List<Earningss> earnings;
}
