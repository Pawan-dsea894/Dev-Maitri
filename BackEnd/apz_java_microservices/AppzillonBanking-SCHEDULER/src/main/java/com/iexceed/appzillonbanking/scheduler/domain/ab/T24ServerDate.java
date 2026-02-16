package com.iexceed.appzillonbanking.scheduler.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "t24_serverdate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class T24ServerDate {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int uId;
	
	private int id;
	
	@JsonProperty("currentDate")
	@Column(name = "currentDate")
	private String currentDate;

	@JsonProperty("nextWorkDate")
	@Column(name = "nextWorkDate")
	private String nextWorkDate;

	@JsonProperty("updatedDate")
	@Column(name = "updatedDate")
	private String updatedDate;

}
