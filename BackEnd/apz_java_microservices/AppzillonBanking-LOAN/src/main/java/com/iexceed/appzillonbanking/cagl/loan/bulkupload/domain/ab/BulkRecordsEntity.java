package com.iexceed.appzillonbanking.cagl.loan.bulkupload.domain.ab;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_BULK_RECORDS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkRecordsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "record_id")
	private long recordId;
	
	@Column(name = "doc_id")
	private String docId;
	
	@Column(name = "customer_id")
	private String customerId;
	
	@Column(name = "loan_id")
	private String loanId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "amount")
	private String amount;
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "upload_status")
	private String uploadStatus;
	
}
