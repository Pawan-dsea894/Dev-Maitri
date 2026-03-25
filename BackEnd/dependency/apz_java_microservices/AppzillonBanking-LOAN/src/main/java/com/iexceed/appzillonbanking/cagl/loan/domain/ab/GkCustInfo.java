package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "gk_cust_info")
@Data
public class GkCustInfo {

    @Id
    @Column(name = "seqid")
    private String seqid;

    @Column(name = "custid")
    private String custid;

    @Column(name = "kendraid")
    private String kendraid;

    @Column(name = "branchid")
    private String branchid;

    @Column(name = "payload")
    private String payload;

    @Column(name = "addinfo1")
    private String addinfo1;

    @Column(name = "addinfo2")
    private String addinfo2;
}