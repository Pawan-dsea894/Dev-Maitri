package com.iexceed.appzillonbanking.cagl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "unnati_lead_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnnatiLeadMaster {

    /* ---------- Identity ---------- */
    @Id
    @Column(name = "pid", length = 50)
    private String pid;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "full_name", length = 255)
    private String fullName;

    /* ---------- Personal ---------- */

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "age")
    private Integer age;

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(name = "marital_status", length = 20)
    private String maritalStatus;

    @Column(name = "father_name", length = 255)
    private String fatherName;

    @Column(name = "spouse_name", length = 255)
    private String spouseName;

    @Column(name = "gender", length = 10)
    private String gender;

    /* ---------- KYC ---------- */

    @Column(name = "primary_kyc_type", length = 50)
    private String primaryKycType;

    @Column(name = "primary_kyc_id", length = 100)
    private String primaryKycId;

    /* ---------- Occupation & Bank ---------- */

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "name_as_per_bank_account", length = 255)
    private String nameAsPerBankAccount;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "bank_name", length = 150)
    private String bankName;

    @Column(name = "branch_name", length = 150)
    private String branchName;

    @Column(name = "account_number", length = 30)
    private String accountNumber;

    /* ---------- Present Address ---------- */

    @Column(name = "present_line_1", length = 255)
    private String presentLine1;

    @Column(name = "present_line_2", length = 255)
    private String presentLine2;

    @Column(name = "present_line_3", length = 255)
    private String presentLine3;

    @Column(name = "present_pincode", length = 10)
    private String presentPincode;

    @Column(name = "present_area", length = 100)
    private String presentArea;

    @Column(name = "present_city_town_village", length = 100)
    private String presentCityTownVillage;

    @Column(name = "present_district", length = 100)
    private String presentDistrict;

    @Column(name = "present_state", length = 100)
    private String presentState;

    @Column(name = "present_country", length = 100)
    private String presentCountry;

    @Column(name = "present_location_coordinates", length = 100)
    private String presentLocationCoordinates;

    @Column(name = "present_address_type", length = 50)
    private String presentAddressType;

    /* ---------- Permanent Address ---------- */

    @Column(name = "permanent_line_1", length = 255)
    private String permanentLine1;

    @Column(name = "permanent_line_2", length = 255)
    private String permanentLine2;

    @Column(name = "permanent_line_3", length = 255)
    private String permanentLine3;

    @Column(name = "permanent_pincode", length = 10)
    private String permanentPincode;

    @Column(name = "permanent_area", length = 100)
    private String permanentArea;

    @Column(name = "permanent_city_town_village", length = 300)
    private String permanentCityTownVillage;

    @Column(name = "permanent_district", length = 100)
    private String permanentDistrict;

    @Column(name = "permanent_state", length = 100)
    private String permanentState;

    @Column(name = "permanent_country", length = 100)
    private String permanentCountry;

    @Column(name = "permanent_location_coordinates", length = 200)
    private String permanentLocationCoordinates;

    @Column(name = "permanent_address_type", length = 50)
    private String permanentAddressType;

    /* ---------- Kendra / Group ---------- */

    @Column(name = "kendra_id", length = 50)
    private String kendraId;

    @Column(name = "kendra_name", length = 150)
    private String kendraName;

    @Column(name = "gl_branch_id", length = 50)
    private String glBranchId;

    @Column(name = "gl_branch_name", length = 150)
    private String glBranchName;

    @Column(name = "gl_region", length = 200)
    private String glRegion;

    @Column(name = "gl_area", length = 200)
    private String glArea;

    @Column(name = "gl_branch_state", length = 100)
    private String glBranchState;

    @Column(name = "group_id", length = 50)
    private String groupId;

    @Column(name = "group_size")
    private Integer groupSize;

    @Column(name = "kendra_size")
    private Integer kendraSize;

    @Column(name = "kendra_vintage_yrs")
    private Integer kendraVintageYrs;

    @Column(name = "no_of_years_relationship")
    private Integer noOfYearsRelationship;

    @Column(name = "kendra_par_status", length = 50)
    private String kendraParStatus;

    @Column(name = "kendra_meeting_freq", length = 50)
    private String kendraMeetingFreq;

    @Column(name = "kendra_meeting_day", length = 50)
    private String kendraMeetingDay;

    /* ---------- Account / Lead ---------- */

    @Column(name = "type_of_account", length = 50)
    private String typeOfAccount;

    @Column(name = "customer_type", length = 50)
    private String customerType;

    @Column(name = "bank_branch_pincode", length = 40)
    private String bankBranchPincode;

    @Column(name = "caglos", length = 50)
    private String caglos;

    @Column(name = "priority", length = 50)
    private String priority;

    @Column(name = "lead_initiated_from", length = 100)
    private String leadInitiatedFrom;

    @Column(name = "kendra_manager_id", length = 50)
    private String kendraManagerId;

    @Column(name = "branch_email_id", length = 150)
    private String branchEmailId;

    /* ---------- Co-Applicant ---------- */

    @Column(name = "co_applicant_c_kyc", length = 100)
    private String coApplicantCKyc;

    @Column(name = "applicant_c_kyc", length = 100)
    private String applicantCKyc;

    @Column(name = "co_customer_id", length = 50)
    private String coCustomerId;

    /* ---------- Status ---------- */

    @Column(name = "first_act_date")
    private LocalDate firstActDate;

    @Column(name = "urn", length = 100)
    private String urn;

    @Column(name = "bre_status", length = 50)
    private String breStatus;

    @Column(name = "product_type", length = 50)
    private String productType;

    @Column(name = "created_ts", updatable = false)
    private LocalDateTime createdTs;

    @PrePersist
    public void onCreate() {
        createdTs = LocalDateTime.now();
    }

}
