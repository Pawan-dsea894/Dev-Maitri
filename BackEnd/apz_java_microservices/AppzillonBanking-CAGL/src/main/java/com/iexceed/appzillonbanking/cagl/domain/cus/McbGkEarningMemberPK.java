package com.iexceed.appzillonbanking.cagl.domain.cus;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class McbGkEarningMemberPK implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	@JsonProperty("recId")
	@Column(name = "REC_ID")
	private String recId;

}
