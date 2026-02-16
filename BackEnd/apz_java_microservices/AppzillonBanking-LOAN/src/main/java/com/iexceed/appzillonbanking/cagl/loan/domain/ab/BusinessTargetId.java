package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessTargetId  implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("branch_id")
	@Column(name = "branch_id", nullable = false)
	private String branch_id;

	@JsonProperty("fy_year")
	@Column(name = "fy_year", nullable = false)
	private String fy_year;

	@JsonProperty("userid")
	@Column(name = "userid", nullable = false)
	private String userid;

	@JsonProperty("target_type")
	@Column(name = "target_type", nullable = false)
	private String target_type;

}
