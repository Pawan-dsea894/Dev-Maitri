package com.iexceed.appzillonbanking.cagl.domain.cus;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gk_group_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkGroupData {

	@Id
	private int groupId;
	
	@Column(name = "KENDRAID")
	private int kendraId;

	@Column(name = "BRANCHID")
	private String branchId;
	
	@Column(name = "CGT_STATUS")
	private String cgtStatus;
	
	@Column(name = "KMID")
	private String kmId;

	@Column(name = "CREATED_TS")
	private Timestamp createdTS;
	
	@Column(name = "GROUP_STATUS")
	private String groupStatus;
	
}
