package com.iexceed.appzillonbanking.core.domain.ab;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ABMI_UDE_MASTER")
@IdClass(TbAbmiUdeMasterId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAbmiUdeMaster {

	@Id
	private String appId;
	
	@Id
	private String module;
	
	@Id
	private String udeName;
	
	@Column(name = "UDE_DESC")
	private String udeDesc;
	
	@Column(name = "UDE_MAP_ELEMENT")
	private String udeMapElement;
	
	@Column(name = "STATUS")
	private String status;
	
	@CreationTimestamp
	@Column(name = "CREATE_TS")
	private Timestamp createdTs;		
}
