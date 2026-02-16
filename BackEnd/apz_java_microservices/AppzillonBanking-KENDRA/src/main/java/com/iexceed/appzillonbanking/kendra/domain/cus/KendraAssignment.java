package com.iexceed.appzillonbanking.kendra.domain.cus;


import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_KENDRA_ASSIGNMENT")
//@IdClass(KendraAssignmentId.class)
@NoArgsConstructor
@Data
@AllArgsConstructor
public class KendraAssignment {

	@Id
	@Column(name = "KENDRA_ASSIGNMENT_ID")
	private String kendraAssignmentId;

	// raghav created property :: START
	@Column(name = "kmid")
	private String kmId;

	@Column(name = "kendra_id")
	private String kendraId;

	@Column(name = "km_name")
	private String kmName;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;
	// END

	@Column(name = "ASSIGNMENT_TYPE")
	private String assignmentType;

	@Column(name = "OLD_KMID")
	private String oldKmId;

	@Column(name = "OLD_KM_NAME")
	private String oldKmName;

	@Column(name = "BRANCH_ID")
	private String branchId;

	@Column(name = "CREATE_TS")
	private String createTs;

	@Column(name = "CREATED_BY")
	private String createBy;

	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "BATCH_NO")
	private String batchNo;
	
	@Column(name = "ALLOCATION_TYPE")
	private String allocationType;

	@Column(name = "ADD_INFO1")
	private String addInfo1;

	@Column(name = "ADD_INFO2")
	private String addInfo2;

}
