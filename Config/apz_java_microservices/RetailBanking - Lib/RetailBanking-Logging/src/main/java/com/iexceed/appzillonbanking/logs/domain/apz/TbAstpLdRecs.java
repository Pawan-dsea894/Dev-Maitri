package com.iexceed.appzillonbanking.logs.domain.apz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="TB_ASTP_LD_RECS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAstpLdRecs implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private TbAstpLdRecsPK id;
	
	@Column(name="DATA1")
	private String data1;
	
	@Column(name="DATA2")
	private String data2;
	
	@Column(name="DATA3")
	private String data3;
	
	@Column(name="DATA4")
	private String data4;
	
	@Column(name="DATA5")
	private String data5;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_TS",insertable=false)
	private Date createTs;
	
	public TbAstpLdRecs(TbAstpLdRecsPK id, String data1, String data2, String data3, String data4, String data5) {
		super();
		this.id = id;
		this.data1 = data1;
		this.data2 = data2;
		this.data3 = data3;
		this.data4 = data4;
		this.data5 = data5;
	}

}
