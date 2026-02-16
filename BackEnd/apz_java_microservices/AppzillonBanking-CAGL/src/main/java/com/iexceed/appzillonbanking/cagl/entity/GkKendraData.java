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

@Entity
@Table(name = "gk_kendra_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkKendraData {

	@JsonProperty("kendraId")
	@Id
	private int kendraId;
	
	@JsonProperty("kendraName")
	@Column(name = "KENDRA_NAME")
	private String kendraName;

	@JsonProperty("kmName")
	@Column(name = "KM_NAME")
	private String kmName;
	
	
	@JsonProperty("branchId")
	@Column(name = "BRANCHID")
	private String branchId;
	
	@JsonProperty("villageType")
	@Column(name = "VILLAGE_TYPE")
	private String villageType;
	
	@JsonProperty("kendraAddr")
	@Column(name = "KENDRA_ADDR")
	private String kendraAddr;
	
	@JsonProperty("state")
	@Column(name = "STATE")
	private String state;
	
	@JsonProperty("district")
	@Column(name = "DISTRICT")
	private String district;
	
	@JsonProperty("taluk")
	@Column(name = "TALUK")
	private String taluk;
	
	@JsonProperty("areaType")
	@Column(name = "AREA_TYPE")
	private String areaType;
	
	@JsonProperty("village")
	@Column(name = "VILLAGE")
	private String village;
	
	@JsonProperty("pincode")
	@Column(name = "PINCODE")
	private String pincode;
	
	@JsonProperty("meetingFrequency")
	@Column(name = "MEETING_FREQ")
	private String meetingFrequency;
	
	@JsonProperty("firstMeetingDate")
	@Column(name = "FIRST_MEETING_DATE")
	private String firstMeetingDate;
	
	@JsonProperty("nextMeetingDate")
	@Column(name = "NEXT_MEETING_DATE")
	private String nextMeetingDate;
	
	@JsonProperty("meetingDay")
	@Column(name = "MEETING_DAY")
	private String meetingDay;
	
	@JsonProperty("meetingPlace")
	@Column(name = "MEETING_PLACE")
	private String meetingPlace;
	
	@JsonProperty("meetingStartTime")
	@Column(name = "MESTARTING_TIME")
	private String meetingStartTime;

	@JsonProperty("endingTime")
	@Column(name = "ENDING_TIME")
	private String endingTime;
	
	@JsonProperty("distance")
	@Column(name = "DISTANCE")
	private String distance;
	
	@JsonProperty("leader")
	@Column(name = "LEADER")
	private String leader;
	
	@JsonProperty("secretary")
	@Column(name = "SECRETARY")
	private String secretary;
	
	@JsonProperty("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@JsonProperty("createdTS")
	@Column(name = "CREATED_TS")
	private Timestamp createdTS;
	
	@JsonProperty("updatedBy")
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@JsonProperty("kendraStatus")
	@Column(name = "KENDRA_STATUS")
	private String kendraStatus;
	
	@JsonProperty("activationDate")
	@Column(name = "ACTIVATION_DATE")
	private String activationDate;
	
	@JsonProperty("kmId")
	@Column(name = "KMID")
	private String kmId;

}
