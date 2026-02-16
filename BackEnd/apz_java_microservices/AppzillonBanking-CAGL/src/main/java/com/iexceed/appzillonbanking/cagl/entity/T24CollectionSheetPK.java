package com.iexceed.appzillonbanking.cagl.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class T24CollectionSheetPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "Kendra_ID")
	private int kendraId;

	@Column(name = "Branch_ID")
	private String branchId;

	@Column(name = "Transaction_Date")
	private Timestamp txnDate;

	@Column(name = "Handled_By")
	private String handledBy;
}
