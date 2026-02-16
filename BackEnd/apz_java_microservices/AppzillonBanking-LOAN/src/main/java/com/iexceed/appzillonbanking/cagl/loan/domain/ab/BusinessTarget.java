package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(BusinessTargetId.class)
@Table(name = "tb_uabt_business_target")
public class BusinessTarget {

	@Id
	private String branch_id;

	@Id
	private String fy_year;

	@Id
	private String userid;

	@Id
	private String target_type;

	@JsonProperty("branch_name")
	@Column(name = "branch_name")
	private String branch_name;

	@JsonProperty("state")
	@Column(name = "state")
	private String state;

	@JsonProperty("userrole")
	@Column(name = "userrole")
	private String userrole;

	@JsonProperty("username")
	@Column(name = "username")
	private String username;

	@JsonProperty("active_staff_count")
	@Column(name = "active_staff_count")
	private String active_staff_count;

	@JsonProperty("m0")
	@Column(name = "m0")
	private String m0;

	@JsonProperty("m1")
	@Column(name = "m1")
	private String m1;

	@JsonProperty("m2")
	@Column(name = "m2")
	private String m2;

	@JsonProperty("m3")
	@Column(name = "m3")
	private String m3;

	@JsonProperty("m4")
	@Column(name = "m4")
	private String m4;

	@JsonProperty("m5")
	@Column(name = "m5")
	private String m5;

	@JsonProperty("m6")
	@Column(name = "m6")
	private String m6;

	@JsonProperty("m7")
	@Column(name = "m7")
	private String m7;

	@JsonProperty("m8")
	@Column(name = "m8")
	private String m8;

	@JsonProperty("m9")
	@Column(name = "m9")
	private String m9;

	@JsonProperty("m10")
	@Column(name = "m10")
	private String m10;

	@JsonProperty("m11")
	@Column(name = "m11")
	private String m11;

}
