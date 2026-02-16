package com.iexceed.appzillonbanking.cagl.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private ResponseHeader responseHeader;

    private ResponseBody responseBody;

	
}
