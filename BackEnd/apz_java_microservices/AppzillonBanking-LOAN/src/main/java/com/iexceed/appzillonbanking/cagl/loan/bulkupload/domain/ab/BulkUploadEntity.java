package com.iexceed.appzillonbanking.cagl.loan.bulkupload.domain.ab;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_BULK_UPLOAD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadEntity {
	
	@Id
	@Column(name = "doc_id")
	private String docId;
	
	@Column(name = "doc_name")
	private String docName;	
	
	@Column(name = "user_id")
	private String userId;	
	
	@Column(name = "uploaded_by")
	private String uploadedBy;	
	
	@Column(name = "created_at")
	private String createdAt;	
	
	@Column(name = "type_of_upload")
	private String typeOfUpload;
	
	@Column(name = "status")
	private String status;
	
}
