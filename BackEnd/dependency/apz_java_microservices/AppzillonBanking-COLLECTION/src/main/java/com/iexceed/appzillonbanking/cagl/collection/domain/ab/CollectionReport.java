package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_uaob_mis_collection_report")
@IdClass(CollectionReportId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionReport {

	@Id
	private String applicationId;

	@Id
	private String kendraId;

	@Id
	private String branchId;

	@Id
	@Column(name = "seq_no")
	private int seqNo;

	@Id
	private String status;

	@Column(name = "payload", columnDefinition = "text")
	private String payload;

	@Column(name = "created_ts", updatable = false)
	private LocalDateTime createdTs;

	@Column(name = "updated_ts")
	private LocalDateTime updatedTs;

	@Column(name = "created_tsby")
	private String created_tsby;

	@Column(name = "updated_tsby")
	private String updated_tsby;

	@Column(name = "collection_type")
	private String collection_type;
	
	@Column(name = "user_role")
	private String user_role;

}
