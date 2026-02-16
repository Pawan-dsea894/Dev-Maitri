package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//Created By Prem

@NoArgsConstructor
@Data
@AllArgsConstructor
public class TbOfficeDataId implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "branchid", nullable = false)
	private String branchid ;

}
