package com.iexceed.appzillonbanking.core.domain.apz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="TB_ABMI_USER_PROFILE")
@IdClass(UserProfileDomainId.class) 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDomain {

	@Id
	private String appId;
	
	@Id
	private String userId;
	
	@Column(name="USER_NAME")
	private String userName;
	
	@Column(name="PROFILE_PIC1")
	private String profilePicOne;
	
	@Column(name="PROFILE_PIC2")
	private String profilePicTwo;
	
	@Column(name="PROFILE_PIC3")
	private String profilePicThree;
	
	@Column(name="PROFILE_PIC4")
	private String profilePicFour;
	
	@Column(name="PROFILE_PIC5")
	private String profilePicFive;
	
	@Column(name="SMS_NOTIF")
	private String smsNotif;
	
	@Column(name="EMAIL_NOTIF")
	private String emailNotif;
	
	@Column(name="PUSH_NOTIF")
	private String pushNotif;
	
	@Column(name="CUSTOMER_SEGMENT")
	private String customerSegment;
	
	@Column(name="INAPP_NOTIF")
	private String inAppNotif;
	
}
