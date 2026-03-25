package com.iexceed.appzillonbanking.cagl.loan.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Earningss {
	private String customerId;
	private Integer index;
	private String rowNo;
	private String name;
	private String dob;
	private String memRelation;
	private String legaldocName;
	private String legaldocId;
	private String OCRresponselog;
	private OCRDatas OCRData;
	private InputDatas InputData;
	private Boolean isKycEdited;
	private Boolean isEarning;
	private Boolean isEdited;
	private String forntImg;
	private String backImg;

}
