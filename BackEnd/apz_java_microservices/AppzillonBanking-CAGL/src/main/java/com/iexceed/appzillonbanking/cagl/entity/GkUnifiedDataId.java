package com.iexceed.appzillonbanking.cagl.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkUnifiedDataId implements Serializable {

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Column(name = "CUSTOMERID")
    private String customerId;
    
    @Column(name = "KENDRAID")
    private String kendraId;
   
    @Column(name = "loan_id")
    private String loanId;
    
}