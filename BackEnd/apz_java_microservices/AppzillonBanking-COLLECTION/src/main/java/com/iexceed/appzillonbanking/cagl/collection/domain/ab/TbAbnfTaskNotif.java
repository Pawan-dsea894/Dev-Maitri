package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_abnf_task_notif")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAbnfTaskNotif {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no")
	private Long seqNo;

	@Column(name = "to_user_id", length = 50, nullable = false)
	private String toUserId;

	@Column(name = "branch_id", length = 50, nullable = false)
	private String branchId;

	@Column(name = "from_user_id", length = 50, nullable = false)
	private String fromUserId;

	@Column(name = "kendraid", length = 50, nullable = false)
	private String kendraId;

	@Column(name = "task", length = 50)
	private String task;

	@Column(name = "from_user_role", length = 50)
	private String fromUserRole;

	@Column(name = "to_user_role", length = 50)
	private String toUserRole;

	@Column(name = "updated_ts")
	private LocalDateTime updatedTs;

	@Column(name = "add_info1", length = 150)
	private String addInfo1;

	@Column(name = "add_info2", length = 150)
	private String addInfo2;

	@Column(name = "add_info3", length = 150)
	private String addInfo3;
	
	@Column(name = "application_date")
    private LocalDate applicationDate;
}
