package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "gk_cust_notification")
@Data
public class GkCustNotification {

    @Id
    @Column(name = "seqid")
    private String seqid;

    @Column(name = "custid")
    private String custid;

    @Column(name = "kendraid")
    private String kendraid;

    @Column(name = "branchid")
    private String branchid;

    @Column(name = "notifypayload")
    private String notifypayload;

    @Column(name = "addinfo")
    private String addinfo;
}