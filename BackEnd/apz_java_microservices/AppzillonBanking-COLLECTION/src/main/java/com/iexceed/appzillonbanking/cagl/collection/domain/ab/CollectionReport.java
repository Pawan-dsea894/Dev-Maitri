package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_uaob_mis_collection_report")
@IdClass(CollectionReportId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionReport {

    @Id
    @Column(name = "applicationid", length = 50, nullable = false)
    private String applicationId;

    @Id
    @Column(name = "kendra_id", length = 50, nullable = false)
    private String kendraId;

    @Id
    @Column(name = "branch_id", length = 50, nullable = false)
    private String branchId;
    
    @Id
    @Column(name = "version_num", nullable = false)
    private int versionNum;

    @Column(name = "payload", columnDefinition = "text")
    private String payload;
    
    @Column(name = "created_ts", updatable = false, insertable = false)
    private LocalDateTime createdTs;

    @Column(name = "updated_ts", insertable = false)
    private LocalDateTime updatedTs;

}

