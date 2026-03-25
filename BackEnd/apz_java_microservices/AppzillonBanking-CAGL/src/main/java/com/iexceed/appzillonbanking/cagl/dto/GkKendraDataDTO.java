package com.iexceed.appzillonbanking.cagl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GkKendraDataDTO {
        // for kendra Fetch API as BM login   
	private String kendraId;
	private String meetingDay;
	private String kmid;

}
