package com.iexceed.appzillonbanking.kendra.assignment.entty;


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
	
	@Id
	@Column(name="kendra_id")
	private String kendraId;

}
