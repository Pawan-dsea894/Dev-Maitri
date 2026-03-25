package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import lombok.*;

import java.io.Serializable;

import jakarta.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionReportId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "applicationid", length = 50, nullable = false)
	private String applicationId;

	@Column(name = "kendra_id", length = 50, nullable = false)
	private String kendraId;

	@Column(name = "branch_id", length = 50, nullable = false)
	private String branchId;

	@Column(name = "seq_no", nullable = false)
	private int seqNo;

	@Column(name = "status", nullable = false)
	private String status;
}
