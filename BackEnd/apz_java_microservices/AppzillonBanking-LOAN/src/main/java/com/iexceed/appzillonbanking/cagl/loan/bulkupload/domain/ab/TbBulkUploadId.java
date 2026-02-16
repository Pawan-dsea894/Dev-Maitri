package com.iexceed.appzillonbanking.cagl.loan.bulkupload.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUalnLoanDtlsId;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbBulkUploadId implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("docId")
	@Column(name = "doc_id")
	private String docId;
}
