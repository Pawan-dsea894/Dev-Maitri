package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.sql.Timestamp;

public interface ApplicationNomineeProjection {

    // -------- Application Table --------
    Timestamp getCreateTs();
    Timestamp getUpdatedTs();
    String getLatestVersionNo();
    String getKendraId();
    String getCustomerId();
    String getCreatedBy();
    String getUpdatedBy();
    String getApplicationType();
    String getKycType();
    String getApplicationStatus();
    String getBranchCode();
    String getCurrentScreenId();
    String getRemarks();
    String getCurrentStage();
    String getKmid();
    String getKendraname();
    String getCustomerName();
    String getAddInfo1();
    String getAddInfo2();
    String getAppId();
    String getLoanApplicationNo();
    String getApplicationId();
    String getBranchName();
    String getRequestType();

    // -------- Nominee Table --------
    Timestamp getNomineeCreateTs();
    Timestamp getNomineeUpdatedTs();
    String getNomineeCustomerId();
    String getRelationtype();
    String getMemrelation();
    String getLegaldocname();
    String getLegaldocid();
    String getInputdata();
    String getDocunof();
    String getDocunob();
    String getReason();
    String getOcrresponsepayload();
    String getNomineedtls();
    String getOcrdetails();
    String getNomineeCustomerName();
    String getNomineeApplicationId();
    String getNomineeAddInfo();
    String getNomineeLatestVersionNo();
}