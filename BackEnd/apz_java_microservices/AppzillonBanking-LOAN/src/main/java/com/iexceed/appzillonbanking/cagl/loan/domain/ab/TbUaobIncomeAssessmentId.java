package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class TbUaobIncomeAssessmentId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "INC_ASSESSMENT_ID")
	private String incAssessmentId;

}
