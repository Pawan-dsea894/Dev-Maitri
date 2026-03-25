package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import lombok.Data;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "TB_ASMI_USER_ROLE")
@IdClass(UserRoleId.class)
@Data
public class UserRole {

    @Id
    private String userId;

    @Id
    private String roleId;

    @Id
    private String appId;

    @Column(name = "CREATE_USER_ID")
    private String createUserId;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    @Column(name = "VERSION_NO")
    private int versionNum;
}
