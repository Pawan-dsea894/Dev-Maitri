package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.sql.Timestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_lock_customer")
@IdClass(LockCustomerDtlsPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockCustomerDtls {

	@Id
	@Column(name="application_Id")
	private String applicationId;

	@Column(name="role_Id")
	private String roleId;

	@Id
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_ts")
	private Timestamp createdTs;

}
