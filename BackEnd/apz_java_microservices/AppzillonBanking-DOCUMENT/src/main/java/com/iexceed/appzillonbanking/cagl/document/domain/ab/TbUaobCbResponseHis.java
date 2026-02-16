package com.iexceed.appzillonbanking.cagl.document.domain.ab;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "TB_UAOB_CB_RESPONSE_HISTORY")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbUaobCbResponseHis {
    @Id
    private String cbDtlId;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @Column(name = "VERSION_NO")
    private String versionNum;

    @Column(name = "CUST_DTL_ID")
    private String custDtlId;

    @Column(name = "REQUEST_PAYLOAD")
    private String reqPayload;

    @Column(name = "RESPONSE_PAYLOAD")
    private String resPayload;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CB_CHECK_STATUS")
    private String cbCheckstatus;

    @Column(name = "REQ_TS")
    private Timestamp reqTs;

    @Column(name = "RES_TS")
    private Timestamp resTs;

    @Column(name = "RETRY_COUNT")
    private Integer retryCount;
}
