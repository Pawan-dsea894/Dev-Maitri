package com.iexceed.appzillonbanking.kendra.domain.cus;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_kendra_assignment")
@AllArgsConstructor
@NoArgsConstructor
public class KendraManagementEntity {
	
	@Column(name = "kmid")
	private String kmId;
	
	@Column(name = "old_kmid")
	private String oldKmID;
	
	@Id
	@Column(name="kendra_id")
	private String kendraId;

}
