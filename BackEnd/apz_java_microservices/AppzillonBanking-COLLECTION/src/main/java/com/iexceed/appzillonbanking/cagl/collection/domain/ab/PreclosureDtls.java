package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_UACO_PRECLOSE_DETAILS")
public class PreclosureDtls {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("appId")
    @Column(name = "app_id")
    private String appId;

    @JsonProperty("applicationId")
    @Column(name = "application_id", unique = true)
    private String applicationId;

    @JsonProperty("branchCode")
    @Column(name = "branch_code")
    private String branchCode;

    @JsonProperty("customerId")
    @Column(name = "customer_id")
    private String customerId;

    @JsonProperty("payload")
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @JsonProperty("createTs")
    @Column(name = "create_ts")
    private Timestamp createTs;

    @JsonProperty("createdBy")
    @Column(name = "created_by")
    private String createdBy;

    @JsonProperty("kendraId")
    @Column(name = "kendra_id")
    private String kendraId;

    @JsonProperty("status")
    @Column(name = "status")
    private String status;

    @JsonProperty("meeting_date")
    @Column(name = "meeting_date")
    private LocalDate meetingDate;

    @JsonProperty("customerName")
    @Column(name = "customer_name")
    private String customerName;

}
