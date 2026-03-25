package com.iexceed.appzillonbanking.cagl.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "digitalcollection")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DigitalCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customerID")
    private String customerId;

    @Column(name = "loanID")
    private String loanId;

    @Column(name = "application")
    private String application;

    @Column(name = "tranID")
    private String tranId;

    @Column(name = "tranType")
    private String tranType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "trn_posted_at")
    private String trnPostedAt;

    @Column(name = "trn_inserted_at")
    private String trnInsertedAt;

    @Column(name = "ackid")
    private String ackId;

    @Column(name = "branch_id")
    private String branchId;

    @Column(name = "kendra_id")
    private String kendraId;
}
