package com.iexceed.appzillonbanking.cbs.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbUaobCbResponseId implements Serializable{

	private static final long serialVersionUID = 1L;

	
	@Column(name = "CB_DTL_ID", nullable = false)
	private String cbDtlId ;

}
