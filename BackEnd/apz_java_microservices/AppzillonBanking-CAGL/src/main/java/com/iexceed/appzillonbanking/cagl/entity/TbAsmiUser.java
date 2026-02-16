package com.iexceed.appzillonbanking.cagl.entity;

import java.sql.Timestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ASMI_USER")
@IdClass(TbAsmiUserId.class)
@Getter @Setter
public class TbAsmiUser {

	@Id
	private String userId;
	
	@Id
	private String appId;
	
	@Column(name = "PIN")
	private String pin;
	
	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "LAST_NAME")
	private String lastName;
	
	@Column(name = "LOGIN_STATUS", nullable = false)
	private String loginStatus;
	
	@Column(name = "FAIL_COUNT")
	private int failCount;
	
	@Column(name = "USER_ACTIVE", nullable = false)
	private String userActive;
	
	@Column(name = "USER_LOCKED", nullable = false)
	private String userLocked;	
	
	@Column(name = "LANGUAGE", nullable = false)
	private String language;
	
	@Column(name = "EXTERNALIDENTIFIER")
	private String externalIdentifier;
	
	@Column(name = "USER_ADDR1")
	private String userAddr1;
	
	@Column(name = "USER_ADDR2")
	private String userAddr2;
	
	@Column(name = "USER_ADDR3")
	private String userAddr3;
	
	@Column(name = "USER_ADDR4")
	private String userAddr4;
	
	@Column(name = "USER_EML1")
	private String userEml1;
	
	@Column(name = "USER_EML2")
	private String userEml2;
	
	@Column(name = "USER_PHNO1")
	private String userPhone1;
	
	@Column(name = "USER_PHNO2")
	private String userPhone2;
	
	@Column(name = "USER_LVL")
	private int userLevel;
	
	@Column(name = "USER_LOCK_TS")
	private Timestamp userLockTs;
	
	@Column(name = "PIN_CHANGE_TS", nullable = false)
	private Timestamp pinChangeTs;
	
	@Column(name = "PROFILE_PIC")
	private String profilePic;
	
	@Column(name = "CREATE_USER_ID")
	private String createdUserId;
	
	@Column(name = "MAKER_ID")
	private String makerId;
	
	@Column(name = "MAKER_TS")
	private Timestamp makerTs;
	
	@Column(name = "CHECKER_ID")
	private String checkerId;
	
	@Column(name = "CHECKER_TS")
	private Timestamp checkerTs;
	
	@Column(name = "AUTH_STATUS")
	private String authStatus;
	
	@Column(name = "CREATE_TS")
	private Timestamp createTs;
	
	@Column(name = "DATE_OF_BIRTH")
	private Timestamp dateOfBirth;
	
	@Column(name = "ADD_INFO1")
	private String addInfo1;
	
	@Column(name = "ADD_INFO2")
	private String addInfo2;

	@Column(name = "ADD_INFO3")
	private String notificationFlag;
	
	@Column(name = "ADD_INFO4")
	private String primaryAcc;
	
	@Column(name = "ADD_INFO5")
	private String addInfo5;
	
	@Column(name = "VERSION_NO")
	private int versionNum;
	
	@Column(name = "user_type")
	private String user_type;

}
