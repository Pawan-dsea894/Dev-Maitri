package com.iexceed.appzillonbanking.cagl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kendra_latlong")
public class KendraLatLongEntity {

	@Id
	private int KendraID;
	private String Lat;
	private String longit;
	private String UpdatedBy;
	private String UpdatedAt;
	private String address;
}
