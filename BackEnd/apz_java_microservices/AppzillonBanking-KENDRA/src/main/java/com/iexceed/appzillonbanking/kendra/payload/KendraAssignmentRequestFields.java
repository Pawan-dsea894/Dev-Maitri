package com.iexceed.appzillonbanking.kendra.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KendraAssignmentRequestFields {

	private List<KendraAssignmentObj> kendraAssignmentListObj;
	
}
