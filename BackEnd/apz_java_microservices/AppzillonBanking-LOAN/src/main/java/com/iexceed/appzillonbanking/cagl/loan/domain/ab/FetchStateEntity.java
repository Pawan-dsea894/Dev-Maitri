package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "tb_ascd_common_codes",schema="public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FetchStateEntity {
	
	@Id
	@JsonProperty("code")
	@Column(name = "code")
	private String code;

	@JsonProperty("keyvalue")
	@Column(name = "keyvalue")
	private String keyvalue;

	@JsonProperty("description")
	@Column(name = "description")
	private String description;

}
