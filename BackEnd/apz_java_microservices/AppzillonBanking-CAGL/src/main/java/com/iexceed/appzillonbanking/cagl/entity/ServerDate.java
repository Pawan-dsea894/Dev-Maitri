package com.iexceed.appzillonbanking.cagl.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "t24_serverdate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServerDate {

	@Id
	private int id;

	@JsonProperty("currentDate")
	@Column(name = "currentDate")
	private String currentDate;

	@JsonProperty("nextWorkDate")
	@Column(name = "nextWorkDate")
	private String nextWorkDate;

	@JsonProperty("updatedDate")
	@Column(name = "updatedDate")
	private Timestamp updatedDate;

}
