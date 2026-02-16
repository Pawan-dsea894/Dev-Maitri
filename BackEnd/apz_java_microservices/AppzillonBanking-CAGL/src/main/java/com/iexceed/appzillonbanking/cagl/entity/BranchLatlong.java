package com.iexceed.appzillonbanking.cagl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "branch_latlong")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchLatlong {
	
	@Id
	private String Branch_ID;
	private String Latitude;
	private String Longitude;
	private String UpdatedBy;
	private String UpdatedOn;
	private String branch;
	

}
