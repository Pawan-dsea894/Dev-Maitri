package com.iexceed.appzillonbanking.cagl.document.domain.ab;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMasterHisId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID", nullable = false)
    private String appId;

    @Column(name = "APPLICATION_ID", nullable = false)
    private String applicationId;

    @Column(name = "LATEST_VERSION_NO", nullable = false)
    private String versionNum;

    @Column(name = "KENDRA_ID", nullable = false)
    private String kendraId;

}
