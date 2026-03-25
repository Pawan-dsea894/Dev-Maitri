package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNomineeDetailsId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String application_id;
	
	private String latest_version_no;

}
