package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CBDetails {

    @JsonProperty("Customer_id")
    private String Customer_id;

    @JsonProperty("Loan_ID")
    private String Loan_ID;

    @JsonProperty("applied_loan_code")
    private String applied_loan_code;

    @JsonProperty("Request_Date")
    private String Request_Date;

    @JsonProperty("OTS_Flag")
    private String OTS_Flag;

    @JsonProperty("eligible_emi")
    private String eligible_emi;

    @JsonProperty("Derived_Attribute_1")
    private String Derived_Attribute_1;

    @JsonProperty("Derived_Attribute_2")
    private String Derived_Attribute_2;

    @JsonProperty("Derived_Attribute_3")
    private String Derived_Attribute_3;

    @JsonProperty("Derived_Attribute_4")
    private String Derived_Attribute_4;

    @JsonProperty("Derived_Attribute_5")
    private String Derived_Attribute_5;

    @JsonProperty("Derived_Attribute_6")
    private String Derived_Attribute_6;

    @JsonProperty("Final_Decision")
    private String Final_Decision;

    @JsonProperty("Approved_Loan_Amount")
    private String Approved_Loan_Amount;

    @JsonProperty("Deviation_Category")
    private String Deviation_Category;

    @JsonProperty("NQA_Flag")
    private String NQA_Flag;

    @JsonProperty("Permissable_Income")
    private String Permissable_Income;

    @JsonProperty("flow_response")
    private String flow_response;

    @JsonProperty("Rejection_reason")
    private String Rejection_reason;

    @JsonProperty("IRIS_message")
    private String IRIS_message;

    @JsonProperty("roi")
    private String roi;

    @JsonProperty("eir")
    private String eir;

    @JsonProperty("Insurance_Charge_Member")
    private String Insurance_Charge_Member;

    @JsonProperty("Insurance_Charge_Spouse")
    private String Insurance_Charge_Spouse;

    @JsonProperty("Processing_fees_without_GST")
    private String Processing_fees_without_GST;

    @JsonProperty("GST")
    private String GST;

    @JsonProperty("Interest_Fee")
    private String Interest_Fee;

    @JsonProperty("Upfront_Fee")
    private String Upfront_Fee;

    @JsonProperty("approved_loan_emi")
    private String approved_loan_emi;

    @JsonProperty("final_foir_obligation")
    private String final_foir_obligation;

    @JsonProperty("final_foir")
    private String final_foir;


}
