package com.iexceed.appzillonbanking.kendra.domain.cus;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "tb_ashs_user_role", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleHis {

	@EmbeddedId
	private UserRoleHisId id;

	@Column(name = "create_user_id")
	private String createUserId;

	@Column(name = "create_ts")
	private Timestamp createTs;
}
