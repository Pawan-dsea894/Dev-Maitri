package com.iexceed.appzillonbanking.cagl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_master")
@AllArgsConstructor
@NoArgsConstructor
public class GkUserData {
	
	@Id
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "user_designation")
	private String userdesignation;
	
	@Column(name = "Hierarchy_ID")
	private String hierarchyId;
	

}
