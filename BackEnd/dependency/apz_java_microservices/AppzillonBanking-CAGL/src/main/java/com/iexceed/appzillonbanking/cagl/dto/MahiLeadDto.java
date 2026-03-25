package com.iexceed.appzillonbanking.cagl.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Getter
@Setter
@Builder
public class MahiLeadDto {

	private String transactionId;
	private String branchId;
	private String kendra;
	private String customerId;
	private String customerName;
	private String productName;
	private String loanPurpose;
	private BigDecimal eligibleLoanAmount;
	private String leadStatus;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dateSubmitted;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate leadUpdateDate;

}
