package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchCustRequestObj {

	@JsonProperty("custid")
	private List<String> custid;

	@JsonProperty("kendraid")
	private List<String> kendraid;

	@JsonProperty("branchid")
	private List<String> branchid;


}
