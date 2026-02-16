package com.iexceed.appzillonbanking.cagl.payload;

import com.iexceed.appzillonbanking.cagl.dto.KendraData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject {

	private KendraData kendraDtls;
}
