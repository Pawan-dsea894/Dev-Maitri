package com.iexceed.appzillonbanking.core.domain.ab;

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
@Table(name = "TB_ABMI_SDE_MASTER")
@IdClass(TbAbmiSdeMasterId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAbmiSdeMaster {

	@Id
	private String id;

	@Id
	private String appId;

	@Column(name = "SDE_NAME")
	private String sdeName;

	@Column(name = "SDE_DESC")
	private String sdeDesc;

	@Column(name = "SDE_MAP_ELEMENT")
	private String sdeMapElement;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "MODULE")
	private String module;
}
