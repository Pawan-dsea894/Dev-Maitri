package com.iexceed.appzillonbanking.logs.domain.apz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAstpLdRecsPK implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name="REF_NO")
	private String refNo;

	@Column(name="SEQ_NO")
	private int seqNo;
	
}
