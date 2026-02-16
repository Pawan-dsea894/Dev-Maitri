package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionReportId implements Serializable {


    private static final long serialVersionUID = 1L;
    
    private String applicationId;
    private String kendraId;
    private String branchId;
    private int versionNum;
}
