package com.iexceed.appzillonbanking.cagl.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkKendraDataId implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("kendraId")
	@Column(name = "KENDRAID")
	private int kendraId;
}
